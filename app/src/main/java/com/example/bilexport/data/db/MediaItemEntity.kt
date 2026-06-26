package com.example.bilexport.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.bilexport.core.model.ExportState

@Entity(
    tableName = "media_items",
    indices = [
        Index(value = ["avid"]),
        Index(value = ["cid"]),
        Index(value = ["source_hash"]),
        Index(value = ["export_state"]),
        Index(value = ["updated_at"])
    ]
)
data class MediaItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "avid")
    val avid: String,

    @ColumnInfo(name = "cid")
    val cid: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "part_title")
    val partTitle: String = "",

    @ColumnInfo(name = "owner_name")
    val ownerName: String = "",

    @ColumnInfo(name = "type_tag")
    val typeTag: String = "",

    @ColumnInfo(name = "quality_pithy_description")
    val qualityPithyDescription: String = "",

    @ColumnInfo(name = "video_width")
    val videoWidth: Int = 0,

    @ColumnInfo(name = "video_height")
    val videoHeight: Int = 0,

    @ColumnInfo(name = "bvid")
    val bvid: String = "",

    @ColumnInfo(name = "source_path")
    val sourcePath: String = "",

    @ColumnInfo(name = "entry_json_path")
    val entryJsonPath: String = "",

    @ColumnInfo(name = "cover_path")
    val coverPath: String = "",

    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = false,

    @ColumnInfo(name = "duration")
    val duration: Long = 0,

    @ColumnInfo(name = "size")
    val size: Long = 0,

    @ColumnInfo(name = "source_hash")
    val sourceHash: String = "",

    @ColumnInfo(name = "source_mtime")
    val sourceMtime: Long = 0,

    @ColumnInfo(name = "export_state")
    val exportState: ExportState = ExportState.NOT_EXPORTED,

    @ColumnInfo(name = "audio_exported")
    val audioExported: Boolean = false,

    @ColumnInfo(name = "danmaku_exported")
    val danmakuExported: Boolean = false,

    @ColumnInfo(name = "last_export_at")
    val lastExportAt: Long = 0,

    @ColumnInfo(name = "last_export_path")
    val lastExportPath: String = "",

    @ColumnInfo(name = "export_count")
    val exportCount: Int = 0,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)