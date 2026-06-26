# ruruDown 核心运行链条

## 1. 应用启动链条

```
用户点击图标
  → AndroidManifest: MainActivity (LAUNCHER)
  → MainActivity.onCreate()
    → setContent { BiliExportTheme { AppNavigation(...) } }
    → mainViewModel = MainViewModel(application) by viewModels()
      → AndroidViewModel 构造
        → db = AppDatabase.getInstance(application)     // Room DB 初始化
        → settingsRepository = SettingsRepositoryImpl(application)  // DataStore
        → mediaRepository = MediaRepositoryImpl(db)
        → exportRepository = ExportRepositoryImpl(db)
        → biliFileServiceClient = BiliFileServiceClient()
        → ffmpegEngine = FfmpegEngine()                 // System.loadLibrary("ffmpeg_bridge")
        → exportPipeline = ExportPipeline(ffmpegEngine, remoteFileCopier, tempPathManager, application)
        → scanLibraryUseCase = ScanLibraryUseCase(mediaRepository, ...)
        → init:
          → ShizukuManager.init()                       // 注册 Shizuku binder 监听
          → biliFileServiceClient.init()                // 绑定 BiliFileService UserService
          → ffmpegEngine.init()                         // av_log_set_level(AV_LOG_WARNING)
          → delay(2000)                                 // 等 Shizuku UserService 就绪
          → if (ShizukuManager.isAvailable) scanLibraryUseCase.execute()
```

## 2. 扫描链条

```
scanLibraryUseCase.execute()
  → BiliFileServiceClient → Binder IPC → BiliFileService (shell UID)
  → listAvids()
    → File("/sdcard/Android/data/tv.danmaku.bili/download/")
    → 过滤正则 ^\d+|s_.+ 的目录
    → return ["116726071041510", "116809890010155", ...]

  → for each avid:
    → listSubDirs(avid)
      → File("{BILI_ROOT}/{avid}/")
      → return ["c_39015812633", "c_39394740857", ...]

    → for each subDir (c_{cid}):
      → readEntryJson(avid, subDir)
        → File("{BILI_ROOT}/{avid}/{subDir}/entry.json")
        → readText() → JSON string

      → EntryJsonParser.parse(jsonText)
        ┌─────────────────────────────────────────┐
        │ JSON field             → ParsedEntry      │
        │─────────────────────────────────────────│
        │ title                  → title            │
        │ owner_name             → ownerName        │
        │ page_data.part         → partTitle        │
        │ avid / bvid            → avid / bvid      │
        │ page_data.cid          → cid              │
        │ type_tag               → typeTag          │
        │ quality_pithy_desc     → qualityPithyDesc │
        │ page_data.width/height → width / height   │
        │ total_bytes            → totalBytes       │
        │ total_time_milli       → duration (ms)    │
        │ time_update_stamp      → timeUpdateStamp  │
        │ is_completed           → isCompleted      │
        └─────────────────────────────────────────┘

      → listQualityFolders(avid, subDir) → ["80", "64", ...]
      → typeTag = parsed.typeTag ?: qualityFolders.max()

      → readCoverBytes(avid, subDir)
        → File("{BILI_ROOT}/{avid}/{subDir}/cover.jpg") → ByteArray
        → CoverCache.put(avid, bytes)

      → MediaItem(
          avid, cid, title, partTitle, ownerName,
          typeTag, qualityPithyDescription, width, height, bvid,
          sourcePath = "{avid}/{subDir}",
          duration, size = totalBytes,
          createdAt = timeUpdateStamp, updatedAt = timeUpdateStamp,
          exportState = NOT_EXPORTED
        )

  → mediaRepository.insertAll(mediaItems)
    → for each item:
      → dao.getByAvidAndCid(avid, cid)
      → 已存在: dao.update(existing.copy(...))   // 保留导出状态
      → 新条目: dao.insert(item.toEntity())

  → 减量清理:
    → getAllSuspend() → existingItems
    → scannedKeys = mediaItems.map { "${avid}:${cid}" }.toSet()
    → for each existing not in scannedKeys:
      → mediaRepository.deleteById(id)

  → _scanState.value = COMPLETED
```

## 3. 首页展示链条

