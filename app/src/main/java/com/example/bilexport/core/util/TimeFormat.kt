package com.example.bilexport.core.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 时间格式化工具。
 */
object TimeFormat {

    private val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    fun formatDateTime(timestamp: Long): String {
        if (timestamp <= 0) return "—"
        return dateTimeFormat.format(Date(timestamp))
    }

    fun formatDate(timestamp: Long): String {
        if (timestamp <= 0) return "—"
        return dateFormat.format(Date(timestamp))
    }

    fun formatDuration(millis: Long): String {
        if (millis <= 0) return "00:00"
        val totalSeconds = millis / 1000
        val h = totalSeconds / 3600
        val m = (totalSeconds % 3600) / 60
        val s = totalSeconds % 60
        return if (h > 0) {
            String.format("%d:%02d:%02d", h, m, s)
        } else {
            String.format("%02d:%02d", m, s)
        }
    }

    fun formatFileSize(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt()
        return String.format("%.1f %s", bytes / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups])
    }

    fun formatElapsed(startMs: Long, endMs: Long): String {
        val elapsed = endMs - startMs
        return when {
            elapsed < 1000 -> "${elapsed}ms"
            elapsed < 60_000 -> "${elapsed / 1000}s"
            else -> "${elapsed / 60_000}m ${(elapsed % 60_000) / 1000}s"
        }
    }
}