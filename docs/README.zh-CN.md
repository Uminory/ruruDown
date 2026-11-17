<div align="center">

**ruruDown** — 将 bilibili 缓存视频导出为普通的 mp4 / m4a / xml / srt / txt

[![版本](https://img.shields.io/badge/version-1.0.4.3-blue)](../CHANGELOG.md)
[![Android](https://img.shields.io/badge/Android-8.0%2B-green)](#系统要求)
[![许可](https://img.shields.io/badge/license-freeware%20(closed--source)-orange)](LICENSE.zh-CN.md)
[![架构](https://img.shields.io/badge/ABI-arm64--v8a-lightgrey)](#系统要求)

[English](../README.md) · 简体中文 · [日本語](README.ja.md)

</div>

---

ruruDown 将 bilibili Android 客户端**已经缓存到本机**的视频导出为普通的
`mp4` / `m4a` / `xml` / `srt` / `txt` 文件。

> **这是一个发布仓库。** 包含 APK、配套文档和随包 FFmpeg 库的 LGPL 材料。
> ruruDown 免费闭源，详见 [LICENSE](LICENSE.zh-CN.md)。

## 下载

从 [Releases 页面](https://github.com/Uminory/ruruDown/releases) 获取 APK，
或从仓库的 [`apk/`](../apk/) 目录下载。

### 系统要求

| 项目 | 值 |
| --- | --- |
| 版本 | **1.0.4.3**（versionCode 109） |
| 文件 | [`apk/ruruDown-1.0.4.3-arm64-v8a.apk`](../apk/ruruDown-1.0.4.3-arm64-v8a.apk) |
| 大小 | 4.4 MiB |
| 架构 | `arm64-v8a` |
| 系统要求 | Android 8.0 或更高 |

## 前置条件：Shizuku

扫描和从缓存导出需要 [Shizuku](https://shizuku.rikka.app/) 授权。

1. 安装并启动 Shizuku。
2. 打开 ruruDown，在首页点击**授权**，并在 Shizuku 弹窗中允许。
3. 回到首页，点击**重新扫描**。

本地 MP4 支持直接裁切导出。访问问题可在**设置 → 诊断**中查看。

## 导出目录

默认导出到 `Download/ruruDown`。前往**设置 → 导出目录**，选择本机或可移动存储的公共目录。

## 功能

- 扫描国内版和国际版 Bilibili 客户端的缓存视频，合并到同一媒体库，支持添加自定义扫描路径。
- 支持番剧与多 P 合集浏览。
- 查看缓存、导出目录和本机存储占用。
- 媒体库和音频页支持按标题、UP 主、BV 号或文件名搜索。
- 导出视频为 `.mp4`。
- 导出音频为 `.m4a`。
- 展开详情页的**属性**，查看视频的实际媒体参数。
- 导出弹幕为 `.xml`。
- 导出缓存字幕为 `.srt` 或纯文本 `.txt`，与视频同名。
- 播放器通过 CC 开关显示视频旁的同名 `.srt` 字幕。
- 片段裁切支持导出视频或纯音频片段。
- 内置视频播放器。
- 显示弹幕，可在**设置 → 弹幕设定**中调整样式。

## 隐私

应用在本地处理媒体。**设置 → 清除本地缓存**会清理媒体扫描缓存与封面缩略图，保留导出记录。

## 许可

- **应用本体**——免费闭源软件，禁止商业使用与再打包分发，详见 [LICENSE](LICENSE.zh-CN.md)。
- **第三方组件**——详见 [THIRD-PARTY](THIRD-PARTY.zh-CN.md)。
- **FFmpeg**——以 LGPL v2.1 许可使用。许可全文、构建脚本与替换说明见
  [`ffmpeg/README.zh-CN.md`](../ffmpeg/README.zh-CN.md) 和
  [`licenses/LGPL-2.1.txt`](../licenses/LGPL-2.1.txt)。

导出的内容版权归原作者所有，请仅用于个人离线观看，遵守原作者的授权要求。

## 声明

本软件按「现状」提供，不附带任何形式的担保。与 bilibili、Shizuku 项目（RikkaApps）均无关联。
