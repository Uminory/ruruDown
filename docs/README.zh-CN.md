<div align="center">

**ruruDown** — 导出 bilibili 缓存视频，在本机浏览和播放

[![版本](https://img.shields.io/badge/version-1.0.6-beta.2-blue)](../CHANGELOG.md)
[![Android](https://img.shields.io/badge/Android-8.0%2B-green)](#系统要求)
[![许可](https://img.shields.io/badge/license-freeware%20(closed--source)-orange)](LICENSE.zh-CN.md)
[![架构](https://img.shields.io/badge/ABI-arm64--v8a-lightgrey)](#系统要求)

[English](../README.md) · 简体中文 · [日本語](README.ja.md)

</div>

---

ruruDown 可导出 bilibili Android 客户端已缓存到本机的视频，并在本地媒体库中浏览、播放已导出的文件。

> **这是一个发布仓库。** 包含 APK、配套文档和随包 FFmpeg 库的 LGPL 材料。
> ruruDown 免费闭源，详见 [LICENSE](LICENSE.zh-CN.md)。

## 下载

从 [Releases 页面](https://github.com/Uminory/ruruDown/releases) 获取 APK，
或从仓库的 [`apk/`](../apk/) 目录下载。

### 系统要求

| 项目 | 值 |
| --- | --- |
| 版本 | **1.0.6-beta.2**（versionCode 112） |
| 文件 | [`apk/ruruDown-1.0.6-beta.2-arm64-v8a.apk`](../apk/ruruDown-1.0.6-beta.2-arm64-v8a.apk) |
| 大小 | 5.2 MiB |
| 架构 | `arm64-v8a` |
| 系统要求 | Android 8.0 或更高 |

## 前置条件：Shizuku

扫描和从缓存导出需要 [Shizuku](https://shizuku.rikka.app/) 授权。

1. 安装并启动 Shizuku。
2. 打开 ruruDown，在首页点击**授权**，并在 Shizuku 弹窗中允许。
3. 回到首页，点击**重新扫描**。

已导出的 MP4 或 MKV 支持直接裁切导出。访问问题可在**设置 → 诊断**中查看。

## 导出目录

默认导出到 `Download/ruruDown`。前往**设置 → 导出目录**，选择本机或可移动存储的公共目录。

## 功能

- 浏览国内版和国际版 bilibili 的缓存视频，支持番剧与多 P 合集。
- 在本地媒体库浏览、搜索和管理已导出的文件。
- 导出 MP4 视频、M4A 音频、XML、PB 或 ASS 弹幕，以及 SRT、ASS 或 TXT 字幕。
- 按视频或合集将导出文件归入子目录，支持批量导出同一合集的 MKV。
- 选择字幕语言、导出双语 ASS，并支持后台听音和定时关闭。
- 自定义 ASS 样式，选择视频、音频、字幕和弹幕轨封装为 MKV。
- 内置播放器支持自动续播、全屏播放设置，以及独立的字幕和弹幕开关。
- 裁切视频或音频片段，查看媒体属性和存储占用。

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
