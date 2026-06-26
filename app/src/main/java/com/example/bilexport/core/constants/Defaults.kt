package com.example.bilexport.core.constants

/**
 * 默认配置值。
 */
object Defaults {
    const val MAX_CONCURRENT_SCANS = 4
    const val MAX_CONCURRENT_EXPORTS = 1
    const val JOB_TIMEOUT_MS = 30 * 60 * 1000L // 30分钟
    const val COPY_BUFFER_SIZE = 8192
    const val COVER_CACHE_SIZE_MB = 200L
    const val LOG_RETENTION_DAYS = 7
    const val MAX_RETRY_COUNT = 3
    const val SCAN_INTERVAL_MS = 60 * 1000L // 扫描间隔1分钟
}