# Changelog · 更新日志

All notable changes to ruruDown, newest first. Version numbers match the APK's
`versionName`.

## v1.0.4.1 — 2026-11-12

Subtitle export and playback update. Internal versionCode: **107**;
uses the existing release signing certificate.

- Scan cached subtitle track names without reading their contents during library scanning. Preserve the existing catalog through a non-destructive database upgrade.
- Show subtitle export in details only when cached tracks were found, with independent SRT and plain TXT options. Use the exported video's actual filename stem.
- Add a CC switch for same-folder, same-name SRT playback, including externally supplied files; no subtitle ownership verification or selector is required. Parse and render subtitles separately from the video source, with resource limits and failure isolation.
- With overwrite disabled, skip all same-name targets, including unregistered files and names reserved in the current batch, rather than creating numbered copies. Keep safe replacement protection when overwrite is enabled.
- Retain full-MP4-only home badges. Subtitle output recognition belongs in details. ASS styling and MKV export are outside this version.

<details><summary>简体中文</summary>

字幕导出与播放更新，内部版本号 **107**，沿用原发布签名。

- 扫描缓存字幕文件名，首页扫描不解析字幕正文；数据库增量升级保留旧媒体索引。
- 仅扫描到字幕时在详情页显示字幕导出，内含独立 SRT / 纯 TXT 勾选项，文件主干跟随视频实际名称。
- 播放器新增 CC 字幕开关，只读取同目录同名 SRT，外部放入的字幕也可使用，不要求资产登记；字幕解析和渲染与视频源隔离，并限制异常资源消耗。
- 关闭覆盖时，目标同名统一跳过，包括未登记文件和批次中已预留的名称，不再编号另存；开启覆盖仍保留安全替换保护。
- 首页仍只标记完整 MP4；字幕在详情标记。本版不含 ASS 样式或 MKV 导出。

</details>

<details><summary>繁體中文</summary>

字幕匯出與播放更新，內部版本號 **107**，沿用原發行簽章。

- 掃描快取字幕檔名，首頁掃描不解析正文；資料庫增量升級保留舊媒體索引。
- 僅偵測到字幕時顯示詳情字幕匯出，內含獨立 SRT／純 TXT 選項，與影片實際檔名同名。
- 播放器新增 CC 開關，只讀取同目錄同名 SRT，無需字幕資產登記；解析與繪製獨立於影片來源，並限制異常資源耗用。
- 關閉覆寫時，同名目標一律略過，包含未登記檔案及本批次已預留名稱，不再編號另存；開啟覆寫仍保留安全替換。
- 首頁僅標記完整 MP4，字幕於詳情標記。本版不含 ASS 樣式或 MKV 匯出。

</details>

<details><summary>日本語</summary>

字幕出力と再生のアップデートです。内部 versionCode は **107**、従来の署名を継続します。

- スキャン時はキャッシュ字幕のファイル名のみを取得し、本文は読み込みません。既存データを維持したままデータベースを更新します。
- 字幕が見つかった場合のみ詳細画面に出力欄を表示。SRT／本文のみの TXT を個別に選択し、動画の実際のファイル名に合わせます。
- 同じフォルダーの同名 SRT を表示する CC スイッチを追加。字幕の出力履歴や選択画面は不要です。解析・描画を動画ソースから分離し、資源使用量を制限します。
- 上書き無効時は未登録ファイルや同バッチ内の予約名も含め同名をスキップし、連番コピーを作りません。上書き有効時の安全な置換を維持します。
- ホームの完了表示は全編 MP4 のみ、字幕は詳細で表示。ASS スタイルと MKV 出力は含みません。

</details>

## v1.0.4 — 2026-11-11

Stable release of the 1.0.4 preview series. Internal versionCode: **106**;
uses the existing release signing certificate.

- **Clip export:** mark a time range in the new Clip trimming tab and stream-copy
  MP4 or audio-only M4A. The player stays independent of tab changes; millisecond
  inputs and a focus-aware Current time button help select boundaries. Recognized
  local MP4 is preferred, with cache fallback; the queue shows save progress,
  completed size and actual boundaries. Video starts remain keyframe-dependent.
- **Exported assets and folders:** recognize registered files by URI and metadata,
  keep title-based filenames, and offer verified migration after folder changes.
  Retain registrations when access methods refer to the same public folder.
  Home badges remain full-MP4-only; audio/XML marks stay in details.
- **Safer storage:** preserve old exports until replacement is verified, recover
  recorded staging files conservatively, protect scans and stale delete selections,
  and check private staging space. Export destinations are verified public storage;
  legacy storage permission handling is improved.
- **More resilient tasks:** handle service failures, timeouts and unresponsive work
  with automatic stopping and cleanup; keep item failures isolated in the queue.
