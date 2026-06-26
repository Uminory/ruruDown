package com.example.bilexport.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.bilexport.core.model.ExportState
import com.example.bilexport.core.model.MediaItem
import com.example.bilexport.core.model.ScanState
import com.example.bilexport.core.model.ServiceInitState
import com.example.bilexport.data.cache.CoverCache
import com.example.bilexport.data.cache.TempPathManager
import com.example.bilexport.data.db.AppDatabase
import com.example.bilexport.data.repository.ExportRepositoryImpl
import com.example.bilexport.data.repository.MediaRepositoryImpl
import com.example.bilexport.data.repository.SettingsRepositoryImpl
import com.example.bilexport.domain.repository.MediaRepository
import com.example.bilexport.domain.repository.ExportRepository
import com.example.bilexport.domain.repository.SettingsRepository
import com.example.bilexport.domain.usecase.*
import com.example.bilexport.export.ffmpeg.FfmpegEngine
import com.example.bilexport.export.pipeline.ExportPipeline
import com.example.bilexport.privileged.permission.PermissionChecker
import com.example.bilexport.privileged.permission.PermissionState
import com.example.bilexport.privileged.shizuku.BiliFileServiceClient
import com.example.bilexport.privileged.shizuku.ShizukuManager
import com.example.bilexport.privileged.source.RemoteDirectoryScanner
import com.example.bilexport.privileged.source.RemoteFileCopier
import com.example.bilexport.data.repository.DiagnosticRepository
import com.example.bilexport.data.repository.CoverSyncManager
import android.util.Log
import com.example.bilexport.core.constants.Tags
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * 主 ViewModel——协调各个子 ViewModel，管理全局状态。
 * 采用分层异步启动：UI 先呈现，后台逐步初始化 Shizuku/DB/FFmpeg/扫描。
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {

    // 基础设施（轻量，可在主线程快速构造）
    private val db: AppDatabase by lazy { AppDatabase.getInstance(application) }
    private val settingsRepository: SettingsRepository by lazy { SettingsRepositoryImpl(application) }
    private val mediaRepository: MediaRepository by lazy { MediaRepositoryImpl(db) }
    private val exportRepository: ExportRepository by lazy { ExportRepositoryImpl(db) }
    private val permissionChecker by lazy { PermissionChecker(application) }

    // Shizuku
    val biliFileServiceClient by lazy { BiliFileServiceClient() }
    val remoteDirectoryScanner by lazy { RemoteDirectoryScanner(biliFileServiceClient) }
    val remoteFileCopier by lazy { RemoteFileCopier(biliFileServiceClient) }

    // 缓存
    val coverCache by lazy { CoverCache(application) }
    val tempPathManager by lazy { TempPathManager(application.cacheDir.absolutePath) }
    val coverSyncManager by lazy { CoverSyncManager(biliFileServiceClient, coverCache) }

    // FFmpeg
    val ffmpegEngine by lazy { FfmpegEngine() }

    // 导出流水线
    val exportPipeline by lazy { ExportPipeline(ffmpegEngine, remoteFileCopier, tempPathManager, application) }

    // UseCase
    val scanLibraryUseCase by lazy { ScanLibraryUseCase(mediaRepository, settingsRepository, remoteDirectoryScanner, coverCache, coverSyncManager) }
    val refreshLibraryUseCase by lazy { RefreshLibraryUseCase(mediaRepository, settingsRepository, remoteDirectoryScanner) }
    val buildExportPreviewUseCase by lazy { BuildExportPreviewUseCase(mediaRepository) }
    val createExportJobUseCase by lazy { CreateExportJobUseCase(exportRepository) }
    val runExportJobUseCase by lazy { RunExportJobUseCase(exportRepository, mediaRepository, exportPipeline) }
    val retryExportJobUseCase by lazy { RetryExportJobUseCase(exportRepository, mediaRepository, runExportJobUseCase) }
    val cleanupJobUseCase by lazy { CleanupJobUseCase(tempPathManager) }

    // 诊断
    val diagnosticRepository by lazy { DiagnosticRepository(biliFileServiceClient) }

    // 状态流
    val permissionState: StateFlow<PermissionState> by lazy { permissionChecker.permissionState }
    val shizukuAvailable: StateFlow<Boolean> = ShizukuManager.isAvailable
    val shizukuPermissionGranted: StateFlow<Boolean> = ShizukuManager.isPermissionGranted
    val biliServiceInitState: StateFlow<ServiceInitState> get() = biliFileServiceClient.initState
    val scanState: StateFlow<ScanState> by lazy { scanLibraryUseCase.scanState }

    private val _exportDir = MutableStateFlow("")
    val exportDir: StateFlow<String> = _exportDir

    // 搜索与筛选
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _exportStateFilter = MutableStateFlow<ExportState?>(null)
    val exportStateFilter: StateFlow<ExportState?> = _exportStateFilter

    // 多选模式
    private val _selectedIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedIds: StateFlow<Set<Long>> = _selectedIds

    private val _isMultiSelectMode = MutableStateFlow(false)
    val isMultiSelectMode: StateFlow<Boolean> = _isMultiSelectMode

    init {
        // 分层异步启动：前台先出 UI，后台并行初始化
        viewModelScope.launch(Dispatchers.Default) {
            // Step 1: 并行初始化 Shizuku + FFmpeg + 设置
            ShizukuManager.init()
            biliFileServiceClient.init()
            ffmpegEngine.init()

            // Step 2: 订阅设置
            settingsRepository.exportDir.collect { _exportDir.value = it }
        }

        // 状态驱动扫描：ServiceInitState → Ready 后触发
        viewModelScope.launch(Dispatchers.Default) {
            biliFileServiceClient.initState.collect { state ->
                Log.d(Tags.SCANNER, "服务初始化状态: $state")
                when (state) {
                    is ServiceInitState.Ready -> {
                        if (ShizukuManager.isAvailable.value) {
                            Log.d(Tags.SCANNER, "服务就绪，触发首次扫描")
                            try {
                                scanLibraryUseCase.execute()
                            } catch (e: Exception) {
                                Log.e(Tags.SCANNER, "扫描失败", e)
                            }
                        }
                    }
                    else -> { /* 等待 */ }
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onExportStateFilterChanged(state: ExportState?) {
        _exportStateFilter.value = state
    }

    fun onToggleSelection(id: Long) {
        _selectedIds.value = _selectedIds.value.toMutableSet().apply {
            if (contains(id)) remove(id) else add(id)
        }.also {
            _isMultiSelectMode.value = it.isNotEmpty()
        }
    }

    fun onClearSelection() {
        _selectedIds.value = emptySet()
        _isMultiSelectMode.value = false
    }

    fun onSelectAll(items: List<MediaItem>) {
        _selectedIds.value = items.map { it.id }.toSet()
        _isMultiSelectMode.value = true
    }

    fun requestShizukuPermission(activity: android.app.Activity) {
        permissionChecker.requestShizukuPermission(activity)
    }

    override fun onCleared() {
        super.onCleared()
        coverSyncManager.cancel()
        biliFileServiceClient.release()
        ffmpegEngine.release()
    }
}
