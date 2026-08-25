<div align="center">

**ruruDown** — export bilibili's cached videos to plain mp4 / m4a / xml

[![Version](https://img.shields.io/badge/version-1.0.2-blue)](CHANGELOG.md)
[![Android](https://img.shields.io/badge/Android-8.0%2B-green)](#requirements)
[![License](https://img.shields.io/badge/license-freeware%20(closed--source)-orange)](LICENSE.md)
[![ABI](https://img.shields.io/badge/ABI-arm64--v8a-lightgrey)](#requirements)

English · [简体中文](docs/README.zh-CN.md) · [日本語](docs/README.ja.md)

</div>

---

ruruDown exports videos that the bilibili Android client has **already cached on your
device** into plain `mp4` / `m4a` / `xml` files. It downloads nothing, uploads nothing,
and never writes to the cache folder — it reads once and stream-copies the result out,
so the export is bit-identical to what was cached.

> **This is a distribution-only repository.** It holds the APK, its documentation, and
> the material required by the LGPL for the bundled FFmpeg libraries. The application's
> own source code is not published — ruruDown is free but closed-source, see
> [LICENSE](LICENSE.md).

## Download

Grab the APK from the [Releases page](https://github.com/Uminory/ruruDown/releases), or
straight from [`apk/`](apk/) in this repository.

### Requirements

| Item | Value |
|---|---|
| Version | **1.0.2** (versionCode 102) |
| File | [`apk/ruruDown-1.0.2-arm64-v8a.apk`](apk/ruruDown-1.0.2-arm64-v8a.apk) |
| Size | 12.8 MiB (13,420,493 bytes) |
| ABI | `arm64-v8a` only |
| Requires | Android 8.0 (API 26) or newer |
| SHA-256 | `703e28afa55e38c2271e7924a7ae161ae7ba0612e537aea29465a26369281987` |

### Signature

APK Signature Scheme **v2 + v3**, 4096-bit RSA. Signing certificate fingerprint
(SHA-256 of the *certificate*, not of the APK file):

```
f6fd5f1b610fd8b7c45740955fe6d93ef54e6739a7e9841a2659bbcaae7cb7fe
```

If Android reports a signature mismatch on upgrade, that APK did not come from here.

### Verify the download

```bash
cd apk && sha256sum -c ruruDown-1.0.2-arm64-v8a.apk.sha256
```

## Prerequisite: Shizuku

The bilibili cache lives in another app's private directory, which normal permissions
cannot reach. ruruDown reads it — read-only — through [Shizuku](https://shizuku.rikka.app/),
over a remote file descriptor rather than by streaming bytes across Binder.

1. Install and start Shizuku (via ADB, or the rootless method).
2. Open ruruDown, tap **Authorize** on the home screen, and allow the Shizuku prompt.
3. Return to the home screen and tap **Rescan**.

Without Shizuku the app still launches but finds nothing; **Settings → Diagnostics**
reports exactly which step is failing.

## Export folder

Defaults to `Download/ruruDown`. Change it in **Settings → Export folder** through the
system directory picker. Folders outside `Download` may additionally require the
all-files-access permission in system settings.

## What it does

- Scans cached videos — sortable by duration / size / time, with cover, uploader,
  quality and part information.
- Exports video as `.mp4` (audio and video stream copy, no re-encoding, quality
  identical to the cache).
- Exports audio as `.m4a`.
- Exports danmaku as `.xml` (bilibili XML format, loadable by players).
- Batch export queue backed by a foreground service with notification progress; keeps
  running when you leave the screen.
- Built-in player: full-screen landscape video, mini player bar for audio.
- Long-press on the audio page to multi-select and batch delete.
- UI languages: English / 日本語 / 简体中文 / 繁體中文, following the system or set manually.

## Privacy

The app contains no networking code at all (FFmpeg itself is built with
`--disable-network`). No accounts, no analytics, no crash reporting. Everything stays on
device, and **Settings → Clear local cache** wipes the index database and thumbnails.

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
