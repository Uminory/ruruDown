package com.example.bilexport.core.model

import androidx.compose.runtime.Immutable
import java.io.File

@Immutable

/**
 * 媒体条目核心数据模型。
 * 从 entry.json 解析而来，代表一个可导出的视频缓存实体。
 */
data class MediaItem(
    val id: Long = 0,
    val avid: String,
    val cid: String,
    val title: String,
    val partTitle: String = "",
    val ownerName: String = "",
    val typeTag: String = "",
    val qualityPithyDescription: String = "",
    val width: Int = 0,
    val height: Int = 0,
    val sourcePath: String = "",
    val entryJsonPath: String = "",
    val coverPath: String = "",
    val isCompleted: Boolean = false,
    val duration: Long = 0,
    val size: Long = 0,
    val sourceHash: String = "",
    val sourceMtime: Long = 0,
    val exportState: ExportState = ExportState.NOT_EXPORTED,
    val audioExported: Boolean = false,
    val danmakuExported: Boolean = false,
    val lastExportAt: Long = 0,
    val lastExportPath: String = "",
    val exportCount: Int = 0,
    val bvid: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    /** 完整的显示标题（含分P标题） */
    val displayTitle: String
        get() = if (partTitle.isNotEmpty()) "$title - $partTitle" else title

    /** 分辨率文本，例如 "3840×2160" */
    val resolutionText: String
        get() = if (width > 0 && height > 0) "${width}×${height}" else ""

    /** 清晰度标签，优先使用 quality_pithy_description */
    val qualityLabel: String
        get() = qualityPithyDescription.ifEmpty { typeTag }

    /** 视频源路径 */
    val videoSourcePath: String
        get() = File(sourcePath, "video.m4s").absolutePath

    /** 音频源路径 */
    val audioSourcePath: String
        get() = File(sourcePath, "audio.m4s").absolutePath
}