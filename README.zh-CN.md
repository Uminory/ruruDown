<div align="center">

[English](/README.md) · [简体中文](/README.zh-CN.md) · [日本語](/README.ja.md)

**ruruDown** — 将 bilibili 缓存视频导出为普通的 mp4 / m4a / xml

[![版本](https://img.shields.io/badge/版本-1.0.1-blue)]()
[![Android](https://img.shields.io/badge/Android-8.0%2B-green)]()
[![许可](https://img.shields.io/badge/许可-免费闭源-orange)](../LICENSE.md)

</div>

---

## 下载
[releases](https://github.com/Uminory/ruruDown/releases)

# ruruDown

把 bilibili 客户端**已经缓存到本机**的视频导出为普通的 `mp4` / `m4a` / `xml` 文件。  
不联网下载、不上传、不改动缓存目录——只读取一次，然后流式复制导出。

> **发布仓库**  
> 本仓库只提供 APK、安装与使用说明，以及 LGPL 组件的许可与构建材料。  
> 应用自身的源代码不公开（免费闭源软件，详见 [LICENSE](LICENSE.zh-CN.md)）。

---

| 项目 | 值 |
|---|---|
| 版本 | **1.0.1** (versionCode 101) |
| 文件 | [`apk/ruruDown-1.0.1-arm64-v8a.apk`](apk/ruruDown-1.0.1-arm64-v8a.apk) |
| 大小 | 12.8 MB |
| 架构 | `arm64-v8a` only |
| 系统要求 | Android 8.0 (API 26) 或更高 |
| SHA-256 | `00433aa0843fb40aff35352ee27929c86965271b7220e56e9618115c3a949495` |

**签名**

APK Signature Scheme **v2 + v3**，4096-bit RSA。

签名证书指纹（证书的 SHA-256，**不是** APK 文件的 SHA-256）：

```

f6fd5f1b610fd8b7c45740955fe6d93ef54e6739a7e9841a2659bbcaae7cb7fe

```

覆盖安装时如果系统提示签名不一致，说明不是这里发布的包。

**校验**

```bash
sha256sum -c apk/ruruDown-1.0.1-arm64-v8a.apk.sha256
```

---

前置条件：Shizuku

bilibili 的缓存目录位于另一个应用的私有目录，普通权限无法访问。ruruDown 通过
Shizuku 提供的文件通道只读访问该目录；读取基于远端文件描述符，
不通过 Binder 传输数据。

使用步骤

1. 安装并启动 Shizuku（ADB 或免 root 方式均可）。
2. 打开 ruruDown，在首页点击「授权」，并在 Shizuku 弹窗中允许。
3. 回到首页，点击「重新扫描」。

没有 Shizuku 时应用仍能启动，但无法扫描到任何缓存；可在「设置 → 诊断」中逐项查看失败原因。

---

导出目录

默认导出到 Download/ruruDown。
如需更改，请前往「设置 → 导出目录」，使用系统目录选择器授权。
若选择 Download 以外的目录，可能还需要在系统设置中额外授予“所有文件访问权限”。

---

功能

- 扫描缓存视频，可按时长、大小、时间排序，并显示封面、UP 主、画质、分 P 信息。
- 导出 视频 .mp4（音视频流复制，不重编码，画质与缓存完全一致）。
- 导出 音频 .m4a。
- 导出 弹幕 .xml（bilibili XML 格式，播放器可直接加载）。
- 支持批量导出队列；前台服务 + 通知进度，离开页面后仍可继续运行。
- 内置播放器：视频横屏全屏播放，音频迷你播放条。
- 音频页长按可多选，支持批量删除。
- 界面语言：English / 日本語 / 简体中文 / 繁體中文，可跟随系统或手动指定。
- 
---

隐私

应用不含任何网络代码（FFmpeg 本身也以 --disable-network 编译）。
没有账号系统、没有统计埋点、没有崩溃上报。所有数据均保存在本机：索引数据库和封面缩略图位于应用私有目录。
可随时通过「设置 → 清除本地缓存」清空这些数据。

---

许可

- 应用本体：免费闭源软件，禁止商业使用与再打包分发。详见 LICENSE.md。
- 第三方组件：详见 [THIRD-PARTY](THIRD-PARTY.zh-CN.md)。
- FFmpeg：以 LGPL v2.1 动态链接方式使用。许可全文、构建脚本、编译配置与可替换说明见
  [ffmpeg/README](ffmpeg/README.zh-CN.md) 与 [LGPL-2.1](licenses/LGPL-2.1.txt)。

导出的内容版权归原作者所有。请仅用于个人离线观看，不要公开二次分发。

---

声明

本软件按「现状」提供，不附带任何形式的明示或默示担保。
与 bilibili、Shizuku 项目（RikkaApps）均无任何关联。
