package com.example.bilexport.data.repository

import com.example.bilexport.core.model.ExportJob
import com.example.bilexport.core.model.ExportJobStatus
import com.example.bilexport.core.model.ExportErrorCode
import com.example.bilexport.data.db.AppDatabase
import com.example.bilexport.data.db.ExportJobEntity
import com.example.bilexport.domain.repository.ExportRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ExportRepositoryImpl(private val db: AppDatabase) : ExportRepository {

    private val dao = db.exportJobDao()

    override fun getAllJobs(): Flow<List<ExportJob>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    override suspend fun getJob(jobId: String): ExportJob? =
        dao.getByJobId(jobId)?.toDomain()

    override suspend fun getActiveJobs(): List<ExportJob> =
        dao.getActiveJobs().map { it.toDomain() }

    override suspend fun getFailedJobs(): List<ExportJob> =
        dao.getFailedJobs().map { it.toDomain() }

    override suspend fun insertJob(job: ExportJob) {
        dao.insert(job.toEntity())
    }

    override suspend fun updateJob(job: ExportJob) {
        dao.update(job.toEntity())
    }

    override suspend fun updateJobStatus(jobId: String, status: ExportJobStatus, progress: Float) {
        val job = dao.getByJobId(jobId) ?: return
        dao.update(job.copy(status = status, progress = progress))
    }

    override suspend fun markJobFailed(jobId: String, errorCode: ExportErrorCode, message: String) {
        val job = dao.getByJobId(jobId) ?: return
        dao.update(
            job.copy(
                status = ExportJobStatus.FAILED,
                errorCode = errorCode.name,
                errorMessage = message,
                endAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun markJobSuccess(jobId: String, outputPath: String) {
        val job = dao.getByJobId(jobId) ?: return
        dao.update(
            job.copy(
                status = ExportJobStatus.SUCCESS,
                outputPath = outputPath,
                progress = 1f,
                endAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun deleteJob(jobId: String) {
        dao.deleteByJobId(jobId)
    }
}

private fun ExportJobEntity.toDomain(): ExportJob = ExportJob(
    jobId = jobId,
    mediaItemId = mediaItemId,
    sourceAvid = sourceAvid,
    sourceCid = sourceCid,
    title = title,
    outputName = outputName,
    outputDir = outputDir,
    jobTempDir = jobTempDir,
    status = status,
    repeatPolicy = com.example.bilexport.core.model.RepeatExportPolicy.valueOf(repeatPolicy),
    startAt = startAt,
    endAt = endAt,
    progress = progress,
    errorCode = try { ExportErrorCode.valueOf(errorCode) } catch (_: Exception) { ExportErrorCode.UNKNOWN },
    errorMessage = errorMessage,
    ffmpegCommand = ffmpegCommand,
    ffmpegLogPath = ffmpegLogPath,
    outputPath = outputPath,
    overwriteExisting = overwriteExisting
)

private fun ExportJob.toEntity(): ExportJobEntity = ExportJobEntity(
    jobId = jobId,
    mediaItemId = mediaItemId,
    sourceAvid = sourceAvid,
    sourceCid = sourceCid,
    title = title,
    outputName = outputName,
    outputDir = outputDir,
    jobTempDir = jobTempDir,
    status = status,
    repeatPolicy = repeatPolicy.name,
    startAt = startAt,
    endAt = endAt,
    progress = progress,
    errorCode = errorCode.name,
    errorMessage = errorMessage,
    ffmpegCommand = ffmpegCommand,
    ffmpegLogPath = ffmpegLogPath,
    outputPath = outputPath,
    overwriteExisting = overwriteExisting
)