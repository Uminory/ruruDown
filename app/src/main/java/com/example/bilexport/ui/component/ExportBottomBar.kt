package com.example.bilexport.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.bilexport.core.model.MediaItem
import com.example.bilexport.ui.theme.*

/**
 * 底部导出栏——批量选择模式下显示导出操作。
 */
@Composable
fun ExportBottomBar(
    selectedCount: Int,
    totalCount: Int,
    onExport: () -> Unit,
    onSelectAll: () -> Unit,
    onClearSelection: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = DarkSurface,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 已选数量
            Text(
                text = "已选 $selectedCount / $totalCount",
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.weight(1f))

            // 全选按钮
            TextButton(onClick = onSelectAll) {
                Text("全选", color = Blue500)
            }

            Spacer(modifier = Modifier.width(8.dp))

            // 导出按钮
            Button(
                onClick = onExport,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Blue500,
                    contentColor = TextPrimary
                )
            ) {
                Text("导出", fontWeight = FontWeight.Bold)
            }
        }
    }
}