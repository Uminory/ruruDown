# BiliExport 项目框架

## 1. 项目目标

这是一个 Android 视频缓存导出工具，核心目标是：

1. 扫描并识别视频平台的本地缓存目录。
2. 读取受限目录中的元数据与媒体分片，受限目录访问通过 Shizuku 完成。
3. 以 **FFmpeg 原生库（.so + JNI）** 作为唯一导出引擎。
4. 支持单条导出、批量导出、重复导出确认、导出队列、失败重试。
5. 保持 UI 简洁、暗色主题、高信息密度、低层级。
6. 保持扫描层、索引层、执行层完全解耦，便于后续维护和替换。

本项目的设计原则是：

- 扫描与导出分离。
- 元数据与任务执行分离。
- UI 与业务逻辑分离。
- Shizuku 只负责提权文件访问，不承载业务逻辑。
- FFmpeg 只作为 native 导出引擎，不直接承担扫描职责。
- 所有长耗时操作必须可中断、可重试、可恢复。

---

## 2. 总体架构

建议采用四层结构：

### 2.1 Presentation 层
负责界面渲染、交互、状态展示。

- 首页：媒体库列表
- 搜索栏：按标题、UP 主、avid、cid、导出状态过滤
- 批量选择模式
- 导出队列页或底部常驻导出条
- 设置页：导出目录、扫描模式、重复导出策略、日志开关

### 2.2 Domain 层
负责业务规则和用例编排。

- 扫描库
- 刷新索引
- 创建导出任务
- 判断重复导出
- 排队导出
- 导出后更新状态
- 导出失败重试

### 2.3 Data 层
负责本地数据、缓存、索引与持久化。

- Room 数据库
- 本地缓存目录
- cover 缓存
- 导出 job 缓存
- 设置持久化（DataStore 或 SharedPreferences）

### 2.4 Infrastructure / Native 层
负责系统能力和第三方能力。

- Shizuku 用户服务
- 受限目录文件读取
- 文件复制
- FFmpeg JNI 调用
- 日志输出
- 权限检查与服务连接状态

---

## 3. 目录结构建议

建议采用如下包结构：

