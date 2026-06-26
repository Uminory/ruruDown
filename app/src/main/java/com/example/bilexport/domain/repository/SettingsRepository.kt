package com.example.bilexport.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val exportDir: Flow<String>
    val repeatPolicy: Flow<String>
    val overwriteExisting: Flow<Boolean>
    val saveFfmpegLog: Flow<Boolean>
    val enableCoverCache: Flow<Boolean>
    val scanRoot: Flow<String>
    val lastScanTime: Flow<Long>

    suspend fun setExportDir(dir: String)
    suspend fun setRepeatPolicy(policy: String)
    suspend fun setOverwriteExisting(overwrite: Boolean)
    suspend fun setSaveFfmpegLog(save: Boolean)
    suspend fun setEnableCoverCache(enable: Boolean)
    suspend fun setScanRoot(root: String)
    suspend fun setLastScanTime(time: Long)
}