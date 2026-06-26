package com.example.bilexport.domain.usecase

import com.example.bilexport.core.model.ExportJob
import com.example.bilexport.core.model.ExportJobStatus
import com.example.bilexport.core.model.ExportState
import com.example.bilexport.core.model.MediaItem
import com.example.bilexport.core.model.RepeatExportPolicy
import com.example.bilexport.core.util.FileNameSanitizer
import com.example.bilexport.core.util.Result
import com.example.bilexport.domain.repository.ExportRepository
import java.util.UUID

/**
 * 创建导出任务——为单个 MediaItem 创建 ExportJob。
 */
class CreateExportJobUseCase(
    private val exportRepository: ExportRepository
) {
    suspend fun execute(
        mediaItem: MediaItem,
        outputDir: String,
        repeatPolicy: RepeatExportPolicy = RepeatExportPolicy.ASK_CONFIRMATION,
        overwriteExisting: Boolean = false,
        exportTypes: String = "VIDEO"
    ): Result<ExportJob> {
        return try {
            val jobId = UUID.randomUUID().toString()
            val title = mediaItem.title
            val partTitle = mediaItem.partTitle

            val outputName = if (partTitle.isNotEmpty()) {
                "${FileNameSanitizer.sanitize(partTitle)}.mp4"
            } else {
                "${FileNameSanitizer.sanitize(title)}.mp4"
            }

            val job = ExportJob(
                jobId = jobId,
                mediaItemId = mediaItem.id,
                sourceAvid = mediaItem.avid,
                sourceCid = mediaItem.cid,
                title = mediaItem.displayTitle,
                outputName = outputName,
                outputDir = outputDir,
                jobTempDir = "",
                status = ExportJobStatus.PENDING,
                repeatPolicy = repeatPolicy,
                startAt = System.currentTimeMillis(),
                overwriteExisting = overwriteExisting,
                exportTypes = exportTypes
            )

            exportRepository.insertJob(job)
            Result.success(job)
        } catch (e: Exception) {
            Result.error(
                com.example.bilexport.core.model.ExportErrorCode.DB_WRITE_FAILED,
                "创建任务失败: ${e.message}"
            )
        }
    }
}