```text
app/src/main/java/com/example/bilexport/
├─ app/
│  ├─ App.kt
│  ├─ MainActivity.kt
│  └─ Navigation.kt
├─ core/
│  ├─ model/
│  │  ├─ MediaItem.kt
│  │  ├─ MediaSource.kt
│  │  ├─ ExportJob.kt
│  │  ├─ ExportState.kt
│  │  ├─ ScanState.kt
│  │  └─ RepeatExportPolicy.kt
│  ├─ util/
│  │  ├─ FileNameSanitizer.kt
│  │  ├─ TimeFormat.kt
│  │  ├─ HashUtil.kt
│  │  └─ Result.kt
│  └─ constants/
│     ├─ Paths.kt
│     ├─ Tags.kt
│     └─ Defaults.kt
├─ data/
│  ├─ db/
│  │  ├─ AppDatabase.kt
│  │  ├─ MediaItemDao.kt
│  │  ├─ ExportJobDao.kt
│  │  ├─ MediaItemEntity.kt
│  │  └─ ExportJobEntity.kt
│  ├─ repository/
│  │  ├─ MediaRepositoryImpl.kt
│  │  ├─ ExportRepositoryImpl.kt
│  │  └─ SettingsRepositoryImpl.kt
│  ├─ cache/
│  │  ├─ CoverCache.kt
│  │  ├─ JobCache.kt
│  │  └─ TempPathManager.kt
│  └─ parser/
│     ├─ EntryJsonParser.kt
│     └─ SourcePathParser.kt
├─ domain/
│  ├─ repository/
│  │  ├─ MediaRepository.kt
│  │  ├─ ExportRepository.kt
│  │  └─ SettingsRepository.kt
│  └─ usecase/
│     ├─ ScanLibraryUseCase.kt
│     ├─ RefreshLibraryUseCase.kt
│     ├─ BuildExportPreviewUseCase.kt
│     ├─ CreateExportJobUseCase.kt
│     ├─ RunExportJobUseCase.kt
│     ├─ RetryExportJobUseCase.kt
│     └─ CleanupJobUseCase.kt
├─ privileged/
│  ├─ shizuku/
│  │  ├─ ShizukuManager.kt
│  │  ├─ BiliFileServiceClient.kt
│  │  ├─ IBiliFileService.aidl
│  │  └─ BiliFileService.kt
│  ├─ source/
│  │  ├─ RemoteDirectoryScanner.kt
│  │  ├─ RemoteFileReader.kt
│  │  └─ RemoteFileCopier.kt
│  └─ permission/
│     ├─ PermissionState.kt
│     └─ PermissionChecker.kt
├─ export/
│  ├─ ffmpeg/
│  │  ├─ FfmpegEngine.kt
│  │  ├─ FfmpegCommandBuilder.kt
│  │  ├─ FfmpegSession.kt
│  │  ├─ FfmpegOptions.kt
│  │  └─ FfmpegProbe.kt
│  ├─ pipeline/
│  │  ├─ ExportPipeline.kt
│  │  ├─ ExportProgress.kt
│  │  └─ ExportValidator.kt
│  └─ strategy/
│     ├─ RepeatExportStrategy.kt
│     └─ OutputNamingStrategy.kt
├─ ui/
│  ├─ theme/
│  │  ├─ Color.kt
│  │  ├─ Theme.kt
│  │  └─ Type.kt
│  ├─ screen/
│  │  ├─ LibraryScreen.kt
│  │  ├─ DetailScreen.kt
│  │  ├─ ExportQueueScreen.kt
│  │  └─ SettingsScreen.kt
│  ├─ component/
│  │  ├─ VideoCard.kt
│  │  ├─ FilterChips.kt
│  │  ├─ ConfirmRepeatExportDialog.kt
│  │  ├─ ExportBottomBar.kt
│  │  └─ StatusBadge.kt
│  └─ viewmodel/
│     ├─ MainViewModel.kt
│     ├─ LibraryViewModel.kt
│     ├─ ExportViewModel.kt
│     └─ SettingsViewModel.kt
└─ native/
   ├─ CMakeLists.txt
   ├─ jni/
   │  ├─ ffmpeg_bridge.cpp
   │  ├─ ffmpeg_bridge.h
   │  └─ log_bridge.cpp
   └─ third_party/
      └─ ffmpeg/
```

---

## 4. 核心实体设计

### 4.1 MediaItem

媒体条目是核心数据模型。

字段建议：

- `id`
- `avid`
- `cid`
- `title`
- `partTitle`
- `ownerName`
- `typeTag`
- `sourcePath`
- `entryJsonPath`
- `coverPath`
- `isCompleted`
- `duration`
- `size`
- `sourceHash`
- `sourceMtime`
- `exportState`
- `lastExportAt`
- `lastExportPath`
- `exportCount`
- `createdAt`
- `updatedAt`

关键点：

- `sourceHash` 只用于变更判断，不用于 UI 频繁计算。
- `exportState` 不能只用布尔值。
- `lastExportPath` 必须记录，便于“打开文件位置”与重复导出策略。
- `isCompleted` 必须以源 JSON 为准，不要凭文件名猜测。

### 4.2 ExportJob

导出任务是队列中的最小执行单位。

字段建议：

- `jobId`
- `mediaItemId`
- `sourceAvid`
- `sourceCid`
- `title`
- `outputName`
- `outputDir`
- `jobTempDir`
- `status`
- `repeatPolicy`
- `startAt`
- `endAt`
- `progress`
- `errorCode`
- `errorMessage`
- `ffmpegCommand`
- `ffmpegLogPath`
- `outputPath`

状态建议：

