package com.example.bilexport.data.cache

import java.io.File

/**
 * 导出任务临时目录管理器。
 * 每个任务独立 temp dir，完成后立即清理，失败时保留便于排查。
 */
class TempPathManager(private val appCacheDir: String) {

    private val jobsRoot: File
        get() = File(appCacheDir, "jobs").also { it.mkdirs() }

    fun createJobDir(jobId: String): File {
        val dir = File(jobsRoot, jobId)
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun getJobDir(jobId: String): File {
        return File(jobsRoot, jobId)
    }

    fun getVideoPath(jobId: String): File {
        return File(getJobDir(jobId), "video.m4s")
    }

    fun getAudioPath(jobId: String): File {
        return File(getJobDir(jobId), "audio.m4s")
    }

    fun getLogPath(jobId: String): File {
        return File(getJobDir(jobId), "log.txt")
    }

    fun getCommandPath(jobId: String): File {
        return File(getJobDir(jobId), "command.txt")
    }

    fun getResultPath(jobId: String): File {
        return File(getJobDir(jobId), "result.json")
    }

    fun cleanupJobDir(jobId: String) {
        val dir = File(jobsRoot, jobId)
        if (dir.exists()) dir.deleteRecursively()
    }

    fun cleanupAll() {
        jobsRoot.listFiles()?.forEach { it.deleteRecursively() }
    }

    fun cleanupOld(retentionMs: Long = 7 * 24 * 60 * 60 * 1000L) {
        val cutoff = System.currentTimeMillis() - retentionMs
        jobsRoot.listFiles()?.forEach { dir ->
            if (dir.lastModified() < cutoff) {
                dir.deleteRecursively()
            }
        }
    }
}