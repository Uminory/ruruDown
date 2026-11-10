# Changelog · 更新日志

All notable changes to ruruDown, newest first. Version numbers match the APK's
`versionName`.

## v1.0.4-beta.2 — 2026-11-10

- Add a Clip trimming tab with independent player, millisecond time inputs and a
  focus-aware Current time button. Stream-copy MP4 or audio-only M4A clips, with
  actual boundaries shown in the export queue.
- Prefer recognized local MP4 inputs, falling back to the Bilibili cache when needed.
- Track successful exports in a persistent private index; preserve clean title-based
  filenames and keep clip exports separate from full-video completion marks.
- Offer verified asset migration when switching export folders and protect stale
  selections and directory operations.
- Show clip processing, actual save progress, completed bars and output sizes.
- Improve export failure cleanup and reliability. Internal versionCode: 105.

<details><summary>简体中文</summary>

- 新增“片段裁切”标签，播放器独立于标签切换；支持毫秒时间输入和随输入焦点启用的
  “当前时间”按钮，可无损导出 MP4 或仅音频 M4A，队列显示实际导出范围。
- 优先使用已识别的本地 MP4，不可用时回退 Bilibili 缓存。
- 使用私有持久索引识别成功导出的资产；文件名保持纯净标题，片段不计作完整视频已导出。
- 切换导出目录时可一键校验迁移旧资产，保护旧选择和目录操作。
- 补齐片段处理中、实际保存进度、完成进度条和文件大小显示。
- 改善导出失败清理及可靠性。内部版本号 versionCode 为 105。

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
