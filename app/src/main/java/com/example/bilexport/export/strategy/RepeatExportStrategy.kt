package com.example.bilexport.export.strategy

import com.example.bilexport.core.model.ExportState
import com.example.bilexport.core.model.MediaItem
import com.example.bilexport.core.model.RepeatExportPolicy

/**
 * 重复导出策略——根据策略决定是否允许重新导出。
 */
object RepeatExportStrategy {

    /**
     * 判断是否应该导出该条目。
     * @return true 表示应该导出，false 表示应跳过
     */
    fun shouldExport(item: MediaItem, policy: RepeatExportPolicy): Boolean {
        return when (policy) {
            RepeatExportPolicy.EXPORT_ALL -> true
            RepeatExportPolicy.SKIP_DUPLICATES -> item.exportState != ExportState.EXPORTED
            RepeatExportPolicy.EXPORT_ONLY_NEW -> item.exportState == ExportState.NOT_EXPORTED
            RepeatExportPolicy.ASK_CONFIRMATION -> true // 由 UI 决定
        }
    }

    /**
     * 判断是否需要显示确认对话框。
     */
    fun needsConfirmation(policy: RepeatExportPolicy): Boolean {
        return policy == RepeatExportPolicy.ASK_CONFIRMATION
    }
}