package com.example.bilexport.privileged.shizuku

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import com.example.bilexport.core.constants.Tags
import com.example.bilexport.core.model.ServiceInitState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import rikka.shizuku.Shizuku

/**
 * BiliFileService 客户端封装，通过 Shizuku 绑定 UserService。
 * 暴露 ServiceInitState 状态流，供 MainViewModel 驱动扫描启动。
 */
class BiliFileServiceClient {

    private var service: IBiliFileService? = null
    private var isBound = false

    val isConnected: Boolean get() = service != null

    private val _initState = MutableStateFlow<ServiceInitState>(ServiceInitState.Idle)
    val initState: StateFlow<ServiceInitState> = _initState

    var onConnected: (() -> Unit)? = null
    var onDisconnected: (() -> Unit)? = null

    private lateinit var serviceArgs: Shizuku.UserServiceArgs
    private lateinit var serviceConnection: ServiceConnection

    private val binderReceivedListener = Shizuku.OnBinderReceivedListener {
        if (!isBound) bind()
    }

    private val binderDeadListener = Shizuku.OnBinderDeadListener {
        service = null
        onDisconnected?.invoke()
        retryBind()
    }

    fun init() {
        Shizuku.addBinderReceivedListener(binderReceivedListener)
        Shizuku.addBinderDeadListener(binderDeadListener)
        try {
            if (Shizuku.pingBinder() && !isBound) {
                bind()
            }
        } catch (_: Exception) {}
    }

