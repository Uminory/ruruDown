package com.example.bilexport.data.repository

import com.example.bilexport.privileged.shizuku.BiliFileServiceClient
import com.example.bilexport.privileged.shizuku.ShizukuManager
import java.text.SimpleDateFormat
import java.util.*

class DiagnosticRepository(private val client: BiliFileServiceClient) {
    private val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    data class DiagnosticInfo(
        val shizukuAvailable: Boolean,
        val shizukuPermission: Boolean,
        val serviceInitState: String,
        val serviceConnected: Boolean,
        val biliDirAccessible: Boolean,
        val biliDirDetail: String,
        val recentErrors: List<ErrorEntry> = emptyList()
    ) {
        fun toCopyableText(): String = buildString {
            appendLine("=== ruruDown 诊断信息 ===")
            appendLine("时间: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())}")
            appendLine()
            appendLine("--- Shizuku ---")
            appendLine("可用: $shizukuAvailable")
            appendLine("已授权: $shizukuPermission")
            appendLine("服务状态: $serviceInitState")
            appendLine("已连接: $serviceConnected")
            appendLine()
            appendLine("--- B站缓存目录 ---")
            appendLine("/storage/emulated/0/Android/data/tv.danmaku.bili/download")
            appendLine("可访问: $biliDirAccessible")
            if (biliDirDetail.isNotEmpty()) appendLine("详情: $biliDirDetail")
            if (recentErrors.isNotEmpty()) {
                appendLine()
                appendLine("--- 最近错误 ---")
                recentErrors.forEach { appendLine("[${it.time}] ${it.module}: ${it.message}") }
            }
        }
    }

    data class ErrorEntry(val time: String, val module: String, val message: String)

    private val errors = mutableListOf<ErrorEntry>()
    private val maxErrors = 10

    // 缓存上次完整诊断是否"完全正常"（四项全绿）
    @Volatile private var lastFullNormal: Boolean? = null
    @Volatile private var cachedBiliDetail: String = ""

    fun addError(module: String, message: String) {
        synchronized(errors) {
            errors.add(0, ErrorEntry(dateFormat.format(Date()), module, message))
            if (errors.size > maxErrors) errors.removeAt(errors.size - 1)
        }
    }

    fun getRecentErrors(): List<ErrorEntry> = errors.toList()

    fun collectInfo(forceRefresh: Boolean = false): DiagnosticInfo {
        val shizukuOk = ShizukuManager.isAvailable.value
        val permOk = ShizukuManager.isPermissionGranted.value
        val svcConnected = client.isConnected

        // 仅当"完全正常"且非手动刷新时复用缓存；否则重新通过 Shell 诊断
        val fullOk = shizukuOk && permOk && svcConnected
        val needRecheck = forceRefresh || lastFullNormal != true || !fullOk
        val biliOk: Boolean
        val biliDetail: String
        if (needRecheck && fullOk) {
            val (ok, detail) = checkBiliDirViaShell()
            biliOk = ok
            biliDetail = detail
            lastFullNormal = fullOk && ok
            cachedBiliDetail = detail
        } else if (lastFullNormal == true && fullOk) {
            // 完全正常，复用缓存
            biliOk = true
            biliDetail = cachedBiliDetail
        } else {
            // Shizuku 都不正常，跳过 Shell 检查
            biliOk = false
            biliDetail = "Shizuku 不可用，无法检查"
            lastFullNormal = false
        }

        return DiagnosticInfo(
            shizukuAvailable = shizukuOk,
            shizukuPermission = permOk,
            serviceInitState = client.initState.value.toString(),
            serviceConnected = client.isConnected,
            biliDirAccessible = biliOk,
            biliDirDetail = biliDetail,
            recentErrors = getRecentErrors()
        )
    }

    /** 通过 Shizuku (shell UID) 检查 B站缓存根目录 */
    private fun checkBiliDirViaShell(): Pair<Boolean, String> {
        return try {
            val ok = client.checkBiliRootExists()
            if (ok) {
                // 进一步验证有内容
                val avids = client.listAvids()
                val count = avids.count { it.matches(Regex("^\\d+|s_.+")) }
                if (count == 0) false to "目录存在但无 avid 子目录"
                else true to "发现 $count 个 avid"
            } else {
                false to "目录不存在或无权限"
            }
        } catch (e: Exception) {
            false to "Shizuku 调用异常: ${e.message}"
        }
    }

    /** 清除缓存，下次 collectInfo 时会重新诊断 */
    fun invalidateBiliCache() {
        lastFullNormal = null
        cachedBiliDetail = ""
    }
}
