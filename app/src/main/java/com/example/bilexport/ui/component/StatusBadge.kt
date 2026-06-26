package com.example.bilexport.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.bilexport.core.model.ExportState
import com.example.bilexport.ui.theme.*

/**
 * 状态标签组件——显示导出状态。
 */
@Composable
fun StatusBadge(
    state: ExportState,
    label: String = "",
    modifier: Modifier = Modifier
) {
    val (text, color) = when (state) {
        ExportState.NOT_EXPORTED -> "未导出" to TextSecondary
        ExportState.EXPORTED -> "已导出" to Green500
        ExportState.EXPORTING -> "导出中" to Blue500
        ExportState.FAILED -> "失败" to Red500
    }
    val displayText = if (label.isNotEmpty()) "$label $text" else text

    Text(
        text = displayText,
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
        color = color,
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    )
}