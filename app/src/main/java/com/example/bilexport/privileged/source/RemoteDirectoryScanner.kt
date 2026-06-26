package com.example.bilexport.privileged.source

import com.example.bilexport.privileged.shizuku.BiliFileServiceClient

/**
 * 远程目录扫描器——通过 Shizuku 服务暴露的高层接口直接获取 B 站缓存结构。
 */
class RemoteDirectoryScanner(
    private val client: BiliFileServiceClient
) {
    fun listAvidDirectories(): List<String> = client.listAvids()

    fun listCidDirectories(avid: String): List<String> = client.listSubDirs(avid)

    fun listTypeTags(avid: String, subDir: String): List<String> = client.listQualityFolders(avid, subDir)

    fun readEntryJson(avid: String, subDir: String): String? = client.readEntryJson(avid, subDir)

    fun readCoverBytes(avid: String, subDir: String): ByteArray = client.readCoverBytes(avid, subDir)

    fun checkM4sFilesExist(avid: String, subDir: String, typeTag: String): Boolean =
        client.checkM4sFilesExist(avid, subDir, typeTag)
}
