package com.example.bilexport.domain.usecase

import com.example.bilexport.core.constants.Tags
import com.example.bilexport.core.model.CoverRequest
import com.example.bilexport.core.model.ExportState
import com.example.bilexport.core.model.MediaItem
import com.example.bilexport.core.model.ScanState
import com.example.bilexport.core.util.Result
import com.example.bilexport.data.cache.CoverCache
import com.example.bilexport.data.parser.EntryJsonParser
import com.example.bilexport.data.repository.CoverSyncManager
import com.example.bilexport.domain.repository.MediaRepository
import com.example.bilexport.domain.repository.SettingsRepository
import com.example.bilexport.privileged.source.RemoteDirectoryScanner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import android.util.Log

class ScanLibraryUseCase(
    private val mediaRepository: MediaRepository,
    private val settingsRepository: SettingsRepository,
    private val remoteScanner: RemoteDirectoryScanner,
    private val coverCache: CoverCache? = null,
    private val coverSyncManager: CoverSyncManager? = null
) {
    private val _scanState = MutableStateFlow(ScanState.IDLE)
    val scanState: StateFlow<ScanState> = _scanState

    suspend fun execute(): Result<List<MediaItem>> {
        _scanState.value = ScanState.SCANNING
        return try {
            val avids = remoteScanner.listAvidDirectories()
            Log.d(Tags.SCANNER, "发现 ${avids.size} 个 avid")

            if (avids.isEmpty()) {
                _scanState.value = ScanState.COMPLETED
                return Result.success(emptyList())
            }

            val enableCovers = settingsRepository.enableCoverCache.first()
            val mediaItems = mutableListOf<MediaItem>()
            val coverRequests = mutableListOf<CoverRequest>()

            for (avid in avids) {
                val subDirs = remoteScanner.listCidDirectories(avid)
                for (subDir in subDirs) {
                    val jsonStr = remoteScanner.readEntryJson(avid, subDir) ?: continue
                    val parsed = EntryJsonParser.parse(jsonStr) ?: continue

                    val typeTag = parsed.typeTag
                    if (typeTag != null) {
                        val filesExist = remoteScanner.checkM4sFilesExist(avid, subDir, typeTag)
                        if (!filesExist) Log.w(Tags.SCANNER, "avid=$avid, subDir=$subDir: 缺少 m4s 文件")
                    }

                    val coverPath = if (coverCache != null) {
                        val p = coverCache.filePath(parsed.avid, parsed.cid)
                        if (enableCovers && coverSyncManager != null &&
                            coverSyncManager.needsSync(parsed.avid, parsed.cid, parsed.timeUpdateStamp)) {
                            coverRequests.add(
                                CoverRequest(avid = parsed.avid, cid = parsed.cid, subDir = subDir, destPath = p)
                            )
                        }
                        p
                    } else ""

                    val item = MediaItem(
                        avid = parsed.avid, cid = parsed.cid,
                        title = parsed.title, partTitle = parsed.partTitle, ownerName = parsed.ownerName,
                        typeTag = parsed.typeTag ?: "",
                        qualityPithyDescription = parsed.qualityPithyDescription,
                        width = parsed.width, height = parsed.height,
                        sourcePath = "$avid/$subDir", entryJsonPath = "$avid/$subDir/entry.json",
                        coverPath = coverPath,
                        isCompleted = parsed.isCompleted, duration = parsed.duration, size = parsed.totalBytes,
                        bvid = parsed.bvid, exportState = ExportState.NOT_EXPORTED,
                        createdAt = parsed.timeUpdateStamp, updatedAt = parsed.timeUpdateStamp
                    )
                    mediaItems.add(item)
                }
            }

            if (mediaItems.isNotEmpty()) {
                mediaRepository.insertAll(mediaItems)
                val scannedKeys = mediaItems.map { "${it.avid}:${it.cid}" }.toSet()
                val existingItems = mediaRepository.getAllSuspend()
                val toDelete = existingItems.filter { "${it.avid}:${it.cid}" !in scannedKeys }
                for (item in toDelete) { mediaRepository.deleteById(item.id); coverCache?.remove(item.avid, item.cid) }
                if (toDelete.isNotEmpty()) Log.d(Tags.SCANNER, "减量清理: 删除 ${toDelete.size} 个过期条目")
            } else {
                val count = mediaRepository.count()
                if (count > 0) { mediaRepository.deleteAll(); coverCache?.clear() }
            }

            if (coverRequests.isNotEmpty() && coverSyncManager != null) {
                Log.d(Tags.SCANNER, "封面同步队列: ${coverRequests.size} 张")
                coverSyncManager.enqueue(coverRequests)
                coverSyncManager.syncAsync()
            }

            Log.d(Tags.SCANNER, "扫描完成: ${mediaItems.size} 个条目")
            _scanState.value = ScanState.COMPLETED
            Result.success(mediaItems)
        } catch (e: Exception) {
            Log.e(Tags.SCANNER, "扫描失败: ${e.message}", e)
            _scanState.value = ScanState.FAILED
            Result.error(com.example.bilexport.core.model.ExportErrorCode.UNKNOWN, "扫描失败: ${e.message}")
        }
    }
}