    private fun bind() {
        if (isBound) return
        isBound = true
        _initState.value = ServiceInitState.Binding

        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
                if (binder != null) {
                    service = IBiliFileService.Stub.asInterface(binder)
                    Log.d(Tags.SHIZUKU, "文件服务已连接")
                    _initState.value = ServiceInitState.Ready
                    onConnected?.invoke()
                } else {
                    _initState.value = ServiceInitState.Failed("Binder 为空")
                }
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                Log.d(Tags.SHIZUKU, "文件服务已断开")
                service = null
                onDisconnected?.invoke()
            }
        }

        serviceArgs = Shizuku.UserServiceArgs(
            ComponentName("com.soyo.rurudown", BiliFileService::class.java.name)
        )
            .processNameSuffix("bili_file")
            .version(1)
            .daemon(false)

        try {
            Shizuku.bindUserService(serviceArgs, serviceConnection)
        } catch (e: Exception) {
            Log.e(Tags.SHIZUKU, "绑定服务失败", e)
            isBound = false
            _initState.value = ServiceInitState.Failed("绑定异常: ${e.message}")
        }
    }

    private var retryCount = 0
    private val maxRetries = 3

    private fun retryBind() {
        if (retryCount >= maxRetries) {
            _initState.value = ServiceInitState.Timeout
            Log.w(Tags.SHIZUKU, "UserService 绑定重试已达上限($maxRetries)")
            return
        }
        retryCount++
        isBound = false
        Log.d(Tags.SHIZUKU, "重试绑定 UserService ($retryCount/$maxRetries)")
        try {
            if (Shizuku.pingBinder()) bind()
        } catch (_: Exception) {}
    }

    fun release() {
        try {
            if (isBound) Shizuku.unbindUserService(serviceArgs, serviceConnection, true)
        } catch (_: Exception) {}
        Shizuku.removeBinderReceivedListener(binderReceivedListener)
        Shizuku.removeBinderDeadListener(binderDeadListener)
        isBound = false
        service = null
        _initState.value = ServiceInitState.Idle
    }

    // ========== 业务方法 ==========

    fun listAvids(): List<String> {
        return try { service?.listAvids() ?: emptyList() } catch (e: RemoteException) { emptyList() }
    }

    fun listSubDirs(avid: String): List<String> {
        return try { service?.listSubDirs(avid) ?: emptyList() } catch (e: RemoteException) { emptyList() }
    }

    fun readEntryJson(avid: String, subDir: String): String? {
        return try {
            val json = service?.readEntryJson(avid, subDir)
            if (json.isNullOrBlank()) null else json
        } catch (e: RemoteException) { null }
    }

    fun readCoverBytes(avid: String, subDir: String): ByteArray {
        return try { service?.readCoverBytes(avid, subDir) ?: ByteArray(0) } catch (e: RemoteException) { ByteArray(0) }
    }

    fun listQualityFolders(avid: String, subDir: String): List<String> {
        return try { service?.listQualityFolders(avid, subDir) ?: emptyList() } catch (e: RemoteException) { emptyList() }
    }

    fun checkM4sFilesExist(avid: String, subDir: String, typeTag: String): Boolean {
        return try { service?.checkM4sFilesExist(avid, subDir, typeTag) ?: false } catch (e: RemoteException) { false }
    }

    fun copyM4sFiles(avid: String, subDir: String, typeTag: String, destDir: String): Int {
        return try { service?.copyM4sFiles(avid, subDir, typeTag, destDir) ?: 0 } catch (e: RemoteException) { 0 }
    }

    fun readDanmakuXml(avid: String, subDir: String): String? {
        return try { service?.readDanmakuXml(avid, subDir) } catch (e: RemoteException) { null }
    }

    fun syncCoverBatch(requests: List<com.example.bilexport.core.model.CoverRequest>): List<com.example.bilexport.core.model.CoverData> {
        return try { service?.syncCoverBatch(requests) ?: emptyList() } catch (e: RemoteException) { emptyList() }
    }

    fun checkBiliRootExists(): Boolean {
        return try { service?.checkBiliRootExists() ?: false } catch (e: RemoteException) { false }
    }

    // ========== 诊断方法 ==========

    fun pingService(): Boolean = isConnected

    fun tryListRoot(): String {
        return try {
            val avids = service?.listAvids()
            if (avids == null) "获取失败 (null)"
            else "成功, 发现 ${avids.size} 个 avid"
        } catch (e: Exception) { "异常: ${e.message}" }
    }

    fun tryReadEntry(): String {
        return try {
            val avids = service?.listAvids() ?: return "无 avid"
            if (avids.isEmpty()) return "无 avid"
            val subDirs = service?.listSubDirs(avids.first()) ?: return "无 subDir"
            if (subDirs.isEmpty()) return "无 subDir"
            val json = service?.readEntryJson(avids.first(), subDirs.first())
            if (json.isNullOrBlank()) "entry.json 为空" else "成功 (${json.length} 字符)"
        } catch (e: Exception) { "异常: ${e.message}" }
    }

    fun tryCopyM4s(): String {
        return try {
            val avids = service?.listAvids() ?: return "无 avid"
            if (avids.isEmpty()) return "无 avid"
            val subDirs = service?.listSubDirs(avids.first()) ?: return "无 subDir"
            if (subDirs.isEmpty()) return "无 subDir"
            val typeTags = service?.listQualityFolders(avids.first(), subDirs.first()) ?: return "无 typeTag"
            if (typeTags.isEmpty()) return "无 typeTag"
            val copied = service?.copyM4sFiles(avids.first(), subDirs.first(), typeTags.first(), "/data/local/tmp")
            "复制 $copied 个文件"
        } catch (e: Exception) { "异常: ${e.message}" }
    }

    fun tryDanmaku(): String {
        return try {
            val avids = service?.listAvids() ?: return "无 avid"
            if (avids.isEmpty()) return "无 avid"
            val subDirs = service?.listSubDirs(avids.first()) ?: return "无 subDir"
            if (subDirs.isEmpty()) return "无 subDir"
            val xml = service?.readDanmakuXml(avids.first(), subDirs.first())
            if (xml.isNullOrBlank()) "danmaku.xml 为空" else "成功 (${xml.length} 字符)"
        } catch (e: Exception) { "异常: ${e.message}" }
    }
}