```
LibraryScreen
  → libraryViewModel.filteredItems.collectAsState()
    ┌──────────────────────────────────────────────┐
    │ combine(                                     │
    │   _allItems,         // Room Flow → 实时推送 │
    │   searchQuery,       // 搜索文本             │
    │   exportStateFilter, // 状态筛选             │
    │   sortField,         // TIME / SIZE          │
    │   sortOrder          // ASC / DESC           │
    │ ) → filter → sort → StateFlow               │
    └──────────────────────────────────────────────┘

  → Scaffold:
    → TopAppBar:
      → "ruruDown" / "共 N 个视频 | 已导出 N"
      → Shizuku 状态指示灯 (Cloud/CloudOff)
      → 设置按钮

    → 搜索栏 → OutlinedTextField
    → FilterChips (全部 / 未导出 / 已导出 / 失败)
    → 排序控件 (时间 / 大小 + 升降序箭头)

    → LazyColumn(state = listState):
      → LaunchedEffect(sortField, sortOrder) → animateScrollToItem(0)
      → items(filteredItems, key = "{avid}_{cid}"):
        → VideoCard(item):
          ├─ Coil AsyncImage(coverPath)                     // 封面
          ├─ item.displayTitle                              // 标题
          ├─ item.ownerName                                 // UP主
          ├─ item.qualityLabel                              // 1080P / 720P ...
          ├─ TimeFormat.formatDuration(item.duration)        // 02:00
          ├─ TimeFormat.formatFileSize(item.size)            // 28.1 MB
          └─ StatusBadge(item.exportState)                  // 未导出/已导出/失败

    → FloatingActionButton → Refresh → libraryViewModel.scan()
    → ExportBottomBar (多选模式) → 选中数 / 全选 / 导出

  → 多选逻辑:
    → BackHandler { onClearSelection() }  // 系统回退键退出多选
```

## 4. 详情页链条

```
用户点击视频卡片
  → onNavigateToDetail(mediaItemId)
    → navController.navigate("detail/$mediaItemId")

  → DetailScreen:
    → libraryViewModel.allItems.collectAsState()
    → val item = items.find { it.id == mediaItemId }

    → 信息卡片 Card:
      ├─ "UP主"         → item.ownerName
      ├─ "avid"         → item.avid
      ├─ "bvid"         → TextButton(蓝色, 点击复制到剪贴板)
      ├─ "cid"          → item.cid
      ├─ "清晰度"       → item.qualityPithyDescription
      ├─ "分辨率"       → "${width}×${height}"
      ├─ "大小"         → TimeFormat.formatFileSize(item.size)
      └─ "时长"         → TimeFormat.formatDuration(item.duration)

    → 导出状态 (3 枚标签):
      ├─ StatusBadge(exportState, "MP4")        // EXPORTED=绿色, NOT_EXPORTED=灰色
      ├─ StatusBadge(audioExported, "M4A")      // EXPORTED/NOT_EXPORTED
      └─ StatusBadge(danmakuExported, "XML")    // EXPORTED/NOT_EXPORTED

    → 上次导出 & 导出次数

    → 导出类型选择卡片:
      ├─ ExportTypeCard("视频", selectedVideo)     // 默认选中(绿色)
      ├─ ExportTypeCard("音频", selectedAudio)     // 未选(蓝色)
      └─ ExportTypeCard("弹幕", selectedDanmaku)

    → ExtendedFloatingActionButton "导出"
      → 仅当 anySelected && exportState != EXPORTING
      → onClick → onExport(mediaItem, types)
```

## 5. 单条导出链条

