package com.example.bilexport.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.bilexport.core.model.ExportType
import com.example.bilexport.ui.theme.*

@Composable
fun ExportTypeSelectDialog(
    onDismiss: () -> Unit,
    onConfirm: (Set<ExportType>) -> Unit
) {
    var selectedVideo by remember { mutableStateOf(true) }
    var selectedAudio by remember { mutableStateOf(false) }
    var selectedDanmaku by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("选择导出内容", color = TextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DialogTypeCard("视频", "MP4", Icons.Default.Videocam, selectedVideo, { selectedVideo = !selectedVideo }, Modifier.weight(1f))
                DialogTypeCard("音频", "M4A", Icons.Default.MusicNote, selectedAudio, { selectedAudio = !selectedAudio }, Modifier.weight(1f))
                DialogTypeCard("弹幕", "XML", Icons.Default.Description, selectedDanmaku, { selectedDanmaku = !selectedDanmaku }, Modifier.weight(1f))
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val types = mutableSetOf<ExportType>()
                    if (selectedVideo) types.add(ExportType.VIDEO)
                    if (selectedAudio) types.add(ExportType.AUDIO)
                    if (selectedDanmaku) types.add(ExportType.DANMAKU)
                    onConfirm(types)
                },
                enabled = selectedVideo || selectedAudio || selectedDanmaku
            ) { Text("导出", color = Blue500, fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消", color = TextSecondary) }
        },
        containerColor = DarkSurface,
        titleContentColor = TextPrimary,
        textContentColor = TextPrimary
    )
}

@Composable
private fun DialogTypeCard(label: String, suffix: String, icon: androidx.compose.ui.graphics.vector.ImageVector, selected: Boolean, onToggle: () -> Unit, modifier: Modifier) {
    Card(
        modifier = modifier.clickable { onToggle() },
        colors = CardDefaults.cardColors(containerColor = if (selected) Green600.copy(alpha = 0.2f) else Blue500.copy(alpha = 0.15f)),
        shape = RoundedCornerShape(10.dp),
        border = if (selected) BorderStroke(1.dp, Green500) else null
    ) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, tint = if (selected) Green500 else Blue500, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(label, color = if (selected) Green500 else Blue500, style = MaterialTheme.typography.labelMedium)
            Text(suffix, color = if (selected) Green600 else TextSecondary, style = MaterialTheme.typography.labelSmall)
        }
    }
}
