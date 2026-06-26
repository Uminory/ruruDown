package com.example.bilexport.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bilexport.core.model.RepeatExportPolicy
import com.example.bilexport.data.cache.CoverCache
import com.example.bilexport.domain.repository.MediaRepository
import com.example.bilexport.domain.repository.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 设置 ViewModel。
 */
class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val mediaRepository: MediaRepository? = null,
    private val coverCache: CoverCache? = null
) : ViewModel() {

    val exportDir: StateFlow<String> = settingsRepository.exportDir
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val repeatPolicy: StateFlow<String> = settingsRepository.repeatPolicy
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RepeatExportPolicy.ASK_CONFIRMATION.name)

    val overwriteExisting: StateFlow<Boolean> = settingsRepository.overwriteExisting
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val saveFfmpegLog: StateFlow<Boolean> = settingsRepository.saveFfmpegLog
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val enableCoverCache: StateFlow<Boolean> = settingsRepository.enableCoverCache
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    fun setExportDir(dir: String) {
        viewModelScope.launch { settingsRepository.setExportDir(dir) }
    }

    fun setRepeatPolicy(policy: String) {
        viewModelScope.launch { settingsRepository.setRepeatPolicy(policy) }
    }

    fun setOverwriteExisting(overwrite: Boolean) {
        viewModelScope.launch { settingsRepository.setOverwriteExisting(overwrite) }
    }

    fun setSaveFfmpegLog(save: Boolean) {
        viewModelScope.launch { settingsRepository.setSaveFfmpegLog(save) }
    }

    fun setEnableCoverCache(enable: Boolean) {
        viewModelScope.launch { settingsRepository.setEnableCoverCache(enable) }
    }

    fun clearAllScanCache(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                mediaRepository?.deleteAll()
                coverCache?.clear()
            }
            onComplete()
        }
    }
}
