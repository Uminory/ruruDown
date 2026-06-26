package com.example.bilexport.privileged.shizuku

import android.util.Log
import com.example.bilexport.core.constants.Tags
import java.io.File

/**
 * Shizuku 用户服务——在特权进程中直接访问 B 站缓存目录。
 * 使用高层 avid-aware 接口，避免多次 IPC。
 * 事务码 16777115 用于 Shizuku 销毁回调。
 */
class BiliFileService : IBiliFileService.Stub() {

    companion object {
        private const val BILI_ROOT = "/storage/emulated/0/Android/data/tv.danmaku.bili/download"
    }

    override fun listAvids(): MutableList<String> {
        val dir = File(BILI_ROOT)
        if (!dir.isDirectory) {
            Log.w(Tags.SHIZUKU, "BILI_ROOT 不是目录: $BILI_ROOT")
            return mutableListOf()
        }
        return dir.listFiles()
            ?.filter { it.isDirectory && it.name.matches(Regex("^\\d+|s_.+")) }
            ?.map { it.name }
            ?.sorted()
            ?.toMutableList()
            ?: mutableListOf()
    }

    override fun listSubDirs(avid: String): MutableList<String> {
        val avidDir = File(BILI_ROOT, avid)
        if (!avidDir.isDirectory) return mutableListOf()
        return avidDir.listFiles()
            ?.filter { it.isDirectory }
            ?.map { it.name }
            ?.sorted()
            ?.toMutableList()
            ?: mutableListOf()
    }

    override fun readEntryJson(avid: String, subDir: String): String {
        val file = File(File(BILI_ROOT, avid), "$subDir/entry.json")
        if (!file.isFile) return ""
        return try {
            file.readText(Charsets.UTF_8)
        } catch (e: Exception) {
            Log.e(Tags.SHIZUKU, "读取 entry.json 失败: ${file.path}", e)
            ""
        }
    }

    override fun readCoverBytes(avid: String, subDir: String): ByteArray {
        val file = File(File(BILI_ROOT, avid), "$subDir/cover.jpg")
        if (!file.isFile) return ByteArray(0)
        return try {
            file.readBytes()
        } catch (e: Exception) {
            Log.e(Tags.SHIZUKU, "读取 cover.jpg 失败: ${file.path}", e)
            ByteArray(0)
        }
    }

    override fun listQualityFolders(avid: String, subDir: String): MutableList<String> {
        val dir = File(File(BILI_ROOT, avid), subDir)
        if (!dir.isDirectory) return mutableListOf()
        return dir.listFiles()
            ?.filter { it.isDirectory }
            ?.map { it.name }
            ?.sorted()
            ?.toMutableList()
            ?: mutableListOf()
    }

    override fun checkM4sFilesExist(avid: String, subDir: String, typeTag: String): Boolean {
        val baseDir = File(File(BILI_ROOT, avid), "$subDir/$typeTag")
        val video = File(baseDir, "video.m4s")
        val audio = File(baseDir, "audio.m4s")
        return video.isFile && video.length() > 0 && audio.isFile && audio.length() > 0
    }

    override fun copyM4sFiles(avid: String, subDir: String, typeTag: String, destDir: String): Int {
        val baseDir = File(File(BILI_ROOT, avid), "$subDir/$typeTag")
        val videoSrc = File(baseDir, "video.m4s")
        val audioSrc = File(baseDir, "audio.m4s")
        val dest = File(destDir)
        Log.w(Tags.SHIZUKU, "copyM4sFiles: baseDir=${baseDir.absolutePath} vidExists=${videoSrc.isFile} audExists=${audioSrc.isFile} destDir=$destDir")
        if (!dest.isDirectory) dest.mkdirs()

        var copied = 0
        if (videoSrc.isFile) {
            try {
                videoSrc.copyTo(File(dest, "video.m4s"), overwrite = true)
                copied++
            } catch (e: Exception) {
                Log.e(Tags.SHIZUKU, "copy video.m4s failed: ${videoSrc.path}", e)
            }
        }
        if (audioSrc.isFile) {
            try {
                audioSrc.copyTo(File(dest, "audio.m4s"), overwrite = true)
                copied++
            } catch (e: Exception) {
                Log.e(Tags.SHIZUKU, "copy audio.m4s failed: ${audioSrc.path}", e)
            }
        }
        Log.w(Tags.SHIZUKU, "copyM4sFiles: copied=$copied")
        return copied
    }

    override fun checkBiliRootExists(): Boolean {
        return File(BILI_ROOT).isDirectory
    }

    override fun readDanmakuXml(avid: String, subDir: String): String? {
        val cidDir = subDir.substringBefore("/")
        val path = "$BILI_ROOT/$avid/$cidDir/danmaku.xml"
        return try {
            val file = java.io.File(path)
            if (file.isFile) file.readText() else null
        } catch (e: Exception) {
            Log.w(Tags.SHIZUKU, "readDanmakuXml error: $path", e)
            null
        }
    }

    override fun syncCoverBatch(requests: List<com.example.bilexport.core.model.CoverRequest>): List<com.example.bilexport.core.model.CoverData> {
        Log.w(Tags.SHIZUKU, "syncCoverBatch: 收到 ${requests.size} 个请求")
        val results = mutableListOf<com.example.bilexport.core.model.CoverData>()
        for (r in requests) {
            try {
                val src = File(File(BILI_ROOT, r.avid), "${r.subDir}/cover.jpg")
                if (!src.isFile) {
                    Log.d(Tags.SHIZUKU, "syncCoverBatch: 源文件不存在 ${src.absolutePath}")
                    continue
                }
                val bytes = src.readBytes()
                results.add(com.example.bilexport.core.model.CoverData("${r.avid}_${r.cid}", bytes))
            } catch (e: Exception) {
                Log.w(Tags.SHIZUKU, "syncCoverBatch: 失败 avid=${r.avid} cid=${r.cid}", e)
            }
        }
        Log.w(Tags.SHIZUKU, "syncCoverBatch: 完成 ${results.size}/${requests.size}")
        return results
    }

    /**
     * Shizuku 销毁回调 — 事务码 16777115。
     * Shizuku 框架会通过此事务码通知我们释放资源。
     */
    override fun onTransact(code: Int, data: android.os.Parcel, reply: android.os.Parcel?, flags: Int): Boolean {
        if (code == 16777115) {
            Log.d(Tags.SHIZUKU, "BiliFileService 收到 destroy 信号，退出进程")
            android.os.Process.killProcess(android.os.Process.myPid())
            return true
        }
        return super.onTransact(code, data, reply, flags)
    }
}
