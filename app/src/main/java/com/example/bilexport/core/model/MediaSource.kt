package com.example.bilexport.core.model

/**
 * 媒体源类型，用于区分不同视频平台的缓存结构。
 */
data class MediaSource(
    val avid: String,
    val cid: String,
    val typeTag: String,
    val rootPath: String,
    val entryJsonPath: String
) {
    val sourceDirPath: String
        get() = "$rootPath/$avid/c_$cid/$typeTag"

    val coverPath: String
        get() = "$rootPath/$avid/cover.jpg"
}