```
DetailScreen: onExport(mediaItem, types)
  ↓
ExportViewModel.singleExport(mediaItem, types)
  → exportDir    = settingsRepository.exportDir.first()          // "/sdcard/Download/ruruDown"
  → overwrite    = settingsRepository.overwriteExisting.first()  // 默认 true
  → outputDir    = OutputNamingStrategy.generateOutputDir(exportDir, mediaItem)
  → outputName   = OutputNamingStrategy.generateOutputName(mediaItem)
  → typesStr     = types.joinToString(",") { it.name }          // "VIDEO,AUDIO"

  → CreateExportJobUseCase.execute(mediaItem, outputDir, overwrite, typesStr)
    → ExportJob(jobId=UUID, mediaItemId, outputName, outputDir,
                overwriteExisting, exportTypes=typesStr)
    → exportRepository.insertJob(job)

  → executeSingleExport(job)
    ↓
RunExportJobUseCase.execute(job)
  → exportRepository.updateJobStatus(jobId, PREPARING)
  → mediaItem = mediaRepository.getById(job.mediaItemId)
  → types = parseTypes(job.exportTypes)                        // Set<ExportType>

  → 仅 VIDEO 时: mediaRepository.updateExportState(id, EXPORTING)

  ┌─── ExportPipeline.execute(job, mediaItem, types) ────────────────────┐
  │                                                                        │
  │  subDir = mediaItem.sourcePath.removePrefix("{avid}/")                 │
  │  sharedDir = "/data/local/tmp/bili_export/{jobId}"                    │
  │  jobDir = app.cacheDir/jobs/{jobId}                                   │
  │                                                                        │
  │  ① 文件复制 (Shizuku)                                                  │
  │    remoteFileCopier.copyMediaFiles(avid, subDir, typeTag, sharedDir)  │
  │      → Binder IPC → BiliFileService.copyM4sFiles()                   │
  │        File(BILI_ROOT/avid/subDir/typeTag/video.m4s)                  │
  │        File(BILI_ROOT/avid/subDir/typeTag/audio.m4s)                  │
  │          → copyTo(sharedDir/video.m4s)                                │
  │          → copyTo(sharedDir/audio.m4s)                                │
  │    minRequired: VIDEO=2, AUDIO=1, DANMAKU=0                           │
  │                                                                        │
  │  ② 视频导出 (FFmpeg JNI)                                               │
  │    if VIDEO in types:                                                  │
  │      tempOutput = "{jobDir}/{baseName}.mp4"                           │
  │      ffmpegEngine.remux(video.m4s, audio.m4s, tempOutput)            │
  │        → JNI nativeRemux():                                           │
  │          avformat_open_input(video) → avformat_find_stream_info       │
  │          avformat_open_input(audio) → avformat_find_stream_info       │
  │          avformat_alloc_output_context2("mp4")                        │
  │          avcodec_parameters_copy() × 2                                │
  │          avformat_write_header()                                      │
  │          while(av_read_frame(video)) → av_interleaved_write_frame()  │
  │          avformat_seek_file(audio)                                    │
  │          while(av_read_frame(audio)) → av_interleaved_write_frame()  │
  │          av_write_trailer()                                           │
  │      ExportValidator.validate(tempOutput, vSize, aSize)              │
  │      copyTo("{finalOutputDir}/{baseName}.mp4")                       │
  │                                                                        │
  │  ③ 音频导出 (M4A)                                                      │
  │    if AUDIO in types:                                                  │
  │      src = "{sharedDir}/audio.m4s"                                    │
  │      tmp = "{jobDir}/{baseName}.m4a"  ← app cache中转                 │
  │      File(src).copyTo(tmp) → tmp.copyTo("{outputDir}/{baseName}.m4a") │
  │                                                                        │
  │  ④ 弹幕导出 (XML)                                                      │
  │    if DANMAKU in types:                                                │
  │      remoteFileCopier.readDanmakuXml(avid, subDir)                    │
  │        → Binder IPC → BiliFileService                                 │
  │          cidDir = subDir.substringBefore("/")                         │
  │          File(BILI_ROOT/avid/cidDir/danmaku.xml).readText()           │
  │      tmp = "{jobDir}/{baseName}.xml" → writeBytes(bytes)              │
  │      tmp.copyTo("{outputDir}/{baseName}.xml")                         │
  │                                                                        │
  │  ⑤ EPERM 保护                                                          │
  │    ensureWritable(outputDir):                                          │
  │      testFile = File(outputDir, ".rw_test").writeText("1")            │
  │      成功 → return outputDir                                           │
  │      EPERM → fallback: context.getExternalFilesDir(MOVIES)            │
  │                                                                        │
  │  ⑥ 清理 & 通知                                                         │
  │    tempPathManager.cleanupJobDir(jobId)                               │
  │    File(sharedDir).deleteRecursively()                                │
  │    MediaScannerConnection.scanFile(ctx, exportedPaths, null)          │
  │                                                                        │
  │  return if exportedPaths.isNotEmpty():                                │
  │    Success(primaryPath, elapsed)        // 部分成功也算成功            │
  │  else:                                                                 │
  │    Failure(OUTPUT_WRITE_FAILED, lastError)                            │
  └────────────────────────────────────────────────────────────────────────┘

  → 回到 RunExportJobUseCase:
    → Success:
      entity.copy(
        exportState     = VIDEO选 ? EXPORTED : NOT_EXPORTED,
        audioExported   = entity.audioExported || AUDIO选,
        danmakuExported = entity.danmakuExported || DANMAKU选,
        lastExportPath  = result.outputPath,
        lastExportAt    = now(),
        exportCount     = VIDEO选 ? entity.exportCount + 1 : entity.exportCount
      ) → mediaRepository.update()

    → Failure:
      updateExportState(id, VIDEO选 ? FAILED : NOT_EXPORTED, "")

  → ExportViewModel._exportResults.emit(Success/Error)
  → batch 模式: onBatchExportComplete?.invoke()
```

