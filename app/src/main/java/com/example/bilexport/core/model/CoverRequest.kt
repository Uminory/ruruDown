package com.example.bilexport.core.model

/**
 * 封面同步请求——一次批量同步中的单个条目。
 */
data class CoverRequest(
    val avid: String,
    val cid: String,
    val subDir: String,
    val destPath: String   // 本地缓存目标路径 (cache/covers/{avid}_{cid}.jpg)
)
