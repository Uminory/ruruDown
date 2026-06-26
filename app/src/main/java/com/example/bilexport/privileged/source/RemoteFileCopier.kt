package com.example.bilexport.privileged.source

import android.util.Log
import com.example.bilexport.core.constants.Paths
import com.example.bilexport.core.constants.Tags
import com.example.bilexport.privileged.shizuku.BiliFileServiceClient

class RemoteFileCopier(
    private val client: BiliFileServiceClient
) {
    fun copyMediaFiles(avid: String, subDir: String, typeTag: String, destDir: String): Int {
        Log.d(Tags.SHIZUKU, "复制 m4s: avid=$avid, subDir=$subDir, typeTag=$typeTag -> $destDir")
        return client.copyM4sFiles(avid, subDir, typeTag, destDir)
    }

    /** 读取弹幕 XML 文件的字节数组，文件位于 c_{cid}/danmaku.xml */
    fun readDanmakuXml(avid: String, subDir: String): ByteArray? {
        val content = client.readDanmakuXml(avid, subDir) ?: return null
        return content.toByteArray(Charsets.UTF_8)
    }
}
