package com.example.bilexport.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.bilexport.core.model.ExportState
import com.example.bilexport.core.model.MediaItem
import com.example.bilexport.core.model.ScanState
import com.example.bilexport.ui.component.*
import com.example.bilexport.ui.theme.*
import com.example.bilexport.ui.viewmodel.LibraryViewModel
import com.example.bilexport.ui.viewmodel.MainViewModel
import com.example.bilexport.ui.viewmodel.ExportViewModel
import com.example.bilexport.ui.viewmodel.SortField
import com.example.bilexport.ui.viewmodel.SortOrder

/**
 * 首页——媒体库列表。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    mainViewModel: MainViewModel,
    libraryViewModel: LibraryViewModel,
    exportViewModel: ExportViewModel,
    onExport: (List<Long>) -> Unit,
    onNavigateToDetail: (Long) -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    val filteredItems by libraryViewModel.filteredItems.collectAsState()
    val stats by libraryViewModel.stats.collectAsState()
    val scanState by libraryViewModel.scanState.collectAsState()
    val searchQuery by mainViewModel.searchQuery.collectAsState()
    val exportStateFilter by mainViewModel.exportStateFilter.collectAsState()
    val selectedIds by mainViewModel.selectedIds.collectAsState()
    val isMultiSelectMode by mainViewModel.isMultiSelectMode.collectAsState()
    val shizukuAvailable by mainViewModel.shizukuAvailable.collectAsState()
    var showBatchTypeDialog by remember { mutableStateOf(false) }
    var pendingBatchIds by remember { mutableStateOf<List<Long>>(emptyList()) }

    val sortField by libraryViewModel.sortField.collectAsState()
    val sortOrder by libraryViewModel.sortOrder.collectAsState()

    // 导出完成后清除多选
    LaunchedEffect(Unit) {
        exportViewModel.onBatchExportComplete = {
            mainViewModel.onClearSelection()
        }
    }

    val listState = rememberLazyListState()
    // 排序变化时自动回到顶部
    LaunchedEffect(sortField, sortOrder) {
        listState.animateScrollToItem(0)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "ruruDown",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "共 ${stats.total} 个视频 | 已导出 ${stats.exported}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                },
                actions = {
                    // 多选模式下系统回退键退出
                    if (isMultiSelectMode) {
                        BackHandler { mainViewModel.onClearSelection() }
                    }
                    // 扫描状态
                    when (scanState) {
                        ScanState.SCANNING -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Blue500,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                        }
                        else -> {}
                    }

                    // 设置
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "设置",
                            tint = TextSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground,
                    titleContentColor = TextPrimary
                )
            )
        },
        floatingActionButton = {
            if (scanState != ScanState.SCANNING) {
                FloatingActionButton(
                    onClick = { libraryViewModel.scan() },
                    containerColor = Blue500,
                    contentColor = TextPrimary
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "扫描")
                }
            }
        },
        bottomBar = {
            if (isMultiSelectMode) {
                ExportBottomBar(
                    selectedCount = selectedIds.size,
                    totalCount = filteredItems.size,
                    onExport = {
                        pendingBatchIds = selectedIds.toList()
                        showBatchTypeDialog = true
                    },
                    onSelectAll = {
                        mainViewModel.onSelectAll(filteredItems)
                    },
                    onClearSelection = {
                        mainViewModel.onClearSelection()
                    }
                )
            }
        },
        containerColor = DarkBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 搜索栏
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { mainViewModel.onSearchQueryChanged(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                placeholder = {
                    Text("搜索标题、UP主、avid...", color = TextDisabled)
                },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { mainViewModel.onSearchQueryChanged("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "清除", tint = TextSecondary)
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Blue500,
                    unfocusedBorderColor = Divider,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = Blue500,
                    focusedContainerColor = DarkSurface,
                    unfocusedContainerColor = DarkSurface
                ),
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            // 筛选芯片
            val counts = mapOf(
                null to stats.total,
                ExportState.NOT_EXPORTED to stats.notExported,
                ExportState.EXPORTED to stats.exported,
                ExportState.FAILED to stats.failed
            )
            FilterChips(
                currentFilter = exportStateFilter,
                counts = counts,
                onFilterSelected = { mainViewModel.onExportStateFilterChanged(it) }
            )

            // 排序控件
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SortChip("时间", sortField == SortField.TIME) {
                    libraryViewModel.setSortField(SortField.TIME)
                }
                SortChip("大小", sortField == SortField.SIZE) {
                    libraryViewModel.setSortField(SortField.SIZE)
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = { libraryViewModel.toggleSortOrder() },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (sortOrder == SortOrder.DESC)
                            Icons.Default.ArrowDownward
                        else
                            Icons.Default.ArrowUpward,
                        contentDescription = if (sortOrder == SortOrder.DESC) "降序" else "升序",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // 空状态
            if (filteredItems.isEmpty() && scanState != ScanState.SCANNING) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.VideoLibrary,
                            contentDescription = null,
                            tint = TextDisabled,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (stats.total == 0) "暂无视频，请先扫描" else "无匹配结果",
                            color = TextSecondary
                        )
                    }
                }
            }

            // 视频列表
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(
                    items = filteredItems,
                    key = { "${it.avid}_${it.cid}" }
                ) { item ->
                    VideoCard(
                        item = item,
                        isSelected = item.id in selectedIds,
                        showCheckbox = isMultiSelectMode,
                        onClick = {
                            if (isMultiSelectMode) {
                                mainViewModel.onToggleSelection(item.id)
                            } else {
                                onNavigateToDetail(item.id)
                            }
                        },
                        onLongClick = {
                            if (!isMultiSelectMode) {
                                mainViewModel.onToggleSelection(item.id)
                            }
                        }
                    )
                }
            }
        }
    }

    if (showBatchTypeDialog) {
        ExportTypeSelectDialog(
            onDismiss = { showBatchTypeDialog = false },
            onConfirm = { types ->
                showBatchTypeDialog = false
                exportViewModel.batchExport(pendingBatchIds, types)
                mainViewModel.onClearSelection()
            }
        )
    }
}

@Composable
private fun SortChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
        color = if (selected) Blue500.copy(alpha = 0.2f) else DarkSurface,
        contentColor = if (selected) Blue500 else TextSecondary,
        border = if (selected) androidx.compose.foundation.BorderStroke(1.dp, Blue500) else null
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium
        )
    }
}