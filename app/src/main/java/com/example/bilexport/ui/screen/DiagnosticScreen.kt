package com.example.bilexport.ui.screen

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bilexport.core.model.ServiceInitState
import com.example.bilexport.data.repository.DiagnosticRepository
import com.example.bilexport.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiagnosticScreen(
    diagnosticRepository: DiagnosticRepository,
    shizukuAvailable: Boolean,
    shizukuPermissionGranted: Boolean,
    onBack: () -> Unit,
    onNavigateToLibrary: () -> Unit = {}
) {
    val context = LocalContext.current
    var info by remember { mutableStateOf(diagnosticRepository.collectInfo(forceRefresh = true)) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("诊断") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    // Shizuku 恢复后显示"前往首页"
                    if (shizukuAvailable && shizukuPermissionGranted) {
                        IconButton(onClick = onNavigateToLibrary) {
                            Icon(Icons.Filled.Home, contentDescription = "首页")
                        }
                    }
                    IconButton(onClick = { info = diagnosticRepository.collectInfo(forceRefresh = true) }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "刷新")
                    }
                    IconButton(onClick = {
                        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        cm.setPrimaryClip(ClipData.newPlainText("diagnostic", info.toCopyableText()))
                        Toast.makeText(context, "已复制诊断信息", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Filled.ContentCopy, contentDescription = "复制")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SectionCard("Shizuku 状态") {
                StatusRow("Shizuku 可用", if (info.shizukuAvailable) "✓ 可用" else "✗ 不可用", info.shizukuAvailable)
                StatusRow("已授权", if (info.shizukuPermission) "✓ 已授权" else "✗ 未授权", info.shizukuPermission)
                StatusRow("服务状态", info.serviceInitState, info.serviceInitState == ServiceInitState.Ready.toString())
                StatusRow("服务已连接", if (info.serviceConnected) "✓ 已连接" else "✗ 未连接", info.serviceConnected)
            }

            if (!info.shizukuAvailable) {
                SectionCard("Shizuku 未运行") {
                    Text(
                        "Shizuku 服务未运行",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "请运行 Shizuku 服务或安装 Shizuku 并运行服务",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.data = Uri.parse("https://shizuku.rikka.app/download/")
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = Blue500)
                    ) {
                        Text("下载 Shizuku", fontWeight = FontWeight.Bold)
                    }
                }
            }

            SectionCard("B站缓存目录") {
                Text(
                    "/storage/emulated/0/Android/data/tv.danmaku.bili/download",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                StatusRow("可访问", if (info.biliDirAccessible) "✓ 可访问" else "✗ 不可访问", info.biliDirAccessible)
                if (!info.biliDirAccessible && info.biliDirDetail.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = info.biliDirDetail, style = MaterialTheme.typography.bodySmall, color = Color(0xFFB71C1C))
                }
            }

            if (info.recentErrors.isNotEmpty()) {
                SectionCard("最近错误") {
                    info.recentErrors.forEach { error ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                            Text(
                                text = "[${error.time}] ",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "${error.module}: ${error.message}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFB71C1C)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun StatusRow(label: String, value: String, ok: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(
            text = value,
            color = if (ok) Green500 else Color(0xFFB71C1C),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}
