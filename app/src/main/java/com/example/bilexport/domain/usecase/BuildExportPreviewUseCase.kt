package com.example.bilexport.domain.usecase

import com.example.bilexport.core.model.MediaItem
import com.example.bilexport.core.model.ExportState
import com.example.bilexport.core.model.RepeatExportPolicy
import com.example.bilexport.domain.repository.MediaRepository
import kotlinx.coroutines.flow.first

/**
 * 构建导出预览——分析待导出列表，区分未导出、已导出、失败项。
 */
class BuildExportPreviewUseCase(
    private val mediaRepository: MediaRepository
) {
    data class ExportPreview(
        val newItems: List<MediaItem>,
        val exportedItems: List<MediaItem>,
        val failedItems: List<MediaItem>,
        val totalCount: Int
    ) {
        val hasDuplicates: Boolean get() = exportedItems.isNotEmpty()
        val allItems: List<MediaItem> get() = newItems + exportedItems + failedItems
    }

    suspend fun execute(
        mediaItemIds: List<Long>,
        repeatPolicy: RepeatExportPolicy = RepeatExportPolicy.ASK_CONFIRMATION
    ): ExportPreview {
        val items = mediaItemIds.mapNotNull { mediaRepository.getById(it) }

        val newItems = items.filter { it.exportState == ExportState.NOT_EXPORTED }
        val exportedItems = items.filter { it.exportState == ExportState.EXPORTED }
        val failedItems = items.filter { it.exportState == ExportState.FAILED }

        return ExportPreview(
            newItems = newItems,
            exportedItems = exportedItems,
            failedItems = failedItems,
            totalCount = items.size
        )
    }
}