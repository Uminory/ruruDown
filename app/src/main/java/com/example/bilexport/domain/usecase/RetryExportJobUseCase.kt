package com.example.bilexport.domain.usecase

import com.example.bilexport.core.model.ExportJobStatus
import com.example.bilexport.core.model.ExportErrorCode
import com.example.bilexport.core.model.ExportState
import com.example.bilexport.core.util.Result
import com.example.bilexport.domain.repository.ExportRepository
import com.example.bilexport.domain.repository.MediaRepository

/**
 * 重试失败的导出任务。
 */
class RetryExportJobUseCase(
    private val exportRepository: ExportRepository,
    private val mediaRepository: MediaRepository,
    private val runExportJobUseCase: RunExportJobUseCase
) {
    suspend fun execute(jobId: String): Result<com.example.bilexport.core.model.ExportJob> {
        val job = exportRepository.getJob(jobId)
            ?: return Result.error(ExportErrorCode.UNKNOWN, "任务不存在")

        if (job.status != ExportJobStatus.FAILED && job.status != ExportJobStatus.CANCELLED) {
            return Result.error(ExportErrorCode.UNKNOWN, "只能重试失败或取消的任务")
        }

        // 重置 job 状态
        val resetJob = job.copy(
            status = ExportJobStatus.PENDING,
            errorCode = ExportErrorCode.UNKNOWN,
            errorMessage = "",
            progress = 0f,
            startAt = System.currentTimeMillis(),
            endAt = 0
        )
        exportRepository.updateJob(resetJob)

        // 重置 MediaItem 状态
        val mediaItem = mediaRepository.getById(job.mediaItemId)
        if (mediaItem != null) {
            mediaRepository.updateExportState(mediaItem.id, ExportState.NOT_EXPORTED, "")
        }

        // 重新执行
        return runExportJobUseCase.execute(resetJob)
    }
}