- **Playback and danmaku:** audio focus, headset-disconnection pause and replay after
  ending; portrait full screen for tall videos; danmaku across the whole player,
  including black bars. Paused rendering and XML resource use are bounded.
- **Performance and navigation:** optimized and obfuscated Release code, unused
  resource shrinking, cached list computations, incremental asset refresh, less
  repeated folder IO and off-main-thread detail lookups. Release danmaku data when
  leaving details and preserve library/audio/settings scroll positions.
- **Polish:** consistent concise interface, diagnostic and license notices across
  English, Simplified Chinese, Traditional Chinese and Japanese; updated release
  documents and download checksums.

<details><summary>简体中文</summary>

1.0.4 系列稳定版，内部版本号 **106**，沿用原发布签名。

- **片段导出**：新增“片段裁切”标签、毫秒时间输入与跟随焦点启用的“当前时间”按钮，
  播放器不随标签切换重建。优先使用已识别本地 MP4，必要时回退缓存；流复制导出 MP4
  或纯音频 M4A，队列显示保存进度、完成体积及实际范围。视频起点仍受关键帧限制。
- **资产与目录**：使用私有索引和文件 URI/元数据核对已导出资产，文件名保留纯净标题；
  更换目录可一键校验迁移，同目录不同访问方式保留登记。首页只标记完整 MP4，音频/XML
  仍仅在详情标记，片段不计为整片。
- **存储可靠性**：覆盖前保留旧文件，核验新副本后再处理原件，保守恢复有记录的暂存；
  加强扫描、过期删除选择与私有暂存空间预检。仅允许已确认的公共存储，补齐旧系统授权。
- **任务恢复**：服务异常、超时或长时间无响应时自动停止并清理，单项失败不阻塞后续任务。
- **播放器与弹幕**：补齐音频焦点、拔耳机暂停及结束重播；竖屏源使用竖屏全屏，弹幕覆盖
  黑边；暂停后按需绘制，限制异常 XML 开销，不影响原始弹幕导出。
- **性能与导航**：启用 Release 优化、混淆及资源裁剪，复用列表计算、增量刷新资产，减少
  目录 IO，详情后备查询移出主线程；退出详情释放弹幕，切页或返回保留首页/音频/设置位置。
- **文本与文档**：同步简体中文、繁体中文、英文、日文的界面、诊断和许可说明，整理发行
  文档、更新日志及下载校验信息。

</details>

<details><summary>繁體中文</summary>

1.0.4 系列穩定版，內部版本號 **106**，沿用原發行簽章。

- **片段匯出**：新增裁切分頁、毫秒時間輸入及依焦點啟用的「目前時間」按鈕，切換分頁
  保留播放器。優先使用已識別的本機 MP4，必要時回退快取；串流複製匯出 MP4 或純音訊
  M4A，佇列顯示儲存進度、檔案大小及實際範圍。視訊起點仍受關鍵影格限制。
- **資產與目錄**：以私有索引及 URI/中繼資料核對已匯出檔案，保留純標題檔名；切換目錄
  可驗證遷移，同目錄不同存取方式保留登記。首頁只標記完整 MP4，音訊/XML 僅在詳情標記。
- **儲存可靠性**：驗證新副本前保留舊檔，保守復原已登記暫存，加強掃描、刪除確認及
  暫存空間預檢；限用已確認的公用儲存空間，改善舊系統授權。
- **任務復原**：服務異常、逾時或長時間無回應時自動停止並清理，單項失敗不阻擋後續任務。
- **播放與彈幕**：音訊焦點、耳機斷線暫停、結束重播、直式影片直式全螢幕；彈幕包含黑邊，
  暫停後按需繪製並限制異常 XML 資源用量，不影響原始 XML 匯出。
- **效能與導覽**：Release 最佳化、混淆與資源縮減，重用清單運算、增量核對資產、減少
  目錄 IO；離開詳情釋放彈幕，返回或切換首頁/音訊/設定時保留捲動位置。
- **文字與文件**：同步四種語言的介面、診斷和授權說明，更新發行文件及校驗資訊。

</details>

<details><summary>日本語</summary>

1.0.4 系列の安定版です。内部 versionCode は **106**、従来の署名を継続します。

- **クリップ書き出し**：専用タブ、ミリ秒入力、入力欄のフォーカスに連動する現在時刻
  ボタンを追加。タブ切り替えでもプレーヤーを維持します。認識済みのローカル MP4 を
  優先し、必要時はキャッシュを使用。再エンコードせず MP4／音声のみの M4A を出力し、
  キューに保存進捗・サイズ・実際の範囲を表示します。映像の開始点はキーフレームに依存します。
- **ファイルとフォルダー**：非公開の索引と URI／メタデータで照合し、タイトルのみの
  ファイル名を維持。フォルダー変更時に検証付き移行を選べ、同じ場所のアクセス方式変更も
  登録を保持します。ホームの完了表示は全編 MP4 のみ、音声／XML は詳細画面のみです。
