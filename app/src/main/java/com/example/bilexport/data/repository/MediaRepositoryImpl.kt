package com.example.bilexport.data.repository

import com.example.bilexport.core.model.ExportState
import com.example.bilexport.core.model.MediaItem
import com.example.bilexport.data.db.AppDatabase
import com.example.bilexport.data.db.MediaItemEntity
import com.example.bilexport.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MediaRepositoryImpl(private val db: AppDatabase) : MediaRepository {

    private val dao = db.mediaItemDao()

    override fun getAll(): Flow<List<MediaItem>> =
        dao.getAllDistinct().map { list -> list.map { it.toDomain() } }

    override suspend fun getById(id: Long): MediaItem? =
        dao.getById(id)?.toDomain()

    override suspend fun getByAvid(avid: String): List<MediaItem> =
        dao.getByAvid(avid).map { it.toDomain() }

    override fun getByExportState(state: ExportState): Flow<List<MediaItem>> =
        dao.getByExportState(state).map { list -> list.map { it.toDomain() } }

    override fun search(query: String): Flow<List<MediaItem>> =
        dao.search(query).map { list -> list.map { it.toDomain() } }

    override suspend fun countByExportState(state: ExportState): Int =
        dao.countByExportState(state)

    override suspend fun count(): Int = dao.count()

    override suspend fun getAllSuspend(): List<MediaItem> =
        dao.getAllOnce().map { it.toDomain() }

    override suspend fun insert(item: MediaItem): Long {
        return dao.insert(item.toEntity())
    }

    override suspend fun insertAll(items: List<MediaItem>) {
        for (item in items) {
            val existing = dao.getByAvidAndCid(item.avid, item.cid)
            if (existing != null) {
                dao.update(
                    existing.copy(
                        title = item.title,
                        partTitle = item.partTitle,
                        ownerName = item.ownerName,
                        typeTag = item.typeTag,
                        qualityPithyDescription = item.qualityPithyDescription,
                        videoWidth = item.width,
                        videoHeight = item.height,
                        bvid = item.bvid,
                        sourcePath = item.sourcePath,
                        entryJsonPath = item.entryJsonPath,
                        coverPath = item.coverPath,
                        isCompleted = item.isCompleted,
                        duration = item.duration,
                        size = item.size,
                        sourceHash = item.sourceHash,
                        sourceMtime = item.sourceMtime,
                        updatedAt = System.currentTimeMillis()
                    )
                )
            } else {
                dao.insert(item.toEntity())
            }
        }
    }

    override suspend fun update(item: MediaItem) {
        dao.update(item.toEntity())
    }

    override suspend fun updateExportState(
        id: Long,
        state: ExportState,
        exportPath: String
    ) {
        val entity = dao.getById(id) ?: return
        val incrementCount = state == ExportState.EXPORTED
        dao.update(
            entity.copy(
                exportState = state,
                lastExportPath = exportPath,
                lastExportAt = System.currentTimeMillis(),
                exportCount = if (incrementCount) entity.exportCount + 1 else entity.exportCount,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun deleteById(id: Long) { dao.deleteById(id) }
    override suspend fun deleteByAvid(avid: String) { dao.deleteByAvid(avid) }
    override suspend fun deleteAll() { dao.deleteAll() }
}

private fun MediaItemEntity.toDomain(): MediaItem = MediaItem(
    id = id, avid = avid, cid = cid, title = title, partTitle = partTitle,
    ownerName = ownerName, typeTag = typeTag, qualityPithyDescription = qualityPithyDescription,
    width = videoWidth, height = videoHeight, bvid = bvid,
    sourcePath = sourcePath, entryJsonPath = entryJsonPath, coverPath = coverPath,
    isCompleted = isCompleted, duration = duration, size = size,
    sourceHash = sourceHash, sourceMtime = sourceMtime,
    exportState = exportState, audioExported = audioExported, danmakuExported = danmakuExported,
    lastExportAt = lastExportAt, lastExportPath = lastExportPath, exportCount = exportCount,
    createdAt = createdAt, updatedAt = updatedAt
)

private fun MediaItem.toEntity(): MediaItemEntity = MediaItemEntity(
    id = id, avid = avid, cid = cid, title = title, partTitle = partTitle,
    ownerName = ownerName, typeTag = typeTag, qualityPithyDescription = qualityPithyDescription,
    videoWidth = width, videoHeight = height, bvid = bvid,
    sourcePath = sourcePath, entryJsonPath = entryJsonPath, coverPath = coverPath,
    isCompleted = isCompleted, duration = duration, size = size,
    sourceHash = sourceHash, sourceMtime = sourceMtime,
    exportState = exportState, audioExported = audioExported, danmakuExported = danmakuExported,
    lastExportAt = lastExportAt, lastExportPath = lastExportPath, exportCount = exportCount,
    createdAt = createdAt, updatedAt = updatedAt
)