- `PENDING`
- `PREPARING`
- `COPYING_INPUT`
- `RUNNING_FFMPEG`
- `VERIFYING`
- `MOVING_OUTPUT`
- `SUCCESS`
- `FAILED`
- `CANCELLED`

### 4.3 RepeatExportPolicy

重复导出策略建议显式枚举：

- `SKIP_DUPLICATES`
- `EXPORT_ALL`
- `ASK_CONFIRMATION`
- `EXPORT_ONLY_NEW`

默认建议：

- 批量导出默认 `ASK_CONFIRMATION`
- 设置页可改成 `SKIP_DUPLICATES`
- 单条导出默认允许覆盖提示

---

## 5. 数据库设计

建议使用 Room，至少两张核心表。

### 5.1 media_items 表

保存扫描后的条目快照与导出状态。

重点索引：

- `avid`
- `cid`
- `sourceHash`
- `exportState`
- `updatedAt`

### 5.2 export_jobs 表

保存历史任务，便于排查失败、恢复队列、展示日志。

重点索引：

- `status`
- `startAt`
- `mediaItemId`
- `jobId`

### 5.3 settings 表或 DataStore

保存：

- 上次导出目录 URI
- 是否自动跳过已导出
- 是否导出时覆盖同名文件
- 是否保存 ffmpeg 日志
- 是否启用封面缓存
- 扫描路径根目录
- 最近一次扫描时间

---

## 6. Shizuku / 受限目录访问设计

Shizuku 层只做“文件访问代理”。

### 6.1 允许的能力

- 列出 avid 目录
- 列出 `c_{cid}` 目录
- 读取 `entry.json`
- 读取 `cover.jpg`
- 复制 `video.m4s`
- 复制 `audio.m4s`
- 读取必要的目录信息
- 删除临时文件（仅限工作目录）

### 6.2 不允许的能力

- 不做业务判断
- 不做导出策略判断
- 不做数据库写入
- 不做 UI 相关逻辑
- 不直接拼接 ffmpeg 命令

### 6.3 传输原则

任何导出文件都要先复制到 App 可控的 job temp dir，再由 FFmpeg 处理。

不要让 FFmpeg 直接碰源目录，原因是：

- 读受限目录不稳定
- 源目录权限可能变化
- 多任务并发容易冲突
- 临时文件可控性差
- 日志和恢复很难做

---

## 7. FFmpeg 集成方式

### 7.1 集成方式选择

推荐：`FFmpeg shared libraries (.so) + JNI`

不推荐：把 Linux CLI 二进制塞进 Android 再 `Runtime.exec`

原因：

- ABI 可控
- 适配 Android 更稳定
- 不依赖 Linux/glibc CLI 环境
- 可做更细粒度的日志和取消
- 更适合长期维护

### 7.2 Native API 设计

建议在 JNI 层只暴露少量接口：

```kotlin
fun init()
fun probe(inputPath: String): MediaProbeResult
fun remux(videoPath: String, audioPath: String, outputPath: String, options: FfmpegOptions): Int
fun cancel(jobId: String)
fun release()
```

### 7.3 命令构建原则

FFmpeg 只做 remux 时，优先使用：

- `-c copy`
- `-map 0:v:0`
- `-map 1:a:0`
- `-y`
- `-nostdin`
- `-hide_banner`

必要时加：

- `-map_chapters -1`
- `-map_metadata -1`
- `-movflags +faststart`

### 7.4 日志与错误码

必须把：

- 命令参数
- 输入路径
- 输出路径
- ffmpeg stderr
- 返回码
- 任务耗时

写入任务日志。

---

## 8. 导出流水线

导出流程必须固定，不能被 UI 直接绕过。

### 8.1 单条导出流程

