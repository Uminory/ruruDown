<div align="center">

**ruruDown** — export bilibili's cached videos to plain mp4 / m4a / xml / srt / txt

[![Version](https://img.shields.io/badge/version-1.0.4.2-blue)](CHANGELOG.md)
[![Android](https://img.shields.io/badge/Android-8.0%2B-green)](#requirements)
[![License](https://img.shields.io/badge/license-freeware%20(closed--source)-orange)](LICENSE.md)
[![ABI](https://img.shields.io/badge/ABI-arm64--v8a-lightgrey)](#requirements)

English · [简体中文](docs/README.zh-CN.md) · [日本語](docs/README.ja.md)

</div>

---

ruruDown exports videos that the bilibili Android client has **already cached on your
device** into plain `mp4` / `m4a` / `xml` / `srt` / `txt` files. Exports are lossless —
the quality is identical to the cache. It downloads nothing, uploads nothing, and never
modifies the cache folder.

> **This is a distribution-only repository.** It holds the APK, its documentation, and
> the material required by the LGPL for the bundled FFmpeg libraries. ruruDown is free
> but closed-source, see [LICENSE](LICENSE.md).

## Download

Grab the APK from the [Releases page](https://github.com/Uminory/ruruDown/releases), or
straight from [`apk/`](apk/) in this repository.

### Requirements

| Item     | Value                                                                  |
| -------- | ---------------------------------------------------------------------- |
| Version  | **1.0.4.2** (versionCode 108)                                          |
| File     | [`apk/ruruDown-1.0.4.2-arm64-v8a.apk`](apk/ruruDown-1.0.4.2-arm64-v8a.apk) |
| Size     | 4.4 MiB                                                                |
| ABI      | `arm64-v8a` only                                                       |
| Requires | Android 8.0 or newer                                                   |

## Prerequisite: Shizuku

The bilibili cache lives in another app's private folder, which normal permissions
cannot reach. ruruDown reads it — read-only — through [Shizuku](https://shizuku.rikka.app/).

1. Install and start Shizuku (via ADB, or the rootless method).
2. Open ruruDown, tap **Authorize** on the home screen, and allow the Shizuku prompt.
3. Return to the home screen and tap **Rescan**.

Scanning and cache-based export require Shizuku authorization. Clips from a local MP4
already on your device can be exported without Shizuku. **Settings → Diagnostics**
reports access problems.

## Export folder

Defaults to `Download/ruruDown`. Use **Settings → Export folder** to pick any public
folder on the device or removable storage. Cloud storage, app-private folders and the
`Android` data subtree cannot be export targets. The app does not request all-files
access.

## What it does

- Scans cached videos — caches from both the domestic and international Bilibili
  clients are merged into one library, and custom scan paths cover modified clients.
  Sort by duration / size / time; see cover, uploader, quality and part information.
- Search the library and audio pages by title, uploader, BV id or filename.
- Exports video as `.mp4` with no quality loss; it shows up in the gallery right away.
- Exports audio as `.m4a`.
- Expand **Properties** in the details page to view the actual media attributes of a video.
- Exports danmaku as `.xml` (bilibili XML format, loadable by players).
- Exports cached subtitles as `.srt` or plain `.txt`, named after the video.
- Plays a same-name `.srt` beside the video with a CC switch — including subtitle files
  you add yourself.
- Trim clips with millisecond time inputs and the player's Current time button; export
  video or audio-only clips.
- Export records survive cache clearing, and changing the export folder keeps your
  existing exports recognized.
- Batch export queue with notification progress; keeps running when you leave the screen.
- Built-in player: portrait full screen for tall videos, landscape for wide videos, and
  a mini player bar for audio. Pauses when headphones disconnect; replays from the start
  after finishing.
- Danmaku overlay across the whole player, including black bars (toggle from the player
  toolbar, styled in **Settings → Danmaku**).
- Long-press on the audio page to multi-select and batch delete.
- Remembers your scroll position in the library, audio and settings pages.
- UI languages: English / 日本語 / 简体中文 / 繁體中文, following the system or set manually.

## Privacy

The app has no networking at all. No accounts, no analytics, no crash reporting.
Everything stays on device. **Settings → Clear local cache** wipes the media scan cache
and thumbnails; export records are kept so existing exports remain recognizable.

## Licensing

- **Application** — free but closed-source; commercial use and redistribution are
  prohibited. See [LICENSE](LICENSE.md).
- **Third-party components** — see [THIRD-PARTY](THIRD-PARTY.md).
- **FFmpeg** — used under LGPL v2.1. License text, build script and replacement
  instructions are in [`ffmpeg/README.md`](ffmpeg/README.md) and
  [`licenses/LGPL-2.1.txt`](licenses/LGPL-2.1.txt).

Exported content belongs to its original authors — keep it for personal offline viewing
and do not redistribute it.

## Disclaimer

Provided "as is", without warranty of any kind. Not affiliated with bilibili, nor with
the Shizuku project (RikkaApps).