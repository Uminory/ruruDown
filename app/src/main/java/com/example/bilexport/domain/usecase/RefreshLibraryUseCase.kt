package com.example.bilexport.domain.usecase

import com.example.bilexport.core.model.MediaItem
import com.example.bilexport.core.model.ScanState
import com.example.bilexport.core.util.Result
import com.example.bilexport.data.parser.EntryJsonParser
import com.example.bilexport.domain.repository.MediaRepository
import com.example.bilexport.domain.repository.SettingsRepository
import com.example.bilexport.privileged.source.RemoteDirectoryScanner
import kotlinx.coroutines.flow.first

/**
 * 刷新库——重新扫描已有条目，更新元数据。
 */
class RefreshLibraryUseCase(
    private val mediaRepository: MediaRepository,
    private val settingsRepository: SettingsRepository,
    private val remoteScanner: RemoteDirectoryScanner
) {
    suspend fun execute(): Result<List<MediaItem>> {
        return try {
            val existingItems = mediaRepository.getAll().first()
            val updatedItems = mutableListOf<MediaItem>()

            for (item in existingItems) {
                val subDir = item.sourcePath.removePrefix("${item.avid}/")
                if (subDir.isEmpty()) continue

                val jsonStr = remoteScanner.readEntryJson(item.avid, subDir)
                if (jsonStr == null) continue

                val parsed = EntryJsonParser.parse(jsonStr)
                if (parsed != null) {
                    val updated = item.copy(
                        title = parsed.title,
                        partTitle = parsed.partTitle,
                        ownerName = parsed.ownerName,
                        isCompleted = parsed.isCompleted,
                        updatedAt = System.currentTimeMillis()
                    )
                    mediaRepository.update(updated)
                    updatedItems.add(updated)
                }
            }

            settingsRepository.setLastScanTime(System.currentTimeMillis())
            Result.success(updatedItems)
        } catch (e: Exception) {
            Result.error(
                com.example.bilexport.core.model.ExportErrorCode.UNKNOWN,
                "刷新失败: ${e.message}"
            )
        }
    }
}
