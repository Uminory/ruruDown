<div align="center">

[English](README.md) · [简体中文](README.zh-CN.md) · [日本語](README.ja.md)

**ruruDown** — Export bilibili cached videos to plain mp4 / m4a / xml

[![Version](https://img.shields.io/badge/version-1.0.1-blue)]()
[![Android](https://img.shields.io/badge/Android-8.0%2B-green)]()
[![License](https://img.shields.io/badge/license-Freeware%20(closed--source)-orange)](LICENSE.md)
[![ABI](https://img.shields.io/badge/ABI-arm64--v8a-lightgrey)]()

</div>

---

## Download
[releases](https://github.com/Uminory/ruruDown/releases)

# ruruDown

Export videos that the bilibili Android client has **already cached on your device**
into plain `mp4` / `m4a` / `xml` files. No network downloads, no uploads, no writes
to the cache folder — it reads once and stream-copies out.

> **Distribution-only repository**  
> This repository contains only the APK, documentation, and the LGPL component
> material. The application's own source code is not published (free but
> closed-source — see [LICENSE.md](LICENSE.md)).

---

| Item | Value |
|---|---|
| Version | **1.0.1** (versionCode 101) |
| File | [`apk/ruruDown-1.0.1-arm64-v8a.apk`](apk/ruruDown-1.0.1-arm64-v8a.apk) |
| Size | 12.8 MB |
| ABI | `arm64-v8a` only |
| Requires | Android 8.0 (API 26) or newer |
| SHA-256 | `00433aa0843fb40aff35352ee27929c86965271b7220e56e9618115c3a949495` |

**Signature**

APK Signature Scheme **v2 + v3**, 4096-bit RSA.

Signing certificate fingerprint (SHA-256 of the certificate, **not** of the APK file):

```

f6fd5f1b610fd8b7c45740955fe6d93ef54e6739a7e9841a2659bbcaae7cb7fe

```

If Android complains about a signature mismatch on upgrade, that APK did not come from here.

**Verify**

```bash
sha256sum -c apk/ruruDown-1.0.1-arm64-v8a.apk.sha256
```

---

Prerequisite: Shizuku

The bilibili cache lives in another app's private directory, which normal permissions
cannot reach. ruruDown reads it — read-only — through Shizuku,
over a remote file descriptor rather than by streaming bytes across Binder.

Setup

1. Install and start Shizuku (via ADB or the rootless method).
2. Open ruruDown, tap "Authorize" on the home screen, and allow the Shizuku prompt.
3. Return to the home screen and tap "Rescan".

Without Shizuku the app still launches but finds nothing; Settings → Diagnostics
reports exactly which step is failing.

---

Export Folder

Defaults to Download/ruruDown.
Change it in Settings → Export folder via the system directory picker; folders outside
Download may additionally require the all-files-access permission in system settings.

---

What It Does

· Scans cached videos, sortable by duration / size / time, with cover, uploader,
  quality, and part information.
· Exports video .mp4 (audio & video stream copy, no re-encoding, quality
  identical to cache).
· Exports audio .m4a.
· Exports danmaku .xml (bilibili XML format, loadable by players).
· Batch export queue with foreground service + notification progress; continues
  when leaving the screen.
· Built-in player: video full‑screen landscape, audio mini player bar.
· Long-press on audio page to multi-select and batch delete.
· UI languages: English / 日本語 / 简体中文 / 繁體中文, following system or manually set.

Not supported yet

· Converting danmaku.pb-only caches into XML is not supported yet.

---

Privacy

The app contains no networking code at all (FFmpeg itself is built with
--disable-network). No accounts, no analytics, no crash reporting. Everything stays
on device; Settings → Clear local cache wipes the index database and thumbnails.

---

Licensing

· Application: free but closed-source; commercial use and redistribution are
  prohibited. See LICENSE.md.
· Third-party components: see THIRD-PARTY.md.
· FFmpeg: used via dynamic linking under LGPL v2.1. License text, build
  scripts, configuration, and replacement instructions are available in
  ffmpeg/README.md and licenses/LGPL-2.1.txt.

Exported content belongs to its original authors — keep it for personal offline
viewing and do not redistribute it.

---

Disclaimer

Provided "as is", without warranty of any kind.
Not affiliated with bilibili, nor with the Shizuku project (RikkaApps).
