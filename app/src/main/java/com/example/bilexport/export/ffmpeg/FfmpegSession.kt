package com.example.bilexport.export.ffmpeg

/**
 * FFmpeg 会话——封装一次 FFmpeg 操作的参数与状态。
 */
data class FfmpegSession(
    val jobId: String,
    val videoPath: String,
    val audioPath: String,
    val outputPath: String,
    val options: FfmpegOptions = FfmpegOptions(),
    val command: String = "",
    val logPath: String = ""
) {
    val commandArgs: List<String>
        get() = FfmpegCommandBuilder.build(videoPath, audioPath, outputPath, options)
}