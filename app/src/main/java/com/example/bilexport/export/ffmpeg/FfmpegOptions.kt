package com.example.bilexport.export.ffmpeg

/**
 * FFmpeg 重封装选项。
 */
data class FfmpegOptions(
    /** 使用流复制模式（无损重封装） */
    val useCopy: Boolean = true,
    /** 映射视频流 */
    val mapVideo: String = "0:v:0",
    /** 映射音频流 */
    val mapAudio: String = "1:a:0",
    /** 覆盖输出文件 */
    val overwrite: Boolean = true,
    /** 移除章节信息 */
    val removeChapters: Boolean = true,
    /** 移除元数据 */
    val removeMetadata: Boolean = true,
    /** 快速启动 */
    val fastStart: Boolean = true,
    /** 非交互模式 */
    val noStdin: Boolean = true,
    /** 隐藏 banner */
    val hideBanner: Boolean = true
)