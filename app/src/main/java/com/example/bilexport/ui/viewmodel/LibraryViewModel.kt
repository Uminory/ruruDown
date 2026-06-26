package com.example.bilexport.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bilexport.core.model.ExportState
import com.example.bilexport.core.model.MediaItem
import com.example.bilexport.core.model.ScanState
import com.example.bilexport.domain.usecase.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class SortField { TIME, SIZE }
enum class SortOrder { ASC, DESC }

class LibraryViewModel(
    private val mediaRepository: com.example.bilexport.domain.repository.MediaRepository,
    private val scanLibraryUseCase: ScanLibraryUseCase,
    private val refreshLibraryUseCase: RefreshLibraryUseCase,
    private val searchQuery: StateFlow<String>,
    private val exportStateFilter: StateFlow<ExportState?>
) : ViewModel() {

    private val _allItems = MutableStateFlow<List<MediaItem>>(emptyList())
    val allItems: StateFlow<List<MediaItem>> = _allItems

    private val _sortField = MutableStateFlow(SortField.TIME)
    val sortField: StateFlow<SortField> = _sortField

    private val _sortOrder = MutableStateFlow(SortOrder.DESC)
    val sortOrder: StateFlow<SortOrder> = _sortOrder

    @OptIn(ExperimentalCoroutinesApi::class)
    val filteredItems: StateFlow<List<MediaItem>> = combine(
        _allItems,
        searchQuery,
        exportStateFilter,
        _sortField,
        _sortOrder
    ) { items, query, stateFilter, field, order ->
        var result = items

        if (query.isNotBlank()) {
            val q = query.lowercase()
            result = result.filter {
                it.title.lowercase().contains(q) ||
                it.ownerName.lowercase().contains(q) ||
                it.avid.contains(q) ||
                it.partTitle.lowercase().contains(q)
            }
        }

        if (stateFilter != null) {
            result = result.filter { it.exportState == stateFilter }
        }

        val comparator: Comparator<MediaItem> = when (field) {
            SortField.TIME -> compareBy { it.createdAt }
            SortField.SIZE -> compareBy { it.size }
        }

        result.sortedWith(if (order == SortOrder.DESC) comparator.reversed() else comparator)
    }.flowOn(Dispatchers.Default)
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val scanState: StateFlow<ScanState> = scanLibraryUseCase.scanState

    private val _stats = MutableStateFlow(LibraryStats())
    val stats: StateFlow<LibraryStats> = _stats

    init {
        viewModelScope.launch {
            mediaRepository.getAll().collect { items ->
                _allItems.value = items
                updateStats(items)
            }
        }
    }

    fun setSortField(field: SortField) {
        _sortField.value = field
    }

    fun toggleSortOrder() {
        _sortOrder.value = if (_sortOrder.value == SortOrder.DESC) SortOrder.ASC else SortOrder.DESC
    }

    fun scan() {
        viewModelScope.launch {
            scanLibraryUseCase.execute()
        }
    }

    fun refresh() {
        viewModelScope.launch {
            refreshLibraryUseCase.execute()
        }
    }

    private suspend fun updateStats(items: List<MediaItem>) {
        val total = items.size
        val exported = items.count { it.exportState == ExportState.EXPORTED }
        val notExported = items.count { it.exportState == ExportState.NOT_EXPORTED }
        val failed = items.count { it.exportState == ExportState.FAILED }
        _stats.value = LibraryStats(
            total = total,
            exported = exported,
            notExported = notExported,
            failed = failed
        )
    }

    data class LibraryStats(
        val total: Int = 0,
        val exported: Int = 0,
        val notExported: Int = 0,
        val failed: Int = 0
    )
}
