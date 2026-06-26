package com.example.bilexport.domain.usecase

import com.example.bilexport.core.model.*
import com.example.bilexport.core.util.Result
import com.example.bilexport.domain.repository.ExportRepository
import com.example.bilexport.domain.repository.MediaRepository
import com.example.bilexport.export.pipeline.ExportPipeline

class RunExportJobUseCase(
    private val exportRepository: ExportRepository,
    private val mediaRepository: MediaRepository,
    private val exportPipeline: ExportPipeline
) {
    suspend fun execute(job: ExportJob): Result<ExportJob> {
        exportRepository.updateJobStatus(job.jobId, ExportJobStatus.PREPARING, 0f)

        val mediaItem = mediaRepository.getById(job.mediaItemId)
            ?: return Result.error(ExportErrorCode.UNKNOWN, "媒体条目不存在")

        val types = try {
            job.exportTypes.split(",").map { ExportType.valueOf(it.trim()) }.toSet()
        } catch (_: Exception) {
            setOf(ExportType.VIDEO)
        }

        // 只在包含视频导出时才标记为导出中
        if (types.contains(ExportType.VIDEO)) {
            mediaRepository.updateExportState(mediaItem.id, ExportState.EXPORTING, "")
        }

        val result = exportPipeline.execute(job, mediaItem, types)

        return when (result) {
            is com.example.bilexport.export.pipeline.ExportResult.Success -> {
                exportRepository.markJobSuccess(job.jobId, result.outputPath)

                val entity = mediaRepository.getById(mediaItem.id)
                if (entity != null) {
                    val updated = entity.copy(
                        exportState = if (types.contains(ExportType.VIDEO))
                            ExportState.EXPORTED else ExportState.NOT_EXPORTED,
                        audioExported = entity.audioExported || types.contains(ExportType.AUDIO),
                        danmakuExported = entity.danmakuExported || types.contains(ExportType.DANMAKU),
                        lastExportPath = result.outputPath,
                        lastExportAt = System.currentTimeMillis(),
                        exportCount = if (types.contains(ExportType.VIDEO))
                            entity.exportCount + 1 else entity.exportCount,
                        updatedAt = System.currentTimeMillis()
                    )
                    mediaRepository.update(updated)
                }
                Result.success(exportRepository.getJob(job.jobId) ?: job)
            }
            is com.example.bilexport.export.pipeline.ExportResult.Failure -> {
                exportRepository.markJobFailed(job.jobId, result.errorCode, result.message)
                // 只在包含视频导出时将视频状态标记为失败
                if (types.contains(ExportType.VIDEO)) {
                    mediaRepository.updateExportState(mediaItem.id, ExportState.FAILED, "")
                } else {
                    // 非视频导出失败时将媒体状态恢复
                    mediaRepository.updateExportState(mediaItem.id, ExportState.NOT_EXPORTED, "")
                }
                Result.error(result.errorCode, result.message)
            }
            is com.example.bilexport.export.pipeline.ExportResult.Cancelled -> {
                exportRepository.updateJobStatus(job.jobId, ExportJobStatus.CANCELLED, 0f)
                if (types.contains(ExportType.VIDEO)) {
                    mediaRepository.updateExportState(mediaItem.id, ExportState.FAILED, "")
                }
                Result.error(ExportErrorCode.CANCELLED, "导出已取消")
            }
        }
    }
}
