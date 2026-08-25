# Third-Party Components

ruruDown 1.0.1 (arm64-v8a) bundles the components listed below. Except for FFmpeg,
they are all licensed under Apache License 2.0 or MIT. Both licenses permit
closed-source distribution provided that the copyright and license notices are
retained — this file serves as that notice.

## FFmpeg · LGPL v2.1 — dynamically linked

| | |
|---|---|
| Version | 7.1.5 (`release/7.1`, commit `9a4bb2c579a16b0469759743d6917d9e8e3cb8c6`) |
| Components | `libavformat.so`, `libavcodec.so`, `libavutil.so` |
| Purpose | Container handling and stream copy (`-c copy`), no re-encoding |
| License | LGPL v2.1 — [`licenses/LGPL-2.1.txt`](licenses/LGPL-2.1.txt) |
| Source & replacement | [`ffmpeg/README.md`](ffmpeg/README.md) |

Copyright belongs to the FFmpeg contributors. The source is unmodified and the
libraries are replaceable per LGPL §6; see the document linked above.

## Apache License 2.0

Full text: https://www.apache.org/licenses/LICENSE-2.0

| Component | Version | Purpose | Copyright |
|---|---|---|---|
| Kotlin stdlib | 2.2.10 | Language runtime | JetBrains s.r.o. and contributors |
| kotlinx-coroutines-android | 1.10.2 | Concurrency | JetBrains s.r.o. and contributors |
| androidx.core:core-ktx | 1.19.0 | Platform compatibility | The Android Open Source Project |
| androidx.activity:activity-compose | 1.13.0 | Activity integration | The Android Open Source Project |
| androidx.lifecycle (runtime-ktx, viewmodel-compose) | 2.11.0 | Lifecycle and ViewModel | The Android Open Source Project |
| androidx.documentfile | 1.1.0 | SAF folder access for export directory | The Android Open Source Project |
| Jetpack Compose (ui, ui-graphics, foundation, material3, tooling-preview) | BOM 2026.03.01 | UI toolkit | The Android Open Source Project |
| androidx.graphics:path (`libandroidx.graphics.path.so`) | Transitive | Path interpolation for Compose | The Android Open Source Project |
| AndroidX Media3 (exoplayer, common) | 1.11.0 | Built-in player | The Android Open Source Project |
| Coil 3 (coil-compose) | 3.4.0 | Thumbnail decoding and caching | Coil Contributors |
| Haze | 1.7.2 | Real-time blur for bottom bar and panels | Chris Banes |

## MIT License

| Component | Version | Purpose | Copyright |
|---|---|---|---|
| Shizuku API (`dev.rikka.shizuku:api`) | 13.1.5 | Rootless privileged read channel | © 2021 RikkaW |
| Shizuku Provider (`dev.rikka.shizuku:provider`) | 13.1.5 | As above | © 2021 RikkaW |

```

Permission is hereby granted, free of charge, to any person obtaining a copy of this
software and associated documentation files (the "Software"), to deal in the Software
without restriction, including without limitation the rights to use, copy, modify,
merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to the following
conditions:

The above copyright notice and this permission notice shall be included in all copies
or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

```

Shizuku is developed by RikkaApps; ruruDown is not affiliated with the project.

## The application itself

`librurumux.so` (the glue layer calling FFmpeg's public API) and all Kotlin code
belong to ruruDown, are licensed under [LICENSE.md](LICENSE.md), and their source is
not published.