1. 校验媒体条目是否完整。
2. 判断是否已导出。
3. 根据重复导出策略决定是否弹窗。
4. 创建 ExportJob。
5. 创建独立 jobTempDir。
6. 通过 Shizuku 复制 `video.m4s` 和 `audio.m4s` 到 jobTempDir。
7. JNI 调用 FFmpeg remux。
8. 校验输出文件是否存在且大小合理。
9. 移动输出文件到目标导出目录。
10. 更新数据库导出状态。
11. 清理 jobTempDir。
12. 输出成功日志或失败原因。

### 8.2 批量导出流程

1. 收集用户勾选的 media items。
2. 分成未导出项、已导出项、失败项。
3. 生成重复导出预览列表。
4. 弹出确认框。
5. 根据用户选择：
   - 取消
   - 排除重复项
   - 确认全部导出
6. 将最终列表写入队列。
7. 单线程或串行执行队列。
8. 每完成一个 job，刷新 UI。

### 8.3 并发策略

- 扫描可并发。
- 解析可并发。
- hash 计算可并发。
- 导出建议串行。
- 如果未来启用多并发导出，也必须限定为极低并发数，并确保每个 job 独立目录。

---

## 9. 重复导出交互规则

这是产品行为的一部分，必须固定。

### 9.1 默认行为

已导出项目默认排除，不直接禁止。

### 9.2 批量导出时

如果列表中包含已导出项目，显示确认对话框：

- 以下视频已导出：
  - Title1
  - Title2
  - Title3
- 是否重复导出？

按钮：

- 取消
- 排除重复项
- 确认重复导出

### 9.3 建议默认按钮

默认高亮按钮应是“排除重复项”。

原因：

- 最符合多数用户预期
- 防止误操作
- 适合批量任务

---

## 10. UI 设计约束

### 10.1 主题

- 纯暗色
- 避免纯黑
- 卡片间层级清晰
- 主色建议蓝色系
- 成功绿色
- 警告黄色
- 错误红色

### 10.2 首页布局

首页建议只保留：

- 顶部标题 + 总数统计
- 搜索框
- 状态筛选 chips
- 视频列表
- 底部导出队列条或浮动导出条

不要在首页堆太多按钮。

### 10.3 卡片信息密度

每个视频条目应展示：

- 封面缩略图
- 标题
- UP 主
- 分辨率 / 大小
- 导出状态
- 可选：导出时间

### 10.4 交互约定

- 单击条目：进入详情
- 长按条目：进入多选
- 批量模式下底部固定导出栏出现
- 已导出状态可点击查看导出信息
- 支持搜索和筛选
- 支持按状态过滤：全部 / 未导出 / 已导出 / 失败

---

## 11. ViewModel 与状态流

建议每个页面一个 ViewModel，核心状态使用 StateFlow 或 LiveData。

### 11.1 MainViewModel

负责：

- 初始化
- Shizuku 连接状态
- 用户权限状态
- 总体库统计
- 当前筛选条件
- 多选状态
- 导出入口

### 11.2 LibraryViewModel

负责：

- 列表数据
- 搜索过滤
- 状态过滤
- 排序
- 刷新

### 11.3 ExportViewModel

负责：

- 导出队列
- 当前任务进度
- 导出日志
- 重试
- 取消
- 重复导出确认状态

### 11.4 SettingsViewModel

负责：

- 导出目录
- 重复导出默认策略
- 日志开关
- 缓存开关
- 扫描策略

---

## 12. 错误处理规范

所有错误必须可分类，不允许只返回“失败”。

建议分类：

- `PermissionDenied`
- `ShizukuUnavailable`
- `SourceDirNotFound`
- `EntryJsonInvalid`
- `MediaFilesMissing`
- `CopyFailed`
- `FfmpegInitFailed`
- `FfmpegRuntimeError`
- `OutputWriteFailed`
- `OutputMoveFailed`
- `DbWriteFailed`
- `Cancelled`
- `Unknown`

每类错误都应该有：

- 用户可读提示
- 开发日志
- 恢复建议

---

## 13. 日志规范

日志分层：

