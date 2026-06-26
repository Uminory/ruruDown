package com.example.bilexport.export.strategy

import com.example.bilexport.core.model.MediaItem
import com.example.bilexport.core.util.FileNameSanitizer

/**
 * 输出文件命名策略。
 */
object OutputNamingStrategy {

    /**
     * 生成输出文件名。
     * 规则：如果有分P标题，用 "分P标题.mp4"；否则用 "标题.mp4"。
     */
    fun generateOutputName(item: MediaItem): String {
        val name = if (item.partTitle.isNotEmpty()) {
            item.partTitle
        } else {
            item.title
        }
        return "${FileNameSanitizer.sanitize(name)}.mp4"
    }

    /**
     * 生成输出目录路径。
     * 如果有分P标题，按视频标题创建子目录。
     */
    fun generateOutputDir(baseDir: String, item: MediaItem): String {
        return if (item.partTitle.isNotEmpty()) {
            "$baseDir/${FileNameSanitizer.sanitize(item.title)}"
        } else {
            baseDir
        }
    }
}