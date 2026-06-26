package com.example.bilexport.core.model

/**
 * 导出状态（用密封类明确表达，禁止只使用布尔值）
 */
enum class ExportState {
    /** 未导出 */
    NOT_EXPORTED,
    /** 已导出 */
    EXPORTED,
    /** 导出失败 */
    FAILED,
    /** 正在导出 */
    EXPORTING;

    val isExported: Boolean get() = this == EXPORTED
    val isExporting: Boolean get() = this == EXPORTING
}