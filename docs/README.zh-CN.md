<div align="center">

**ruruDown** — 将 bilibili 缓存视频导出为普通的 mp4 / m4a / xml

[![版本](https://img.shields.io/badge/version-1.0.2-blue)](../CHANGELOG.md)
[![Android](https://img.shields.io/badge/Android-8.0%2B-green)](#系统要求)
[![许可](https://img.shields.io/badge/license-freeware%20(closed--source)-orange)](LICENSE.zh-CN.md)
[![架构](https://img.shields.io/badge/ABI-arm64--v8a-lightgrey)](#系统要求)

[English](../README.md) · 简体中文 · [日本語](README.ja.md)

</div>

---

ruruDown 把 bilibili 客户端**已经缓存到本机**的视频导出为普通的 `mp4` / `m4a` / `xml`
文件。不联网下载、不上传、也不改动缓存目录——只读取一次，然后流复制导出，
导出结果与缓存内容完全一致。

> **这是一个纯发布仓库。** 仓库内只有 APK、配套文档，以及 LGPL 对随包 FFmpeg 库所要求的
> 材料。应用自身的源代码不公开——ruruDown 免费但闭源，详见 [LICENSE](LICENSE.zh-CN.md)。

## 下载

从 [Releases 页面](https://github.com/Uminory/ruruDown/releases) 获取 APK，
或直接从仓库的 [`apk/`](../apk/) 目录下载。

### 系统要求

| 项目      | 值                                                                         |
| ------- | ------------------------------------------------------------------------- |
| 版本      | **1.0.2**（versionCode 102）                                                |
| 文件      | [`apk/ruruDown-1.0.2-arm64-v8a.apk`](../apk/ruruDown-1.0.2-arm64-v8a.apk) |
| 大小      | 12.8 MiB（13,420,493 字节）                                                   |
| 架构      | 仅 `arm64-v8a`                                                             |
| 系统要求    | Android 8.0（API 26）或更高                                                    |
| SHA-256 | `703e28afa55e38c2271e7924a7ae161ae7ba0612e537aea29465a26369281987`        |

### 签名

APK Signature Scheme **v2 + v3**，4096-bit RSA。签名证书指纹
（**证书**的 SHA-256，不是 APK 文件的 SHA-256）：

```
f6fd5f1b610fd8b7c45740955fe6d93ef54e6739a7e9841a2659bbcaae7cb7fe
```

覆盖安装时如果系统提示签名不一致，说明不是这里发布的。

### 校验下载

```bash
cd apk && sha256sum -c ruruDown-1.0.2-arm64-v8a.apk.sha256
```

## 前置条件：Shizuku

bilibili 的缓存目录位于另一个应用的私有目录，普通权限无法访问。ruruDown 通过
[Shizuku](https://shizuku.rikka.app/) 只读访问该目录；读取基于远端文件描述符，
不通过 Binder 传输数据。

1. 安装并启动 Shizuku（ADB 或免 root 方式均可）。
2. 打开 ruruDown，在首页点击**授权**，并在 Shizuku 弹窗中允许。
3. 回到首页，点击**重新扫描**。

没有 Shizuku 时应用仍能启动，但扫描不到任何缓存；可在**设置 → 诊断**中逐项查看
失败原因。

## 导出目录

默认导出到 `Download/ruruDown`。如需更改，前往**设置 → 导出目录**，使用系统目录
选择器授权。若选择 `Download` 以外的目录，可能还需要在系统设置中额外授予
「所有文件访问权限」。

> 我们推荐更改导出目录到`Download`以外，因为某些系统版本中`Download`目录下的媒体文件存在删除后存储空间并不会释放的问题。
> 这不是ruruDown的问题，而是系统本身的漏洞。被占用的空间在**重启设备**后就会被释放。

## 功能

- 扫描缓存视频，可按时长、大小、时间排序，并显示封面、UP 主、画质、分 P 信息。
- 导出视频 `.mp4`（音视频流复制，不重编码，画质与缓存完全一致）。
- 导出音频 `.m4a`。
- 导出弹幕 `.xml`（bilibili XML 格式，播放器可直接加载）。
- 批量导出队列，由前台服务 + 通知进度承载，离开页面后仍继续运行。
- 内置播放器：视频横屏全屏播放，音频迷你播放条。
- 音频页长按可多选，支持批量删除。
- 界面语言：English / 日本語 / 简体中文 / 繁體中文，可跟随系统或手动指定。

## 隐私

应用不含任何网络代码（FFmpeg 本身也以 `--disable-network` 编译）。没有账号系统、
没有统计埋点、没有崩溃上报。所有数据都保存在本机——索引数据库与封面缩略图位于应用
私有目录，可随时通过**设置 → 清除本地缓存**清空。

## 许可

- **应用本体**——免费闭源软件，禁止商业使用与再打包分发，详见
  [LICENSE](LICENSE.zh-CN.md)。
- **第三方组件**——详见 [THIRD-PARTY](THIRD-PARTY.zh-CN.md)。
- **FFmpeg**——以 LGPL v2.1 动态链接方式使用。许可全文、构建脚本、编译配置与可替换
  说明见 [`ffmpeg/README.zh-CN.md`](../ffmpeg/README.zh-CN.md) 与
  [`licenses/LGPL-2.1.txt`](../licenses/LGPL-2.1.txt)。

导出的内容版权归原作者所有。请仅用于个人离线观看，不要公开二次分发。

## 仓库结构

```
├─ README.md            英文文档（正本）
├─ LICENSE.md           ruruDown 自身的许可
├─ THIRD-PARTY.md       随包第三方组件及其许可
├─ CHANGELOG.md         版本历史
├─ apk/                 发布的 APK 及其 .sha256 校验文件
├─ docs/                以上四份文档的翻译
├─ licenses/            许可全文（Apache-2.0、LGPL-2.1、MIT）
└─ ffmpeg/              LGPL 材料：构建脚本、编译配置、预编译 .so
```

## 声明

本软件按「现状」提供，不附带任何形式的明示或默示担保。与 bilibili、Shizuku 项目
（RikkaApps）均无任何关联。
