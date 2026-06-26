package com.example.bilexport.data.parser

import com.example.bilexport.core.constants.Paths

/**
 * 源路径解析器——根据 avid/cid/typeTag 构建各类路径。
 */
object SourcePathParser {

    /**
     * 构建视频源路径（含 video.m4s / audio.m4s）。
     */
    fun buildSourcePath(avid: String, cid: String, typeTag: String): String {
        return "${Paths.BILI_ROOT}/$avid/c_$cid/$typeTag"
    }

    /**
     * 构建 entry.json 路径。
     */
    fun buildEntryJsonPath(avid: String, cid: String, typeTag: String): String {
        return "${buildSourcePath(avid, cid, typeTag)}/entry.json"
    }

    /**
     * 构建封面路径。
     */
    fun buildCoverPath(avid: String): String {
        return "${Paths.BILI_ROOT}/$avid/cover.jpg"
    }

    /**
     * 从 entry.json 路径中提取 avid。
     */
    fun extractAvidFromEntryPath(entryJsonPath: String): String? {
        // entry/12345678/c_12345/entry.json -> 12345678
        val parts = entryJsonPath.split("/")
        val entryIndex = parts.indexOfLast { it == "entry" }
        return if (entryIndex >= 0 && entryIndex + 1 < parts.size) {
            parts[entryIndex + 1]
        } else null
    }
}