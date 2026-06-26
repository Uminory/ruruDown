package com.example.bilexport.ui.screen

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.bilexport.core.model.ExportState
import com.example.bilexport.core.model.ExportType
import com.example.bilexport.core.model.MediaItem
import com.example.bilexport.core.util.TimeFormat
import com.example.bilexport.ui.component.StatusBadge
import com.example.bilexport.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    mediaItem: MediaItem,
    onExport: (MediaItem, Set<ExportType>) -> Unit,
    onBack: () -> Unit
) {
    var selectedVideo by remember { mutableStateOf(true) }
    var selectedAudio by remember { mutableStateOf(false) }
    var selectedDanmaku by remember { mutableStateOf(false) }

    val anySelected = selectedVideo || selectedAudio || selectedDanmaku

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("详情", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground, titleContentColor = TextPrimary)
            )
        },
        containerColor = DarkBackground,
        floatingActionButton = {
            if (mediaItem.exportState != ExportState.EXPORTING && anySelected) {
                ExtendedFloatingActionButton(
                    onClick = {
                        val types = mutableSetOf<ExportType>()
                        if (selectedVideo) types.add(ExportType.VIDEO)
                        if (selectedAudio) types.add(ExportType.AUDIO)
                        if (selectedDanmaku) types.add(ExportType.DANMAKU)
                        onExport(mediaItem, types)
                    },
                    containerColor = Blue500,
                    contentColor = TextPrimary,
                    icon = { Icon(Icons.Default.FileDownload, contentDescription = null) },
                    text = { Text(if (mediaItem.exportState == ExportState.EXPORTED) "重新导出" else "导出") }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues)
                .verticalScroll(rememberScrollState()).padding(16.dp)
        ) {
            Text(mediaItem.displayTitle, style = MaterialTheme.typography.headlineMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DarkCard), shape = MaterialTheme.shapes.medium) {
                Column(modifier = Modifier.padding(16.dp)) {
                    DetailRow("UP主", mediaItem.ownerName)
                    DetailRow("avid", mediaItem.avid)
                    if (mediaItem.bvid.isNotEmpty()) {
                        val ctx = LocalContext.current
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("bvid", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = mediaItem.bvid,
                                color = Blue500,
                                fontWeight = FontWeight.Medium,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.clickable {
                                    val clipboard = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("bvid", mediaItem.bvid))
                                    Toast.makeText(ctx, "BV号已复制", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                    DetailRow("cid", mediaItem.cid)
                    if (mediaItem.qualityPithyDescription.isNotEmpty()) DetailRow("清晰度", mediaItem.qualityPithyDescription)
                    if (mediaItem.resolutionText.isNotEmpty()) DetailRow("分辨率", mediaItem.resolutionText)
                    if (mediaItem.size > 0) DetailRow("大小", TimeFormat.formatFileSize(mediaItem.size))
                    if (mediaItem.duration > 0) DetailRow("时长", TimeFormat.formatDuration(mediaItem.duration))
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = Divider)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("导出状态", color = TextSecondary)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            StatusBadge(state = mediaItem.exportState, label = "MP4")
                            StatusBadge(
                                state = if (mediaItem.audioExported) ExportState.EXPORTED else ExportState.NOT_EXPORTED,
                                label = "M4A"
                            )
                            StatusBadge(
                                state = if (mediaItem.danmakuExported) ExportState.EXPORTED else ExportState.NOT_EXPORTED,
                                label = "XML"
                            )
                        }
                    }
                    if (mediaItem.lastExportAt > 0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        DetailRow("上次导出", TimeFormat.formatDateTime(mediaItem.lastExportAt))
                        DetailRow("导出次数", "${mediaItem.exportCount} 次")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 导出类型选择卡片
            Text("选择导出内容", style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ExportTypeCard("视频", "MP4", selectedVideo, { selectedVideo = !selectedVideo }, modifier = Modifier.weight(1f))
                ExportTypeCard("音频", "M4A", selectedAudio, { selectedAudio = !selectedAudio }, modifier = Modifier.weight(1f))
                ExportTypeCard("弹幕", "XML", selectedDanmaku, { selectedDanmaku = !selectedDanmaku }, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (mediaItem.lastExportPath.isNotEmpty()) {
                Text("导出文件", style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DarkCard)) {
                    Text(mediaItem.lastExportPath, style = MaterialTheme.typography.bodySmall, color = TextSecondary, modifier = Modifier.padding(12.dp))
                }
            }
        }
    }
}

@Composable
private fun ExportTypeCard(label: String, suffix: String, selected: Boolean, onToggle: () -> Unit, modifier: Modifier) {
    Card(
        modifier = modifier.clickable { onToggle() },
        colors = CardDefaults.cardColors(containerColor = if (selected) Green600.copy(alpha = 0.2f) else Blue500.copy(alpha = 0.15f)),
        shape = MaterialTheme.shapes.medium,
        border = if (selected) androidx.compose.foundation.BorderStroke(1.dp, Green500) else null
    ) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = when (label) {
                    "视频" -> Icons.Default.Videocam
                    "音频" -> Icons.Default.MusicNote
                    else -> Icons.Default.Description
                },
                contentDescription = null,
                tint = if (selected) Green500 else Blue500,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(label, color = if (selected) Green500 else Blue500, style = MaterialTheme.typography.labelMedium)
            Text(suffix, color = if (selected) Green600 else TextSecondary, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
        Text(value, color = TextPrimary, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}
