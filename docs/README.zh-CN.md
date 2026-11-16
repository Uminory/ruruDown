<div align="center">

**ruruDown** — 将 bilibili 缓存视频导出为普通的 mp4 / m4a / xml / srt / txt

[![版本](https://img.shields.io/badge/version-1.0.4.2-blue)](../CHANGELOG.md)
[![Android](https://img.shields.io/badge/Android-8.0%2B-green)](#系统要求)
[![许可](https://img.shields.io/badge/license-freeware%20(closed--source)-orange)](LICENSE.zh-CN.md)
[![架构](https://img.shields.io/badge/ABI-arm64--v8a-lightgrey)](#系统要求)

[English](../README.md) · 简体中文 · [日本語](README.ja.md)

</div>

---

ruruDown 把 bilibili 客户端**已经缓存到本机**的视频导出为普通的 `mp4` / `m4a` / `xml` / `srt` / `txt`
文件。

> **这是一个纯发布仓库。** 仓库内只有 APK、配套文档，以及 LGPL 对随包 FFmpeg 库所要求的
> 材料。ruruDown 免费但闭源，详见 [LICENSE](LICENSE.zh-CN.md)。

## 下载

从 [Releases 页面](https://github.com/Uminory/ruruDown/releases) 获取 APK，
或直接从仓库的 [`apk/`](../apk/) 目录下载。

### 系统要求

| 项目   | 值                                                                         |
| ---- | ------------------------------------------------------------------------- |
| 版本   | **1.0.4.2**（versionCode 108）                                                |
| 文件   | [`apk/ruruDown-1.0.4.2-arm64-v8a.apk`](../apk/ruruDown-1.0.4.2-arm64-v8a.apk) |
| 大小   | 4.4 MiB                                                                   |
| 架构   | 仅 `arm64-v8a`                                                              |
| 系统要求 | Android 8.0 或更高                                                           |

## 前置条件：Shizuku

bilibili 的缓存目录位于私有目录，普通权限无法访问。ruruDown 通过
[Shizuku](https://shizuku.rikka.app/) 只读访问该目录。

1. 安装并启动 Shizuku。
2. 打开 ruruDown，在首页点击**授权**，并在 Shizuku 弹窗中允许。
3. 回到首页，点击**重新扫描**。

扫描和从缓存导出需要 Shizuku 授权；本机已有 MP4 的片段裁切无需 Shizuku。
访问问题可在**设置 → 诊断**中查看。

## 导出目录

默认导出到 `Download/ruruDown`。前往**设置 → 导出目录**，可选择本机或可移动存储的
任意公共目录。

## 功能

- 扫描缓存视频：国内版与国际版 Bilibili 客户端的缓存自动合并入库，还可添加自定义扫描路径。
- 媒体库与音频页支持按标题、UP 主、BV 号、文件名搜索。
- 导出视频 `.mp4`。
- 导出音频 `.m4a`。
- 详情页展开**属性**，查看视频的实际媒体参数。
- 导出弹幕 `.xml`
- 导出缓存字幕为 `.srt` 或纯文本 `.txt`，文件与视频同名。
- 播放器 CC 开关可显示视频同目录同名 `.srt`。
- 片段裁切可导出视频或仅音频片段。
- 内置播放器
- 弹幕（**设置 → 弹幕设定**调样式）。

## 隐私

应用完全不含网络功能。**设置 → 清除本地缓存**会清空媒体扫描缓存与封面缩略图；
导出记录会保留。

## 许可

- **应用本体**——免费闭源软件，禁止商业使用与再打包分发，详见
  [LICENSE](LICENSE.zh-CN.md)。
- **第三方组件**——详见 [THIRD-PARTY](THIRD-PARTY.zh-CN.md)。
- **FFmpeg**——以 LGPL v2.1 方式使用。许可全文、构建脚本与可替换说明见
  [`ffmpeg/README.zh-CN.md`](../ffmpeg/README.zh-CN.md) 与
  [`licenses/LGPL-2.1.txt`](../licenses/LGPL-2.1.txt)。

导出的内容版权归原作者所有。请仅用于个人离线观看，不要公开二次分发。

## 声明

本软件按「现状」提供，不附带任何形式的明示或默示担保。与 bilibili、Shizuku 项目
（RikkaApps）均无任何关联。