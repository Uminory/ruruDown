package com.example.bilexport.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bilexport.core.model.*
import com.example.bilexport.core.util.Result
import com.example.bilexport.domain.usecase.*
import com.example.bilexport.export.strategy.OutputNamingStrategy
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * 导出 ViewModel——管理导出队列、进度、重试、取消。
 */
class ExportViewModel(
    private val exportRepository: com.example.bilexport.domain.repository.ExportRepository,
    private val mediaRepository: com.example.bilexport.domain.repository.MediaRepository,
    private val settingsRepository: com.example.bilexport.domain.repository.SettingsRepository,
    private val buildExportPreviewUseCase: BuildExportPreviewUseCase,
    private val createExportJobUseCase: CreateExportJobUseCase,
    private val runExportJobUseCase: RunExportJobUseCase,
    private val retryExportJobUseCase: RetryExportJobUseCase,
    private val cleanupJobUseCase: CleanupJobUseCase
) : ViewModel() {

    private val _jobs = MutableStateFlow<List<ExportJob>>(emptyList())
    val jobs: StateFlow<List<ExportJob>> = _jobs

    private val _activeJobs = MutableStateFlow<List<ExportJob>>(emptyList())
    val activeJobs: StateFlow<List<ExportJob>> = _activeJobs

    private val _exportResults = MutableSharedFlow<ExportResultState>()
    val exportResults: SharedFlow<ExportResultState> = _exportResults

    var onBatchExportComplete: (() -> Unit)? = null

    private val _isExporting = MutableStateFlow(false)
    val isExporting: StateFlow<Boolean> = _isExporting

    init {
        viewModelScope.launch {
            exportRepository.getAllJobs().collect { _jobs.value = it }
        }
    }

    /**
     * 批量导出选中的条目。
     */
    fun batchExport(mediaItemIds: List<Long>, exportTypes: Set<ExportType> = setOf(ExportType.VIDEO)) {
        viewModelScope.launch {
            val preview = buildExportPreviewUseCase.execute(mediaItemIds)
            executeBatchExport(preview.allItems, exportTypes)
        }
    }

    /**
     * 单条导出。
     */
    fun singleExport(mediaItem: MediaItem, exportTypes: Set<ExportType> = setOf(ExportType.VIDEO)) {
        viewModelScope.launch {
            val exportDir = settingsRepository.exportDir.first()
            val overwriteExisting = settingsRepository.overwriteExisting.first()
            val outputDir = OutputNamingStrategy.generateOutputDir(exportDir, mediaItem)

            val typesStr = exportTypes.joinToString(",") { it.name }
            val result = createExportJobUseCase.execute(mediaItem, outputDir,
                overwriteExisting = overwriteExisting, exportTypes = typesStr)
            when (result) {
                is Result.Success -> {
                    executeSingleExport(result.data)
                }
                is Result.Error -> {
                    _exportResults.emit(ExportResultState.Error(result.errorCode, result.message))
                }
            }
        }
    }

    /**
     * 重试失败任务。
     */
    fun retryJob(jobId: String) {
        viewModelScope.launch {
            val result = retryExportJobUseCase.execute(jobId)
            when (result) {
                is Result.Success -> {
                    _exportResults.emit(ExportResultState.Success(jobId, result.data.outputPath))
                }
                is Result.Error -> {
                    _exportResults.emit(ExportResultState.Error(result.errorCode, result.message))
                }
            }
        }
    }

    private suspend fun executeBatchExport(items: List<MediaItem>, exportTypes: Set<ExportType> = setOf(ExportType.VIDEO)) {
        _isExporting.value = true
        val exportDir = settingsRepository.exportDir.first()
        val overwriteExisting = settingsRepository.overwriteExisting.first()

        for (item in items) {
            val outputDir = OutputNamingStrategy.generateOutputDir(exportDir, item)
            val typesStr = exportTypes.joinToString(",") { it.name }
            val result = createExportJobUseCase.execute(item, outputDir,
                overwriteExisting = overwriteExisting, exportTypes = typesStr)

            when (result) {
                is Result.Success -> {
                    val runResult = runExportJobUseCase.execute(result.data)
                    when (runResult) {
                        is Result.Success -> {
                            _exportResults.emit(ExportResultState.Success(
                                result.data.jobId,
                                runResult.data.outputPath
                            ))
                        }
                        is Result.Error -> {
                            _exportResults.emit(ExportResultState.Error(
                                runResult.errorCode,
                                runResult.message
                            ))
                        }
                    }
                }
                is Result.Error -> {
                    _exportResults.emit(ExportResultState.Error(
                        result.errorCode,
                        result.message
                    ))
                }
            }

            // 刷新 job 列表
            _activeJobs.value = exportRepository.getActiveJobs()
        }

        _isExporting.value = false
        onBatchExportComplete?.invoke()
    }

    private suspend fun executeSingleExport(job: ExportJob) {
        _isExporting.value = true
        val result = runExportJobUseCase.execute(job)
        when (result) {
            is Result.Success -> {
                _exportResults.emit(ExportResultState.Success(job.jobId, result.data.outputPath))
            }
            is Result.Error -> {
                _exportResults.emit(ExportResultState.Error(result.errorCode, result.message))
            }
        }
        _isExporting.value = false
    }

    sealed class ExportResultState {
        data class Success(val jobId: String, val outputPath: String) : ExportResultState()
        data class Error(val errorCode: ExportErrorCode, val message: String) : ExportResultState()
    }
}