package com.example.bilexport.data.db

import androidx.room.*
import com.example.bilexport.core.model.ExportJobStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ExportJobDao {

    @Query("SELECT * FROM export_jobs ORDER BY start_at DESC")
    fun getAll(): Flow<List<ExportJobEntity>>

    @Query("SELECT * FROM export_jobs WHERE job_id = :jobId")
    suspend fun getByJobId(jobId: String): ExportJobEntity?

    @Query("SELECT * FROM export_jobs WHERE status = :status ORDER BY start_at ASC")
    suspend fun getByStatus(status: ExportJobStatus): List<ExportJobEntity>

    @Query("SELECT * FROM export_jobs WHERE status IN ('PENDING', 'PREPARING', 'COPYING_INPUT', 'RUNNING_FFMPEG', 'VERIFYING', 'MOVING_OUTPUT') ORDER BY start_at ASC")
    suspend fun getActiveJobs(): List<ExportJobEntity>

    @Query("SELECT * FROM export_jobs WHERE media_item_id = :mediaItemId ORDER BY start_at DESC LIMIT 1")
    suspend fun getLatestByMediaItemId(mediaItemId: Long): ExportJobEntity?

    @Query("SELECT * FROM export_jobs WHERE status IN ('FAILED') ORDER BY start_at DESC")
    suspend fun getFailedJobs(): List<ExportJobEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(job: ExportJobEntity)

    @Update
    suspend fun update(job: ExportJobEntity)

    @Query("DELETE FROM export_jobs WHERE job_id = :jobId")
    suspend fun deleteByJobId(jobId: String)

    @Query("DELETE FROM export_jobs")
    suspend fun deleteAll()
}