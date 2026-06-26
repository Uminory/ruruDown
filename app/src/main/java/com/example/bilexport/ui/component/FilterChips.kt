package com.example.bilexport.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bilexport.core.model.ExportState
import com.example.bilexport.ui.theme.*

/**
 * 筛选芯片组——按导出状态过滤。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChips(
    currentFilter: ExportState?,
    counts: Map<ExportState?, Int>,
    onFilterSelected: (ExportState?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = currentFilter == null,
            onClick = { onFilterSelected(null) },
            label = {
                Text("全部 (${counts[null] ?: 0})")
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = Blue500.copy(alpha = 0.2f),
                selectedLabelColor = Blue500
            )
        )

        FilterChip(
            selected = currentFilter == ExportState.NOT_EXPORTED,
            onClick = { onFilterSelected(ExportState.NOT_EXPORTED) },
            label = {
                Text("未导出 (${counts[ExportState.NOT_EXPORTED] ?: 0})")
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = TextSecondary.copy(alpha = 0.2f),
                selectedLabelColor = TextPrimary
            )
        )

        FilterChip(
            selected = currentFilter == ExportState.EXPORTED,
            onClick = { onFilterSelected(ExportState.EXPORTED) },
            label = {
                Text("已导出 (${counts[ExportState.EXPORTED] ?: 0})")
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = Green500.copy(alpha = 0.2f),
                selectedLabelColor = Green500
            )
        )

        FilterChip(
            selected = currentFilter == ExportState.FAILED,
            onClick = { onFilterSelected(ExportState.FAILED) },
            label = {
                Text("失败 (${counts[ExportState.FAILED] ?: 0})")
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = Red500.copy(alpha = 0.2f),
                selectedLabelColor = Red500
            )
        )
    }
}