package com.example.bilexport.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.bilexport.core.constants.Paths
import com.example.bilexport.core.model.RepeatExportPolicy
import com.example.bilexport.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepositoryImpl(private val context: Context) : SettingsRepository {

    companion object {
        private val KEY_EXPORT_DIR = stringPreferencesKey("export_dir")
        private val KEY_REPEAT_POLICY = stringPreferencesKey("repeat_policy")
        private val KEY_OVERWRITE_EXISTING = booleanPreferencesKey("overwrite_existing")
        private val KEY_SAVE_FFMPEG_LOG = booleanPreferencesKey("save_ffmpeg_log")
        private val KEY_ENABLE_COVER_CACHE = booleanPreferencesKey("enable_cover_cache")
        private val KEY_SCAN_ROOT = stringPreferencesKey("scan_root")
        private val KEY_LAST_SCAN_TIME = longPreferencesKey("last_scan_time")
    }

    override val exportDir: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_EXPORT_DIR] ?: Paths.DEFAULT_EXPORT_DIR
    }

    override val repeatPolicy: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_REPEAT_POLICY] ?: RepeatExportPolicy.ASK_CONFIRMATION.name
    }

    override val overwriteExisting: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_OVERWRITE_EXISTING] ?: true
    }

    override val saveFfmpegLog: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_SAVE_FFMPEG_LOG] ?: true
    }

    override val enableCoverCache: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_ENABLE_COVER_CACHE] ?: true
    }

    override val scanRoot: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_SCAN_ROOT] ?: Paths.BILI_ROOT
    }

    override val lastScanTime: Flow<Long> = context.dataStore.data.map { prefs ->
        prefs[KEY_LAST_SCAN_TIME] ?: 0L
    }

    override suspend fun setExportDir(dir: String) {
        context.dataStore.edit { prefs -> prefs[KEY_EXPORT_DIR] = dir }
    }

    override suspend fun setRepeatPolicy(policy: String) {
        context.dataStore.edit { prefs -> prefs[KEY_REPEAT_POLICY] = policy }
    }

    override suspend fun setOverwriteExisting(overwrite: Boolean) {
        context.dataStore.edit { prefs -> prefs[KEY_OVERWRITE_EXISTING] = overwrite }
    }

    override suspend fun setSaveFfmpegLog(save: Boolean) {
        context.dataStore.edit { prefs -> prefs[KEY_SAVE_FFMPEG_LOG] = save }
    }

    override suspend fun setEnableCoverCache(enable: Boolean) {
        context.dataStore.edit { prefs -> prefs[KEY_ENABLE_COVER_CACHE] = enable }
    }

    override suspend fun setScanRoot(root: String) {
        context.dataStore.edit { prefs -> prefs[KEY_SCAN_ROOT] = root }
    }

    override suspend fun setLastScanTime(time: Long) {
        context.dataStore.edit { prefs -> prefs[KEY_LAST_SCAN_TIME] = time }
    }
}