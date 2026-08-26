# Changelog · 更新日志

All notable changes to ruruDown, newest first. Version numbers match the APK's
`versionName`.

## v1.0.2 — 2026-08-26

- Search bar on the media library and audio pages (title / uploader / BV id / filename).
- Long-press on the audio page to multi-select and batch delete.
- Export queue is now a two-stage pipeline: one coroutine moves bytes, another verifies
  and cleans up temporary files, so finishing one item no longer blocks the next from
  starting.
- An exported video is pushed to the system media index as soon as it has been verified,
  so the gallery picks it up immediately instead of after a reboot or a scheduled scan.
- Removed the floating export button from the home screen; exporting now starts from the
  multi-select bar only.
- UI languages: English, 日本語, 简体中文, 繁體中文, switchable in Settings.

<details><summary>简体中文</summary>

- 媒体库与音频页新增搜索栏（标题 / UP 主 / BV 号 / 文件名）
- 音频页支持长按多选、批量删除
- 导出队列改为两级流水线：搬运与「核对 + 清理临时文件」各占一条协程，
  上一项收尾不再阻塞下一项开始搬运
- 视频导出并校验通过后立刻推给系统媒体库，相册当场就能认出来，
  不用等重启或者系统的定时扫描
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
