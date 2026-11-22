<div align="center">

**ruruDown** — export bilibili's cached videos and manage them offline

[![Version](https://img.shields.io/badge/version-1.0.6-beta.2-blue)](CHANGELOG.md)
[![Android](https://img.shields.io/badge/Android-8.0%2B-green)](#requirements)
[![License](https://img.shields.io/badge/license-freeware%20(closed--source)-orange)](LICENSE.md)
[![ABI](https://img.shields.io/badge/ABI-arm64--v8a-lightgrey)](#requirements)

English · [简体中文](docs/README.zh-CN.md) · [日本語](docs/README.ja.md)

</div>

---

ruruDown exports videos already cached on your device by the bilibili Android client.
You can browse and play exported files in the local media library.

> **This is a distribution-only repository.** It holds the APK, its documentation, and
> the material required by the LGPL for the bundled FFmpeg libraries. ruruDown is free
> but closed-source, see [LICENSE](LICENSE.md).

## Download

Grab the APK from the [Releases page](https://github.com/Uminory/ruruDown/releases), or
straight from [`apk/`](apk/) in this repository.

### Requirements

| Item     | Value                                                                  |
| -------- | ---------------------------------------------------------------------- |
| Version  | **1.0.6-beta.2** (versionCode 112)                                          |
| File     | [`apk/ruruDown-1.0.6-beta.2-arm64-v8a.apk`](apk/ruruDown-1.0.6-beta.2-arm64-v8a.apk) |
| Size     | 5.2 MiB                                                                |
| ABI      | `arm64-v8a` only                                                       |
| Requires | Android 8.0 or newer                                                   |

## Prerequisite: Shizuku

Scanning and cache-based export require [Shizuku](https://shizuku.rikka.app/) authorization.

1. Install and start Shizuku.
2. Open ruruDown, tap **Authorize** on the home screen, and allow the Shizuku prompt.
3. Return to the home screen and tap **Rescan**.

Export clips directly from local MP4 or MKV files. Check access in **Settings → Diagnostics**.

## Export folder

Defaults to `Download/ruruDown`. Use **Settings → Export folder** to pick any public
folder on the device or removable storage.

## What it does

- Browse cached videos from both bilibili clients, including series and multi-part collections.
- Browse, search and manage exported files in the local media library.
- Export video as MP4, audio as M4A, danmaku as XML, PB or ASS, and cached subtitles as SRT, ASS or TXT.
- Organize exports in folders by video or collection and export selected episodes as MKV in one batch.
- Choose subtitle languages, export bilingual ASS subtitles, and listen in the background with a sleep timer.
- Customize ASS styles and select video, audio, subtitle and danmaku tracks for MKV export.
- Play videos in the app with automatic resume, fullscreen controls and separate subtitle and danmaku switches.
- Trim video or audio clips, inspect media properties and check storage usage.

## Privacy

Media is processed locally on your device. **Settings → Clear local cache** wipes the media scan
cache and thumbnails; export records are kept.

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
