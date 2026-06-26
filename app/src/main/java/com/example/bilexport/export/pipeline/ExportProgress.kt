package com.example.bilexport.export.pipeline

/**
 * 导出进度封装。
 */
data class ExportProgress(
    val jobId: String,
    val stage: ExportStage,
    val percent: Float = 0f,
    val message: String = ""
)

enum class ExportStage {
    INIT, COPYING, REMUXING, VERIFYING, MOVING, DONE
}