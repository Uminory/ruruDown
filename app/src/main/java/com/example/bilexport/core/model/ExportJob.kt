package com.example.bilexport.core.model

/**
 * 导出任务——队列中的最小执行单位。
 */
data class ExportJob(
    val jobId: String = "",
    val mediaItemId: Long = 0,
    val sourceAvid: String = "",
    val sourceCid: String = "",
    val title: String = "",
    val outputName: String = "",
    val outputDir: String = "",
    val jobTempDir: String = "",
    val status: ExportJobStatus = ExportJobStatus.PENDING,
    val repeatPolicy: RepeatExportPolicy = RepeatExportPolicy.ASK_CONFIRMATION,
    val startAt: Long = 0,
    val endAt: Long = 0,
    val progress: Float = 0f,
    val errorCode: ExportErrorCode = ExportErrorCode.UNKNOWN,
    val errorMessage: String = "",
    val ffmpegCommand: String = "",
    val ffmpegLogPath: String = "",
    val outputPath: String = "",
    val overwriteExisting: Boolean = false,
    val exportTypes: String = "VIDEO"
)