## 6. 批量导出链条

```
LibraryScreen:
  用户长按卡片
    → mainViewModel.onToggleSelection(id)
      → _selectedIds += id → _isMultiSelectMode = true

  ExportBottomBar 出现

  "导出" → showBatchTypeDialog = true

  ExportTypeSelectDialog:
    → 视频 / 音频 / 弹幕 三选卡片 (默认视频选中)
    → onConfirm(types)
      → exportViewModel.batchExport(pendingBatchIds, types)

ExportViewModel.batchExport(ids, types)
  → preview = buildExportPreviewUseCase.execute(ids)
    → 分组: newItems / exportedItems / failedItems
  → executeBatchExport(preview.allItems, types)
    → for each item:
      → exportDir = settingsRepository.exportDir.first()
      → overwrite = settingsRepository.overwriteExisting.first()
      → CreateExportJobUseCase.execute(item, outputDir, overwrite, typesStr)
      → RunExportJobUseCase.execute(job)
        → (同单条导出链条)
      → _activeJobs.value = exportRepository.getActiveJobs()
  → onBatchExportComplete?.invoke()
    → mainViewModel.onClearSelection()
```

## 7. Shizuku Binder 通信链条

```
┌──────────────────────────┬──────────────────────────┬───────────────────────────┐
│   App 进程 (UID 10499)   │ Shizuku 进程 (UID 10195) │   Shell 进程 (UID 2000)   │
├──────────────────────────┼──────────────────────────┼───────────────────────────┤
│                          │                          │                           │
│ BiliFileServiceClient    │                          │                           │
│   .init()                │                          │                           │
│     → bindUserService()  │                          │                           │
│       ──────────────────→│ 启动 UserService          │                           │
│                          │   → BiliFileService       │                           │
│                          │     (Stub.Binder)         │                           │
│                          │                          │                           │
│ 调用链:                   │                          │                           │
│ client.listAvids()       │                          │                           │
│  → service?.listAvids()  │                          │                           │
│    [Proxy Parcel]        │                          │                           │
│    ──────Binder IPC─────→│ onTransact(code=1)        │                           │
│                          │  → listAvids()            │                           │
│                          │   → File(BILI_ROOT)       │                           │
│                          │    .listFiles()            │                           │
│                          │  ← return ["116...", ...] │                           │
│  ← result                 │                          │                           │
│                          │                          │                           │
│ client.readDanmakuXml()  │                          │                           │
│  → service?.readDanma... │                          │                           │
│    [Proxy Parcel]        │                          │                           │
│    ──────Binder IPC─────→│ onTransact(code=8)        │                           │
│                          │  → readDanmakuXml()       │                           │
│                          │   → cidDir = "c_..."      │                           │
│                          │   → File("{root}/...      │                           │
│                          │    /danmaku.xml")         │                           │
│                          │    .readText()            │                           │
│  ← result: String?        │                          │                           │
│                          │                          │                           │
│ client.copyM4sFiles()    │                          │                           │
│  → service?.copyM4sFi..  │                          │                           │
│    [Proxy Parcel]        │                          │                           │
│    ──────Binder IPC─────→│ onTransact(code=7)        │                           │
│                          │  → copyM4sFiles()         │                           │
│                          │   → File("{root}/{avid}   │                           │
│                          │    /{subDir}/{typeTag}/   │                           │
│                          │    video.m4s")            │                           │
│                          │   → File("{root}/{avid}   │                           │
│                          │    /{subDir}/{typeTag}/   │                           │
│                          │    audio.m4s")            │                           │
│                          │   → copyTo(destDir)       │                           │
│  ← copied: Int (0-2)     │                          │                           │
│                          │                          │                           │
└──────────────────────────┴──────────────────────────┴───────────────────────────┘
```

