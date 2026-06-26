package com.example.bilexport.export.ffmpeg

/**
 * FFmpeg 媒体探测结果。
 */
data class MediaProbeResult(
    val hasVideo: Boolean = false,
    val hasAudio: Boolean = false,
    val videoCodec: String = "",
    val audioCodec: String = "",
    val width: Int = 0,
    val height: Int = 0,
    val duration: Long = 0,
    val bitRate: Long = 0
)