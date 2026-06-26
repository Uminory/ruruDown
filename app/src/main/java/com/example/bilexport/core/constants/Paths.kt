package com.example.bilexport.core.constants

/**
 * 路径常量——集中管理所有路径配置，禁止散落在业务代码中。
 */
object Paths {
    /** Bilibili 下载缓存根目录 */
    const val BILI_ROOT = "/storage/emulated/0/Android/data/tv.danmaku.bili/download"

    /** 应用默认导出目录 */
    const val DEFAULT_EXPORT_DIR = "/storage/emulated/0/Download/ruruDown"

    /** 应用内部缓存根目录 */
    fun appCacheDir(app: android.app.Application): String =
        app.cacheDir.absolutePath

    /** entry.json 本地缓存目录 */
    fun entryCacheDir(app: android.app.Application): String =
        "${app.cacheDir.absolutePath}/entry"

    /** 封面缓存目录 */
    fun coverCacheDir(app: android.app.Application): String =
        "${app.cacheDir.absolutePath}/covers"

    /** 导出任务临时目录 */
    fun jobTempDir(app: android.app.Application, jobId: String): String =
        "${app.cacheDir.absolutePath}/jobs/$jobId"

    /** 导出任务日志目录 */
    fun jobLogDir(app: android.app.Application, jobId: String): String =
        "${app.cacheDir.absolutePath}/jobs/$jobId"

    /** 数据库路径 */
    fun dbPath(app: android.app.Application): String =
        "${app.filesDir.absolutePath}/bilexport.db"
}