package com.example.bilexport.domain.repository

import com.example.bilexport.core.model.ExportState
import com.example.bilexport.core.model.MediaItem
import kotlinx.coroutines.flow.Flow

interface MediaRepository {
    fun getAll(): Flow<List<MediaItem>>
    suspend fun getById(id: Long): MediaItem?
    suspend fun getByAvid(avid: String): List<MediaItem>
    fun getByExportState(state: ExportState): Flow<List<MediaItem>>
    fun search(query: String): Flow<List<MediaItem>>
    suspend fun countByExportState(state: ExportState): Int
    suspend fun count(): Int
    suspend fun getAllSuspend(): List<MediaItem>
    suspend fun insert(item: MediaItem): Long
    suspend fun insertAll(items: List<MediaItem>)
    suspend fun update(item: MediaItem)
    suspend fun updateExportState(id: Long, state: ExportState, exportPath: String)
    suspend fun deleteById(id: Long)
    suspend fun deleteByAvid(avid: String)
    suspend fun deleteAll()
}