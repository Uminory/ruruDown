package com.example.bilexport.domain.repository

import com.example.bilexport.core.model.ExportJob
import com.example.bilexport.core.model.ExportJobStatus
import com.example.bilexport.core.model.ExportErrorCode
import kotlinx.coroutines.flow.Flow

interface ExportRepository {
    fun getAllJobs(): Flow<List<ExportJob>>
    suspend fun getJob(jobId: String): ExportJob?
    suspend fun getActiveJobs(): List<ExportJob>
    suspend fun getFailedJobs(): List<ExportJob>
    suspend fun insertJob(job: ExportJob)
    suspend fun updateJob(job: ExportJob)
    suspend fun updateJobStatus(jobId: String, status: ExportJobStatus, progress: Float)
    suspend fun markJobFailed(jobId: String, errorCode: ExportErrorCode, message: String)
    suspend fun markJobSuccess(jobId: String, outputPath: String)
    suspend fun deleteJob(jobId: String)
}