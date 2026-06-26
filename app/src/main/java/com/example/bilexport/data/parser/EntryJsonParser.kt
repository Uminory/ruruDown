package com.example.bilexport.data.parser

import com.example.bilexport.core.util.FileNameSanitizer
import org.json.JSONObject

/**
 * entry.json 解析器——解析 Bilibili 缓存目录中的 entry.json 文件。
 * 参考 biliexport_demo.py 中的解析逻辑。
 */
object EntryJsonParser {

    /**
     * 从 entry.json 文件内容解析出的数据。
     */
    data class ParsedEntry(
        val title: String,
        val ownerName: String,
        val partTitle: String,
        val avid: String,
        val cid: String,
        val page: Int?,
        val typeTag: String?,
        val isCompleted: Boolean,
        val seasonId: String?,
        val qualityPithyDescription: String = "",
        val width: Int = 0,
        val height: Int = 0,
        val totalBytes: Long = 0,
        val duration: Long = 0,
        val timeUpdateStamp: Long = 0,
        val bvid: String = ""
    )

    /**
     * 解析 entry.json 文本。
     */
    fun parse(jsonText: String): ParsedEntry? {
        return try {
            val data = JSONObject(jsonText)

            // 判断是否已完成缓存
            val isCompleted = data.optBoolean("is_completed", false)
            if (!isCompleted) return null

            val pageData = data.optJSONObject("page_data")
            val pageNo = pageData?.optInt("page")
            val cid = pageData?.optString("cid") ?: return null
            val width = pageData?.optInt("width", 0) ?: 0
            val height = pageData?.optInt("height", 0) ?: 0

            val avid = data.optString("avid")
            val seasonId = data.optString("season_id", null)
            val resolvedAvid = if (avid.isNotEmpty()) avid
            else if (!seasonId.isNullOrEmpty()) "s_$seasonId"
            else return null

            // 清晰度描述（如"1080P"），而非编码质量数值
            val qualityPithyDescription = data.optString("quality_pithy_description", "")
            // 文件总大小
            val totalBytes = data.optLong("total_bytes", 0)
            // 视频时长（毫秒）
            val duration = data.optLong("total_time_milli", 0)
            // 缓存更新时间戳（毫秒）
            val timeUpdateStamp = data.optLong("time_update_stamp", 0)
            val bvid = data.optString("bvid", "")

            ParsedEntry(
                title = FileNameSanitizer.sanitize(data.optString("title", "未命名")),
                ownerName = data.optString("owner_name", "未知"),
                partTitle = FileNameSanitizer.sanitize(pageData?.optString("part", "") ?: ""),
                avid = resolvedAvid,
                cid = cid,
                page = pageNo,
                typeTag = data.optString("type_tag", null),
                isCompleted = true,
                seasonId = seasonId,
                qualityPithyDescription = qualityPithyDescription,
                width = width,
                height = height,
                totalBytes = totalBytes,
                duration = duration,
                timeUpdateStamp = timeUpdateStamp,
                bvid = bvid
            )
        } catch (e: Exception) {
            null
        }
    }
}