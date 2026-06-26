package com.example.bilexport.core.model

/**
 * 重复导出策略
 */
enum class RepeatExportPolicy {
    /** 跳过重复 */
    SKIP_DUPLICATES,
    /** 全部导出 */
    EXPORT_ALL,
    /** 询问确认 */
    ASK_CONFIRMATION,
    /** 仅导出新项 */
    EXPORT_ONLY_NEW
}