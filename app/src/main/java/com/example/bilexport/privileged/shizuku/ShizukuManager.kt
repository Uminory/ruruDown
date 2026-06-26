package com.example.bilexport.privileged.shizuku

import android.content.Context
import android.util.Log
import com.example.bilexport.core.constants.Tags
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import rikka.shizuku.Shizuku

/**
 * Shizuku 管理器——负责 Shizuku 权限检查与服务状态管理。
 * 只做系统能力管理，不承载业务逻辑。
 */
object ShizukuManager {

    private val _isAvailable = MutableStateFlow(false)
    val isAvailable: StateFlow<Boolean> = _isAvailable

    private val _isPermissionGranted = MutableStateFlow(false)
    val isPermissionGranted: StateFlow<Boolean> = _isPermissionGranted

    private val listeners = mutableListOf<Shizuku.OnRequestPermissionResultListener>()

    fun init() {
        checkState()
        Shizuku.addBinderReceivedListener {
            checkState()
        }
        Shizuku.addBinderDeadListener {
            _isAvailable.value = false
            _isPermissionGranted.value = false
        }
        Shizuku.addRequestPermissionResultListener(object : Shizuku.OnRequestPermissionResultListener {
            override fun onRequestPermissionResult(requestCode: Int, grantResult: Int) {
                _isPermissionGranted.value = grantResult == android.content.pm.PackageManager.PERMISSION_GRANTED
                listeners.forEach { it.onRequestPermissionResult(requestCode, grantResult) }
            }
        })
    }

    private fun checkState() {
        _isAvailable.value = try {
            Shizuku.pingBinder()
        } catch (e: Exception) {
            false
        }
        _isPermissionGranted.value = if (_isAvailable.value) {
            try {
                Shizuku.checkSelfPermission() == android.content.pm.PackageManager.PERMISSION_GRANTED
            } catch (e: Exception) {
                false
            }
        } else false
    }

    fun requestPermission(activity: android.app.Activity, requestCode: Int = 0) {
        if (!_isAvailable.value) return
        try {
            if (Shizuku.checkSelfPermission() != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                Shizuku.requestPermission(requestCode)
            }
        } catch (e: Exception) {
            Log.e(Tags.SHIZUKU, "请求权限失败", e)
        }
    }

    fun addPermissionListener(listener: Shizuku.OnRequestPermissionResultListener) {
        listeners.add(listener)
    }

    fun removePermissionListener(listener: Shizuku.OnRequestPermissionResultListener) {
        listeners.remove(listener)
    }

    fun release() {
        listeners.clear()
    }
}