- UI 日志：给用户看
- 业务日志：给开发调试
- FFmpeg stderr：原始日志
- Shizuku 访问日志：源访问问题排查

建议每个 job 输出一个日志文件：

```text
cache/jobs/{jobId}/log.txt
```

必要时保留：

```text
cache/jobs/{jobId}/command.txt
cache/jobs/{jobId}/result.json
```

---

## 14. 缓存策略

### 14.1 封面缓存

- 以 avid 作为 key
- 本地缓存图片
- 不强制每次刷新都重新拉取
- 如果 `cover.jpg` 缺失，显示占位图

### 14.2 导出临时缓存

- 每个任务独立 temp dir
- 完成后立即清理
- 失败时保留一段时间便于排查

### 14.3 索引缓存

- entry.json 解析结果存数据库
- 通过 mtime + size + hash 判断是否变化
- 不要每次启动都全量解析全部源文件

---

## 15. 扫描策略

建议扫描分两级：

### 15.1 快速扫描

- 列出 avid
- 列出 `c_{cid}`
- 定位 entry.json
- 读取必要字段
- 快速更新数据库

### 15.2 深度扫描

在用户主动刷新时执行：

- 重新读取 entry.json
- 更新 hash
- 校验缺失媒体分片
- 检查封面缓存

---

## 16. 可测试性要求

Agent 编写代码时必须保证以下模块可单独测试：

- `EntryJsonParser`
- `FileNameSanitizer`
- `RepeatExportStrategy`
- `FfmpegCommandBuilder`
- `ExportValidator`
- `SourcePathParser`

建议至少保留：

- 单元测试
- 少量本地样本测试
- 伪造 entry.json 样例
- 伪造缺失音视频样例

---

## 17. 代码风格要求

### 17.1 Kotlin 侧

- 禁止把系统路径硬编码散落在业务代码里
- 路径统一从 `Paths.kt` 或配置层获取
- 使用密封类表达状态
- 使用数据类表达快照
- 长耗时操作必须挂在协程/后台线程

### 17.2 Native 侧

- JNI 接口尽量少
- 所有返回值清晰
- 不把业务规则写在 C++
- 只做 FFmpeg 相关逻辑

### 17.3 命名规则

- `UseCase` 表达单一业务动作
- `Repository` 负责数据聚合
- `Manager` 只做系统能力管理
- `Engine` 只做执行引擎
- `Parser` 只做结构解析

---

## 18. 推荐的实现顺序

建议按以下顺序做：

1. 数据模型与数据库
2. `entry.json` 解析器
3. Shizuku 目录读取与复制
4. job temp dir 机制
5. FFmpeg JNI 框架
6. 单条导出闭环
7. 批量导出与重复导出确认
8. UI 首页与多选模式
9. 导出队列与失败重试
10. 日志、缓存、设置页
11. 优化与测试

---

## 19. 验收标准

以下条件全部满足才算框架可用：

- 能稳定扫描源目录
- 能正确解析 entry.json
- 能显示视频列表与状态
- 能通过 Shizuku 读取受限目录
- 能创建独立 jobTempDir
- 能通过 FFmpeg 完成无损重封装
- 能处理重复导出确认
- 能批量导出
- 能保存导出状态
- 能重试失败任务
- 能清理临时文件
- 能在暗色主题下正常展示

---

## 20. 给 Agent 的执行原则

Agent 实施时必须遵守：

1. 先建骨架，再填实现。
2. 先跑通最小闭环，再做美化。
3. 任何模块不得跨层调用。
4. 不要为了“快速”把 FFmpeg、Shizuku、UI 混在一起。
5. 不要让临时目录复用。
6. 不要让导出状态只靠布尔值。
7. 不要让用户看见复杂内部细节，复杂度放在架构里，不放在界面里。
8. 不要在未确认输出可靠前删除源缓存。
9. 不要把 CLI 习惯直接搬到 Android App。
10. 任何设计都要优先保证可恢复、可重试、可排障。
