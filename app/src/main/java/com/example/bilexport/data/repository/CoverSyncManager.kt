package com.example.bilexport.data.repository

import android.util.Log
import com.example.bilexport.core.constants.Tags
import com.example.bilexport.core.model.CoverData
import com.example.bilexport.core.model.CoverRequest
import com.example.bilexport.data.cache.CoverCache
import com.example.bilexport.privileged.shizuku.BiliFileServiceClient
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class CoverSyncState { IDLE, SYNCING, COMPLETED, FAILED }

/**
 * 封面后台同步队列。
 * Shell 返回 CoverData 字节数组，App 端自行写入缓存目录。
 * 自动分块：每次最多 CHUNK_SIZE 个请求，避免 Binder 事务超限。
 */
class CoverSyncManager(
    private val client: BiliFileServiceClient,
    private val coverCache: CoverCache,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
) {
    companion object {
        private const val CHUNK_SIZE = 8  // 每批最多 8 张，约 ~2MB 安全上限
    }

    private val _state = MutableStateFlow(CoverSyncState.IDLE)
    val state: StateFlow<CoverSyncState> = _state

    private var pendingRequests = mutableListOf<CoverRequest>()

    @Volatile
    private var running = false

    fun enqueue(requests: List<CoverRequest>) {
        synchronized(pendingRequests) {
            pendingRequests.addAll(requests)
        }
    }

    /** 异步同步：分块调用 Binder，每批收到的字节写入本地缓存 */
    fun syncAsync() {
        if (running) return
        running = true
        scope.launch {
            _state.value = CoverSyncState.SYNCING
            try {
                val batch: List<CoverRequest>
                synchronized(pendingRequests) {
                    batch = pendingRequests.toList()
                    pendingRequests.clear()
                }
                if (batch.isEmpty()) {
                    _state.value = CoverSyncState.COMPLETED
                    return@launch
                }

                var totalWritten = 0
                // 分块处理，每 CHUNK_SIZE 个请求一次 Binder 调用
                val chunks = batch.chunked(CHUNK_SIZE)
                Log.w(Tags.SHIZUKU, "CoverSync: ${batch.size} 张封面, 分 ${chunks.size} 批同步")

                for ((i, chunk) in chunks.withIndex()) {
                    val dataList: List<CoverData> = withContext(Dispatchers.IO) {
                        client.syncCoverBatch(chunk)
                    }
                    // 写入本地缓存
                    for (data in dataList) {
                        try {
                            val file = coverCache.getFileById(data.id)
                            file.parentFile?.mkdirs()
                            file.writeBytes(data.bytes)
                            totalWritten++
                        } catch (e: Exception) {
                            Log.e(Tags.SHIZUKU, "CoverSync: 写入缓存失败 id=${data.id}", e)
                        }
                    }
                    Log.w(Tags.SHIZUKU, "CoverSync: 第 ${i + 1}/${chunks.size} 批完成 (收到 ${dataList.size}, 写入 $totalWritten)")
                }
                Log.w(Tags.SHIZUKU, "CoverSync: 全部完成 $totalWritten/${batch.size}")
                _state.value = CoverSyncState.COMPLETED
            } catch (e: Exception) {
                Log.e(Tags.SHIZUKU, "CoverSync: 同步失败", e)
                _state.value = CoverSyncState.FAILED
            } finally {
                running = false
            }
        }
    }

    fun needsSync(avid: String, cid: String, sourceMtime: Long): Boolean {
        if (!coverCache.contains(avid, cid)) return true
        return coverCache.mtime(avid, cid) < sourceMtime
    }

    fun remove(avid: String, cid: String) {
        coverCache.remove(avid, cid)
    }

    fun cancel() {
        scope.cancel()
    }
}
