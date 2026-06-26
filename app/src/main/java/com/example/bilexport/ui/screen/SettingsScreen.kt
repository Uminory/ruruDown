package com.example.bilexport.ui.screen

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.bilexport.core.model.RepeatExportPolicy
import com.example.bilexport.ui.theme.*
import com.example.bilexport.ui.viewmodel.SettingsViewModel

/**
 * 设置页。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    onRequestPickDir: () -> Unit = {},
    onBack: () -> Unit,
    onNavigateToAbout: () -> Unit = {},
    onNavigateToDiagnostic: () -> Unit = {}
) {
    val exportDir by settingsViewModel.exportDir.collectAsState()
    val repeatPolicy by settingsViewModel.repeatPolicy.collectAsState()
    val overwriteExisting by settingsViewModel.overwriteExisting.collectAsState()
    val saveFfmpegLog by settingsViewModel.saveFfmpegLog.collectAsState()
    val enableCoverCache by settingsViewModel.enableCoverCache.collectAsState()

    var showDirDialog by remember { mutableStateOf(false) }
    var dirInput by remember { mutableStateOf(exportDir) }
    var showClearDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground,
                    titleContentColor = TextPrimary
                )
            )
        },
        containerColor = DarkBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // 导出目录 — 可点击更换
            SettingsClickableSection(
                title = "导出目录",
                description = exportDir,
                onClick = {
                    dirInput = exportDir
                    showDirDialog = true
                }
            )

            HorizontalDivider(color = Divider, modifier = Modifier.padding(vertical = 8.dp))

            // 开关设置
            SettingsSwitch(
                title = "覆盖同名文件",
                description = "导出时覆盖已存在的同名文件",
                checked = overwriteExisting,
                onCheckedChange = { settingsViewModel.setOverwriteExisting(it) }
            )

            SettingsSwitch(
                title = "保存 FFmpeg 日志",
                description = "在任务目录保留 FFmpeg stderr 日志",
                checked = saveFfmpegLog,
                onCheckedChange = { settingsViewModel.setSaveFfmpegLog(it) }
            )

            SettingsSwitch(
                title = "启用封面缓存",
                description = "本地缓存封面图片以加速加载",
                checked = enableCoverCache,
                onCheckedChange = { settingsViewModel.setEnableCoverCache(it) }
            )

            HorizontalDivider(color = Divider, modifier = Modifier.padding(vertical = 8.dp))

            // 清除扫描缓存
            TextButton(
                onClick = { showClearDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.textButtonColors(contentColor = Red500)
            ) {
                Icon(Icons.Default.DeleteForever, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("清除缓存", fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 关于入口
            SettingsClickableSection(
                title = "关于",
                description = "ruruDown v1.0.0",
                onClick = onNavigateToAbout
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 诊断入口
            SettingsClickableSection(
                title = "诊断",
                description = "查看 Shizuku 和服务状态",
                onClick = onNavigateToDiagnostic
            )
        }
    }

    // 导出目录修改对话框
    if (showDirDialog) {
        AlertDialog(
            onDismissRequest = { showDirDialog = false },
            title = { Text("导出目录", color = TextPrimary) },
            text = {
                Column {
                    Text(
                        "输入导出目录路径或使用系统选择器：",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = dirInput,
                        onValueChange = { dirInput = it },
                        label = { Text("导出目录路径") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Blue500,
                            unfocusedBorderColor = Divider,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = Blue500,
                            focusedContainerColor = DarkSurface,
                            unfocusedContainerColor = DarkSurface,
                            focusedLabelColor = Blue500,
                            unfocusedLabelColor = TextSecondary
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = {
                            showDirDialog = false
                            onRequestPickDir()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Blue500)
                    ) {
                        Icon(Icons.Default.FolderOpen, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("使用系统文件选择器")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    settingsViewModel.setExportDir(dirInput)
                    showDirDialog = false
                }) {
                    Text("确定", color = Blue500)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDirDialog = false }) {
                    Text("取消", color = TextSecondary)
                }
            },
            containerColor = DarkSurface,
            titleContentColor = TextPrimary,
            textContentColor = TextPrimary
        )
    }

    // 清除扫描缓存确认对话框
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("清除扫描缓存", color = TextPrimary) },
            text = { Text("将删除所有已扫描的视频数据及封面缓存，此操作不可撤销。", color = TextSecondary) },
            confirmButton = {
                TextButton(
                    onClick = {
                        settingsViewModel.clearAllScanCache { showClearDialog = false }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Red500)
                ) {
                    Text("确认清除")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("取消", color = TextSecondary)
                }
            },
            containerColor = DarkSurface,
            titleContentColor = TextPrimary,
            textContentColor = TextPrimary
        )
    }
}

@Composable
private fun SettingsSection(
    title: String,
    description: String? = null,
    content: @Composable (() -> Unit)? = null
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold
        )
        if (description != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
        if (content != null) {
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun SettingsClickableSection(
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                modifier = Modifier.weight(1f)
            )
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun SettingsSwitch(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = TextPrimary,
                checkedTrackColor = Blue500,
                uncheckedThumbColor = TextSecondary,
                uncheckedTrackColor = DarkSurfaceVariant
            )
        )
    }
}
