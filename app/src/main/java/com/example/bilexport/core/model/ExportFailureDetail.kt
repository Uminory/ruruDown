package com.example.bilexport.core.model

/**
 * 结构化 FFmpeg 导出错误，方便诊断。
 */
data class ExportFailureDetail(
    val stage: String,
    val errorCode: Int? = null,
    val message: String,
    val inputVideoPath: String? = null,
    val inputAudioPath: String? = null,
    val outputPath: String? = null,
    val ffmpegLog: String? = null,
    val streamInfo: String? = null
) {
    fun toSummary(): String = buildString {
        appendLine("[阶段] $stage")
        errorCode?.let { appendLine("[错误码] $it") }
        appendLine("[消息] $message")
        inputVideoPath?.let { appendLine("[输入视频] $it") }
        inputAudioPath?.let { appendLine("[输入音频] $it") }
        outputPath?.let { appendLine("[输出路径] $it") }
        streamInfo?.let { appendLine("[流信息] $it") }
        ffmpegLog?.let { appendLine("[FFmpeg日志]\n$it") }
    }
}