### IBiliFileService 接口方法

```
interface IBiliFileService : IInterface
  ├── listAvids(): MutableList<String>
  ├── listSubDirs(avid): MutableList<String>
  ├── readEntryJson(avid, subDir): String
  ├── readCoverBytes(avid, subDir): ByteArray
  ├── listQualityFolders(avid, subDir): MutableList<String>
  ├── checkM4sFilesExist(avid, subDir, typeTag): Boolean
  ├── copyM4sFiles(avid, subDir, typeTag, destDir): Int
  └── readDanmakuXml(avid, subDir): String?

  abstract class Stub : Binder(), IBiliFileService
    ├── asInterface(obj) → IBiliFileService?   // 从 IBinder 还原接口
    ├── onTransact(code, data, reply, flags)   // Binder 事务分发
    └── private class Proxy : IBiliFileService // 远程代理 (Parcel 序列化)

  companion object:
    TRANSACTION_listAvids = 1
    TRANSACTION_listSubDirs = 2
    TRANSACTION_readEntryJson = 3
    TRANSACTION_readCoverBytes = 4
    TRANSACTION_listQualityFolders = 5
    TRANSACTION_checkM4sFilesExist = 6
    TRANSACTION_copyM4sFiles = 7
    TRANSACTION_readDanmakuXml = 8
```

## 8. FFmpeg JNI 链条

```
FfmpegEngine (Kotlin)
  companion init:
    System.loadLibrary("ffmpeg_bridge")     // 加载 libffmpeg_bridge.so
      → 依赖链: ffmpeg_bridge → avformat → avcodec → avutil
      → 所有 .so 位于 APK lib/arm64-v8a/

  fun remux(videoPath, audioPath, outputPath, options):
    → nativeRemux(video, audio, output, useCopy, overwrite)
      └── JNI C++ 层:
        extern "C" {
        #include <libavformat/avformat.h>
        #include <libavcodec/avcodec.h>
        #include <libavutil/avutil.h>
        }

        Java_com_example_bilexport_export_ffmpeg_FfmpegEngine_nativeRemux()
          ├─ avformat_open_input(&videoCtx, videoPath)
          ├─ avformat_find_stream_info(videoCtx)
          ├─ avformat_open_input(&audioCtx, audioPath)
          ├─ avformat_find_stream_info(audioCtx)
          ├─ avformat_alloc_output_context2(&outCtx, "mp4")
          ├─ av_find_best_stream(videoCtx, AVMEDIA_TYPE_VIDEO)
          ├─ av_find_best_stream(audioCtx, AVMEDIA_TYPE_AUDIO)
          ├─ avcodec_parameters_copy() × 2
          ├─ avio_open(&outCtx->pb, output, AVIO_FLAG_WRITE)
          ├─ avformat_write_header(outCtx)
          ├─ av_packet_alloc()
          ├─ while(av_read_frame(videoCtx, pkt)):
          │   av_rescale_q_rnd() / av_interleaved_write_frame()
          ├─ avformat_seek_file(audioCtx, ...)
          ├─ while(av_read_frame(audioCtx, pkt)):
          │   av_rescale_q_rnd() / av_interleaved_write_frame()
          ├─ av_write_trailer(outCtx)
          └─ cleanup: av_packet_free, avio_closep,
                      avformat_free_context, avformat_close_input
```

## 9. 数据库与状态链条

