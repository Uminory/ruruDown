# 第三方组件声明

ruruDown 1.0.1（arm64-v8a）打包了下列第三方组件。除 FFmpeg 之外，其余组件均采用
Apache License 2.0 或 MIT 许可。这两种许可证均允许闭源分发，前提是保留版权与许可
声明——本文件即为此声明。

## FFmpeg · LGPL v2.1 —— 动态链接

| | |
|---|---|
| 版本 | 7.1.5 (`release/7.1`, commit `9a4bb2c579a16b0469759743d6917d9e8e3cb8c6`) |
| 组件 | `libavformat.so`, `libavcodec.so`, `libavutil.so` |
| 用途 | 容器处理与流复制（`-c copy`），不重编码 |
| 许可 | LGPL v2.1 — [`licenses/LGPL-2.1.txt`](licenses/LGPL-2.1.txt) |
| 源码与替换说明 | [`ffmpeg/README`](ffmpeg/README.zh-CN.md) |

版权归 FFmpeg 项目贡献者所有。源码未经修改，库可按 LGPL §6 替换；详见上文链接文档。

## Apache License 2.0

许可全文：https://www.apache.org/licenses/LICENSE-2.0

| 组件 | 版本 | 用途 | 版权 |
|---|---|---|---|
| Kotlin stdlib | 2.2.10 | 语言运行时 | JetBrains s.r.o. and contributors |
| kotlinx-coroutines-android | 1.10.2 | 并发 | JetBrains s.r.o. and contributors |
| androidx.core:core-ktx | 1.19.0 | 平台兼容 | The Android Open Source Project |
| androidx.activity:activity-compose | 1.13.0 | Activity 集成 | The Android Open Source Project |
| androidx.lifecycle (runtime-ktx, viewmodel-compose) | 2.11.0 | 生命周期与 ViewModel | The Android Open Source Project |
| androidx.documentfile | 1.1.0 | 导出目录的 SAF 访问 | The Android Open Source Project |
| Jetpack Compose (ui, ui-graphics, foundation, material3, tooling-preview) | BOM 2026.03.01 | 界面框架 | The Android Open Source Project |
| androidx.graphics:path (`libandroidx.graphics.path.so`) | 传递依赖 | Compose 的路径解析 | The Android Open Source Project |
| AndroidX Media3 (exoplayer, common) | 1.11.0 | 内置播放器 | The Android Open Source Project |
| Coil 3 (coil-compose) | 3.4.0 | 封面解码与内存缓存 | Coil Contributors |
| Haze | 1.7.2 | 底栏与面板的实时模糊 | Chris Banes |

## MIT License

| 组件 | 版本 | 用途 | 版权 |
|---|---|---|---|
| Shizuku API (`dev.rikka.shizuku:api`) | 13.1.5 | 免 root 的系统级文件读取通道 | © 2021 RikkaW |
| Shizuku Provider (`dev.rikka.shizuku:provider`) | 13.1.5 | 同上 | © 2021 RikkaW |

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

Shizuku 由 RikkaApps 开发，ruruDown 与 Shizuku 项目无任何关联。

## 应用自身

`librurumux.so`（调用 FFmpeg 公开 API 的胶水层）与全部 Kotlin 代码属于 ruruDown，
按 [LICENSE](LICENSE.zh-CN.md) 授权，源代码不公开。
