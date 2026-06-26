package com.example.bilexport.export.pipeline

import com.example.bilexport.core.model.ExportErrorCode

/**
 * 导出结果密封类。
 */
sealed class ExportResult {
    data class Success(val outputPath: String, val elapsedMs: Long) : ExportResult()
    data class Failure(val errorCode: ExportErrorCode, val message: String) : ExportResult()
    object Cancelled : ExportResult()
}