```
AppDatabase (Room)
  version = 5, fallbackToDestructiveMigration()

  entities:
    ├── MediaItemEntity (media_items)
    │   ├── @PrimaryKey id
    │   ├── avid, cid, bvid, title, partTitle, ownerName
    │   ├── typeTag, qualityPithyDescription, videoWidth, videoHeight
    │   ├── sourcePath, entryJsonPath, coverPath
    │   ├── isCompleted, duration, size
    │   ├── sourceHash, sourceMtime
    │   ├── exportState (NOT_EXPORTED / EXPORTED / FAILED / EXPORTING)
    │   ├── audioExported, danmakuExported
    │   ├── lastExportAt, lastExportPath, exportCount
    │   └── createdAt, updatedAt

    └── ExportJobEntity (export_jobs)
        ├── @PrimaryKey jobId
        ├── mediaItemId, sourceAvid, sourceCid, title
        ├── outputName, outputDir, jobTempDir
        ├── status (PENDING→PREPARING→...→SUCCESS/FAILED/CANCELLED)
        ├── repeatPolicy, overwriteExisting
        ├── startAt, endAt, progress
        ├── errorCode, errorMessage
        └── ffmpegCommand, ffmpegLogPath, outputPath

  DAOs:
    ├── MediaItemDao
    │   ├── getAll() → Flow
    │   ├── getAllDistinct() → Flow (去重: MAX(id) GROUP BY avid,cid)
    │   ├── getAllOnce() → suspend
    │   ├── getByAvidAndCid() → suspend
    │   └── insert/update/delete...

    └── ExportJobDao
        ├── getAll() → Flow
        ├── getByStatus / getActiveJobs / getFailedJobs
        └── insert/update/delete...

MediaItem 导出状态机:
  NOT_EXPORTED ──[选VIDEO导出]──→ EXPORTING ──[成功]──→ EXPORTED
       │                              │
       └──────[失败]─────────────────→ FAILED
       │
       └──[仅AUDIO/DANMAKU导出]──→ NOT_EXPORTED (不改变)
                                   audioExported/danmakuExported = true

  "已导出/未导出" 筛选依据: exportState == EXPORTED (仅MP4)
  M4A/XML 导出状态: audioExported / danmakuExported 独立 Boolean
```

## 10. 数据流全图

```
┌─────────────────────────────────────────────────────────────────┐
│                         B 站缓存目录                               │
│  /sdcard/Android/data/tv.danmaku.bili/download/                  │
│    {avid}/                                                        │
│      c_{cid}/                                                     │
│        entry.json    ← 元数据                                      │
│        cover.jpg     ← 封面                                        │
│        danmaku.xml   ← 弹幕                                        │
│        {typeTag}/                                                   │
│          video.m4s   ← H.264/H.265 视频流                          │
│          audio.m4s   ← AAC 音频流                                  │
│          index.json  ← CDN URL + 各流元信息                         │
└───────────────────────┬─────────────────────────────────────────┘
                        │ Shizuku Binder IPC
                        ▼
┌─────────────────────────────────────────────────────────────────┐
│                   BiliFileService (shell UID)                     │
│  直接 File() 读写 → list / read / copy                            │
└───────────────────────┬─────────────────────────────────────────┘
                        │ Binder IPC 返回数据
                        ▼
┌─────────────────────────────────────────────────────────────────┐
│                   App 进程 (UID 10499)                             │
│                                                                   │
│  ScanLibraryUseCase          ExportPipeline                       │
│    → EntryJsonParser           → Shizuku copy → /data/local/tmp  │
│    → MediaItem                  → FFmpeg JNI remux → jobDir      │
│    → insertAll(DB)              → copyTo → Download/ruruDown     │
│      + 减量清理                                                    │
│                        │              │                           │
│                        ▼              ▼                           │
│                  Room DB (SQLite)   文件系统                        │
│                  media_items        /sdcard/Download/ruruDown/   │
│                  export_jobs          ├── {视频名}/                │
│                  DataStore            │   ├── xxx.mp4              │
│                  (settings)           │   ├── xxx.m4a              │
│                                      │   └── xxx.xml              │
│                        │              │                           │
│                        ▼              │                           │
│                  MediaRepository      │                           │
│                  (Flow → StateFlow)   │                           │
│                        │              │                           │
│                        ▼              │                           │
│                  LibraryViewModel ────┘                           │
│                  LibraryScreen (Compose)                          │
│                    用户看到列表                                     │
└─────────────────────────────────────────────────────────────────┘
```