- **ストレージ保護**：新しい出力の検証まで既存ファイルを保持し、記録済み一時ファイルを
  慎重に復旧。スキャン・削除確認・一時領域の空き容量確認と旧 Android の権限処理を改善。
  出力先は確認済みの共有ストレージに限定します。
- **タスクの復旧**：サービス異常、タイムアウト、長時間の無応答で自動停止と後始末を行い、
  失敗した項目が後続タスクを妨げないようにします。
- **再生と弾幕**：音声フォーカス、イヤホン切断時の一時停止、終了後の再再生、縦長動画の
  縦画面全画面に対応。黒帯にも弾幕を表示し、一時停止中の描画と XML の資源消費を抑えます。
- **性能と画面状態**：Release の最適化・難読化・リソース削減、一覧計算の再利用と差分照合、
  フォルダー IO の削減。詳細を離れると弾幕を解放し、主要画面のスクロール位置を保持します。
- **文言と文書**：4 言語の画面・診断・ライセンス表記を整理し、配布文書とチェックサムを更新。

</details>

## v1.0.4-beta.2 — 2026-11-10

- Preview of clip trimming with local-MP4 preference, persistent exported-asset
  recognition, verified folder migration and clip queue progress. Internal versionCode: 105.
- Subsequent preview maintenance is consolidated into the stable 1.0.4 entry above.

<details><summary>简体中文</summary>

- 预览片段裁切、本地 MP4 优先、持久资产识别、目录校验迁移和片段队列进度，内部版本号 105。
- 后续预览维护改进统一归入上方 1.0.4 稳定版条目。

</details>

## v1.0.4-beta.1 — 2026-09-27

- Custom scan paths (**Settings → Scan paths**): the cache folder of a modified or
  third-party bilibili client can be added alongside the built-in ones.
- In-player XML danmaku overlay, powered by a built-in engine (zero new dependencies):
  tap the toolbar button to toggle, long-press for the quick style panel; persistent
  defaults live in **Settings → Danmaku**.

<details><summary>简体中文</summary>

- 自定义扫描路径（**设置 → 扫描路径**）：可将修改版 / 第三方 Bilibili 客户端的缓存
  目录加入扫描，与内置路径并存。
- 内置播放器新增 XML 弹幕叠加层：自研引擎，零新增依赖。工具条按钮点击开关、
  长按呼出快捷样式面板，默认值在**设置 → 弹幕设定**持久化。

</details>

## v1.0.3 — 2026-09-17

- Support for the international Bilibili app (`com.bilibili.app.in`): cached videos in
  either app are found automatically and merged into the same library. The cache folder
  structure is identical, so nothing else changes.
- Updated the release signing key (rotation).

<details><summary>简体中文</summary>

- 支持国际版 Bilibili 客户端（`com.bilibili.app.in`）：两个应用的缓存都会被自动识别
  并合并进同一个媒体库。缓存目录结构一致，其余行为不变。
- 轮换了发布签名密钥。

</details>

## v1.0.2 — 2026-08-26

- Search bar on the media library and audio pages (title / uploader / BV id / filename).
- Long-press on the audio page to multi-select and batch delete.
- Export queue is now a two-stage pipeline: one coroutine moves bytes, another verifies
  and cleans up temporary files, so finishing one item no longer blocks the next from
  starting.
- An exported video is pushed to the system media index as soon as it has been verified.
- Removed the floating export button from the home screen; exporting now starts from the
  multi-select bar only.
- UI languages: English, 日本語, 简体中文, 繁體中文, switchable in Settings.

<details><summary>简体中文</summary>

- 媒体库与音频页新增搜索栏（标题 / UP 主 / BV 号 / 文件名）
- 音频页支持长按多选、批量删除
- 导出队列改为两级流水线：搬运与「核对 + 清理临时文件」各占一条协程，
  上一项收尾不再阻塞下一项开始搬运
- 视频导出并校验通过后立刻推给系统媒体库
- 移除首页右下角的悬浮导出按钮，导出入口统一收进多选栏
- 多语言界面：英文、日文、简体中文、繁体中文，可在设置里切换

</details>

## v1.0.1 — 2026-08-25

- Reworked the project: better performance, refreshed visuals, smaller APK.
- Added the built-in player.

<details><summary>简体中文</summary>

- 重构了项目，优化了性能和视觉，缩小了体积
- 增加了内置播放器

</details>

## v1.0.0-beta — 2026-06-26

- Export video as MP4 (lossless remux through FFmpeg).
- Export audio as M4A.
- Export danmaku as XML.

<details><summary>简体中文</summary>

- 支持导出 MP4 视频（FFmpeg 无损合并）
- 支持导出 M4A 音频
- 支持导出 XML 弹幕

</details>
