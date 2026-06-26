package com.example.bilexport.core.util

import java.util.regex.Pattern

/**
 * 文件名清理工具——将非法字符替换为下划线。
 */
object FileNameSanitizer {

    private val ILLEGAL_CHARS = Pattern.compile("""[\\/:*?"<>|]""")

    /**
     * 清理文件名中的非法字符。
     */
    fun sanitize(name: String, fallback: String = "unnamed"): String {
        val cleaned = ILLEGAL_CHARS.matcher(name).replaceAll("_").trim()
        return cleaned.ifEmpty { fallback }
    }

    /**
     * 清理并截断文件名到指定长度。
     */
    fun sanitizeAndTruncate(name: String, maxLength: Int = 120, fallback: String = "unnamed"): String {
        val cleaned = sanitize(name, fallback)
        return if (cleaned.length > maxLength) {
            val dotIndex = cleaned.lastIndexOf('.')
            if (dotIndex > 0) {
                val namePart = cleaned.substring(0, minOf(dotIndex, maxLength - 4))
                val ext = cleaned.substring(dotIndex)
                "$namePart$ext"
            } else {
                cleaned.substring(0, maxLength)
            }
        } else cleaned
    }
}