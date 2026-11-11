<div align="center">

**ruruDown** — export bilibili's cached videos to plain mp4 / m4a / xml

[![Version](https://img.shields.io/badge/version-1.0.4-blue)](CHANGELOG.md)
[![Android](https://img.shields.io/badge/Android-8.0%2B-green)](#requirements)
[![License](https://img.shields.io/badge/license-freeware%20(closed--source)-orange)](LICENSE.md)
[![ABI](https://img.shields.io/badge/ABI-arm64--v8a-lightgrey)](#requirements)

English · [简体中文](docs/README.zh-CN.md) · [日本語](docs/README.ja.md)

</div>

---

ruruDown exports videos that the bilibili Android client has **already cached on your
device** into plain `mp4` / `m4a` / `xml` files. It downloads nothing, uploads nothing,
and never writes to the cache folder. It preserves encoded audio/video through stream
copy, without re-encoding.

> **This is a distribution-only repository.** It holds the APK, its documentation, and
> the material required by the LGPL for the bundled FFmpeg libraries. The application's
> own source code is not published — ruruDown is free but closed-source, see
> [LICENSE](LICENSE.md).

## Download

Grab the APK from the [Releases page](https://github.com/Uminory/ruruDown/releases), or
straight from [`apk/`](apk/) in this repository.

### Requirements

| Item     | Value                                                                  |
| -------- | ---------------------------------------------------------------------- |
| Version  | **1.0.4** (versionCode 106)                                          |
| File     | [`apk/ruruDown-1.0.4-arm64-v8a.apk`](apk/ruruDown-1.0.4-arm64-v8a.apk) |
| Size     | 4.4 MiB (4,597,645 bytes)                                            |
| ABI      | `arm64-v8a` only                                                       |
| Requires | Android 8.0 (API 26) or newer                                          |
| SHA-256  | `08bea1923c1e4717e838683a74a33ff9dcce88e76d894b229b612780af1a96f6`    |

### Signature

APK Signature Scheme **v2 + v3**, 2048-bit RSA. Signing certificate fingerprint
(SHA-256 of the *certificate*, not of the APK file):

```
d54c27cffe944f903b49a1add77daa9a02a73b7476680003da574ef73c526a23
```

If Android reports a signature mismatch on upgrade, that APK did not come from here.

### Verify the download

```bash
cd apk && sha256sum -c ruruDown-1.0.4-arm64-v8a.apk.sha256
```

## Prerequisite: Shizuku

The bilibili cache lives in another app's private directory, which normal permissions
cannot reach. ruruDown reads it — read-only — through [Shizuku](https://shizuku.rikka.app/),
over a remote file descriptor rather than by streaming bytes across Binder.

1. Install and start Shizuku (via ADB, or the rootless method).
2. Open ruruDown, tap **Authorize** on the home screen, and allow the Shizuku prompt.
3. Return to the home screen and tap **Rescan**.

Scanning and cache-based export require Shizuku authorization. Clips from a recognized
local MP4 can be exported without Shizuku. **Settings → Diagnostics** reports access problems.

## Export folder

Defaults to `Download/ruruDown`. Use **Settings → Export folder** to select a public
folder on the device or removable storage through the system directory picker.
Only the system external-storage provider is supported; cloud storage, app-private
folders and the `Android` subtree cannot be export targets. The app does not request
all-files access. Android 8–10 asks for legacy storage permission when the default
folder needs it; a SAF-selected public folder uses its directory grant instead.

Selecting the same physical folder through another access method preserves verified
asset ownership without copying the files. Interrupted writes use recorded staging
files; system timeouts and unresponsive work stop automatically, with the reason in
the export queue.

## What it does

- Scans cached videos (caches from both the domestic and international Bilibili
  clients are auto-detected and merged into one library, plus custom scan paths for
  modified clients) — sortable by duration / size / time, with cover, uploader,
  quality and part information.
- Exports video as `.mp4` (audio and video stream copy, no re-encoding, quality
  identical to the cache), and hands it straight to the system media index so it shows
  up in the gallery without waiting for a reboot or a scheduled scan.
- Exports audio as `.m4a`.
- Exports danmaku as `.xml` (bilibili XML format, loadable by players).
- Trim clips with millisecond time inputs and the player’s Current time button. Prefer
  recognized local MP4 files, with cache fallback; export video or audio-only clips.
- Keep successful export records across cache clearing, with clean filenames and verified
  asset migration when changing the export folder.
- Batch export queue backed by a foreground service with notification progress; keeps
  running when you leave the screen.
- Built-in player: portrait full screen for tall videos, landscape for square or wide
  videos, and a mini player bar for audio. Audio focus and headset disconnection are
  handled by the system player; pressing play after completion replays from the start.
- XML danmaku uses the entire player area, including black bars (toggle from the
  player toolbar, styled in **Settings → Danmaku**). Paused rendering is demand-driven;
  malformed or over-budget playback XML is silently disabled. Raw XML export is unaffected.
- Long-press on the audio page to multi-select and batch delete.
- Keep the library, audio and settings scroll positions when returning from another page or switching tabs.
- UI languages: English / 日本語 / 简体中文 / 繁體中文, following the system or set manually.

## Privacy

The app contains no networking code at all (FFmpeg itself is built with
`--disable-network`). No accounts, no analytics, no crash reporting. Everything stays on
device. **Settings → Clear local cache** wipes the media scan cache and thumbnails;
successful export records are retained so existing exports remain recognizable.

## Licensing

- **Application** — free but closed-source; commercial use and redistribution are
  prohibited. See [LICENSE](LICENSE.md).
- **Third-party components** — see [THIRD-PARTY](THIRD-PARTY.md).
- **FFmpeg** — used via dynamic linking under LGPL v2.1. License text, build script,
  configuration and replacement instructions are in
  [`ffmpeg/README.md`](ffmpeg/README.md) and
  [`licenses/LGPL-2.1.txt`](licenses/LGPL-2.1.txt).

Exported content belongs to its original authors — keep it for personal offline viewing
and do not redistribute it.

## Repository layout

```
├─ README.md            English documentation (canonical)
├─ LICENSE.md           ruruDown's own licence
├─ THIRD-PARTY.md       bundled third-party components and their licences
├─ CHANGELOG.md         release history
├─ apk/                 released APK + its .sha256 checksum
├─ docs/                translations of the four documents above
├─ licenses/            full licence texts (Apache-2.0, LGPL-2.1, MIT)
└─ ffmpeg/              LGPL material: build script, configuration, prebuilt .so
```

## Disclaimer

Provided "as is", without warranty of any kind. Not affiliated with bilibili, nor with
the Shizuku project (RikkaApps).
