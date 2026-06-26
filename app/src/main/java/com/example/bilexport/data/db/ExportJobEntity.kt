package com.example.bilexport.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.bilexport.core.model.ExportJobStatus
import com.example.bilexport.core.model.ExportErrorCode

@Entity(
    tableName = "export_jobs",
    indices = [
        Index(value = ["status"]),
        Index(value = ["start_at"]),
        Index(value = ["media_item_id"]),
        Index(value = ["job_id"], unique = true)
    ]
)
data class ExportJobEntity(
    @PrimaryKey
    @ColumnInfo(name = "job_id")
    val jobId: String,

    @ColumnInfo(name = "media_item_id")
    val mediaItemId: Long = 0,

    @ColumnInfo(name = "source_avid")
    val sourceAvid: String = "",

    @ColumnInfo(name = "source_cid")
    val sourceCid: String = "",

    @ColumnInfo(name = "title")
    val title: String = "",

    @ColumnInfo(name = "output_name")
    val outputName: String = "",

    @ColumnInfo(name = "output_dir")
    val outputDir: String = "",

    @ColumnInfo(name = "job_temp_dir")
    val jobTempDir: String = "",

    @ColumnInfo(name = "status")
    val status: ExportJobStatus = ExportJobStatus.PENDING,

    @ColumnInfo(name = "repeat_policy")
    val repeatPolicy: String = "ASK_CONFIRMATION",

    @ColumnInfo(name = "start_at")
    val startAt: Long = 0,

    @ColumnInfo(name = "end_at")
    val endAt: Long = 0,

    @ColumnInfo(name = "progress")
    val progress: Float = 0f,

    @ColumnInfo(name = "error_code")
    val errorCode: String = "UNKNOWN",

    @ColumnInfo(name = "error_message")
    val errorMessage: String = "",

    @ColumnInfo(name = "ffmpeg_command")
    val ffmpegCommand: String = "",

    @ColumnInfo(name = "ffmpeg_log_path")
    val ffmpegLogPath: String = "",

    @ColumnInfo(name = "output_path")
    val outputPath: String = "",
    @ColumnInfo(name = "overwrite_existing")
    val overwriteExisting: Boolean = false
)