package com.example.bilexport.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.bilexport.core.model.MediaItem
import com.example.bilexport.ui.theme.*

/**
 * 重复导出确认对话框。
 */
@Composable
fun ConfirmRepeatExportDialog(
    duplicateItems: List<MediaItem>,
    onConfirm: (excludeDuplicates: Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(16.dp),
        containerColor = DarkSurface,
        titleContentColor = TextPrimary,
        textContentColor = TextSecondary,
        title = {
            Text(
                "以下视频已导出",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    "是否重复导出？",
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                duplicateItems.take(5).forEach { item ->
                    Text(
                        text = item.displayTitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        maxLines = 1
                    )
                }
                if (duplicateItems.size > 5) {
                    Text(
                        text = "... 及其他 ${duplicateItems.size - 5} 项",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextDisabled
                    )
                }
            }
        },
        confirmButton = {
            // 默认高亮按钮：排除重复项
            Button(
                onClick = { onConfirm(true) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Blue500
                )
            ) {
                Text("排除重复项")
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = onDismiss) {
                    Text("取消", color = TextSecondary)
                }
                TextButton(onClick = { onConfirm(false) }) {
                    Text("确认重复导出", color = Red500)
                }
            }
        }
    )
}