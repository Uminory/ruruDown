package com.example.bilexport.data.cache

import android.content.Context
import java.io.File

/**
 * 封面缓存——以 avid_cid 为 key 管理本地缓存文件。
 * 缓存文件由 CoverSyncManager 通过 App 进程写入（字节来自 BiliFileService Binder 返回）。
 * Coil 通过 getFilePath 加载。
 */
class CoverCache(context: Context) {

    private val cacheDir: File = File(context.cacheDir, "covers").also { it.mkdirs() }

    /** 以 avid_cid.jpg 格式命名 */
    fun filePath(avid: String, cid: String): String {
        return File(cacheDir, "${avid}_${cid}.jpg").absolutePath
    }

    fun getFile(avid: String, cid: String): File {
        return File(cacheDir, "${avid}_${cid}.jpg")
    }

    /** 根据 id (格式: "avid_cid") 获取 File */
    fun getFileById(id: String): File {
        return File(cacheDir, "$id.jpg")
    }

    fun contains(avid: String, cid: String): Boolean {
        return getFile(avid, cid).isFile
    }

    fun mtime(avid: String, cid: String): Long {
        val f = getFile(avid, cid)
        return if (f.isFile) f.lastModified() else 0L
    }

    fun clear() {
        cacheDir.listFiles()?.forEach { it.delete() }
    }

    fun remove(avid: String, cid: String) {
        getFile(avid, cid).delete()
    }

    fun size(): Long {
        return cacheDir.listFiles()?.sumOf { it.length() } ?: 0
    }
}
