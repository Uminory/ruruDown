package com.example.bilexport.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.bilexport.core.model.ExportJob
import com.example.bilexport.core.model.ExportJobStatus
import com.example.bilexport.core.util.TimeFormat
import com.example.bilexport.ui.component.StatusBadge
import com.example.bilexport.ui.theme.*
import com.example.bilexport.ui.viewmodel.ExportViewModel

/**
 * 导出队列页——展示导出历史与当前进度。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportQueueScreen(
    exportViewModel: ExportViewModel,
    onBack: () -> Unit
) {
    val jobs by exportViewModel.jobs.collectAsState()
    val isExporting by exportViewModel.isExporting.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "导出队列",
                        fontWeight = FontWeight.Bold
                    )
                },
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
        if (jobs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.History,
                        contentDescription = null,
                        tint = TextDisabled,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("暂无导出记录", color = TextSecondary)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(items = jobs, key = { it.jobId }) { job ->
                    JobCard(job = job, onRetry = { exportViewModel.retryJob(it) })
                }
            }
        }
    }
}

@Composable
private fun JobCard(
    job: ExportJob,
    onRetry: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = job.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )
                JobStatusBadge(status = job.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 进度条（仅活跃任务）
            if (job.status.isRunning) {
                LinearProgressIndicator(
                    progress = job.progress,
                    modifier = Modifier.fillMaxWidth(),
                    color = Blue500,
                    trackColor = DarkSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${(job.progress * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = Blue500
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 时间信息
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (job.startAt > 0) TimeFormat.formatDateTime(job.startAt) else "—",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                if (job.status == ExportJobStatus.FAILED) {
                    TextButton(
                        onClick = { onRetry(job.jobId) },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Blue500
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("重试", color = Blue500)
                    }
                }
            }

            // 错误信息
            if (job.errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = job.errorMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = Red500,
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
private fun JobStatusBadge(status: ExportJobStatus) {
    val (text, color) = when (status) {
        ExportJobStatus.PENDING -> "等待中" to TextSecondary
        ExportJobStatus.PREPARING -> "准备中" to Blue500
        ExportJobStatus.COPYING_INPUT -> "复制中" to Blue500
        ExportJobStatus.RUNNING_FFMPEG -> "重封装中" to Blue500
        ExportJobStatus.VERIFYING -> "校验中" to Blue500
        ExportJobStatus.MOVING_OUTPUT -> "移动中" to Blue500
        ExportJobStatus.SUCCESS -> "成功" to Green500
        ExportJobStatus.FAILED -> "失败" to Red500
        ExportJobStatus.CANCELLED -> "已取消" to TextSecondary
    }

    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = color,
        fontWeight = FontWeight.Medium
    )
}