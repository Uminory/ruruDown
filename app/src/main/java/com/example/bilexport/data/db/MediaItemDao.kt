package com.example.bilexport.data.db

import androidx.room.*
import com.example.bilexport.core.model.ExportState
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaItemDao {

    @Query("SELECT * FROM media_items ORDER BY updated_at DESC")
    fun getAll(): Flow<List<MediaItemEntity>>

    @Query("SELECT * FROM media_items ORDER BY updated_at DESC")
    fun getAllDistinct(): Flow<List<MediaItemEntity>>

    /** 一次性获取全部条目（用于减量比对） */
    @Query("SELECT * FROM media_items")
    suspend fun getAllOnce(): List<MediaItemEntity>

    @Query("SELECT * FROM media_items WHERE id = :id")
    suspend fun getById(id: Long): MediaItemEntity?

    @Query("SELECT * FROM media_items WHERE avid = :avid AND cid = :cid LIMIT 1")
    suspend fun getByAvidAndCid(avid: String, cid: String): MediaItemEntity?

    @Query("SELECT * FROM media_items WHERE avid = :avid")
    suspend fun getByAvid(avid: String): List<MediaItemEntity>

    @Query("SELECT * FROM media_items WHERE export_state = :state ORDER BY updated_at DESC")
    fun getByExportState(state: ExportState): Flow<List<MediaItemEntity>>

    @Query("SELECT * FROM media_items WHERE title LIKE '%' || :query || '%' OR owner_name LIKE '%' || :query || '%' OR avid LIKE '%' || :query || '%'")
    fun search(query: String): Flow<List<MediaItemEntity>>

    @Query("SELECT COUNT(*) FROM media_items WHERE export_state = :state")
    suspend fun countByExportState(state: ExportState): Int

    @Query("SELECT COUNT(*) FROM media_items")
    suspend fun count(): Int

    @Query("SELECT * FROM media_items WHERE source_hash = :hash AND id != :excludeId LIMIT 1")
    suspend fun findByHashExcluding(hash: String, excludeId: Long): MediaItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: MediaItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<MediaItemEntity>)

    @Update
    suspend fun update(item: MediaItemEntity)

    @Query("DELETE FROM media_items WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM media_items WHERE avid = :avid")
    suspend fun deleteByAvid(avid: String)

    @Query("DELETE FROM media_items")
    suspend fun deleteAll()
}
