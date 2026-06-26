package com.example.bilexport.privileged.permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import androidx.core.content.ContextCompat
import com.example.bilexport.privileged.shizuku.ShizukuManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * 权限检查器——聚合 Shizuku 权限与存储权限状态。
 */
class PermissionChecker(private val context: Context) {

    private val _permissionState = MutableStateFlow(PermissionState())
    val permissionState: StateFlow<PermissionState> = _permissionState

    init {
        // 组合 Shizuku 状态流
        combine(
            ShizukuManager.isAvailable,
            ShizukuManager.isPermissionGranted
        ) { available, granted ->
            PermissionState(
                shizukuAvailable = available,
                shizukuPermissionGranted = granted,
                storagePermissionGranted = checkStoragePermission()
            )
        }.also { flow ->
            // 收集更新
            GlobalScope.launch(Dispatchers.Main) {
                flow.collect { _permissionState.value = it }
            }
        }
    }

    private fun checkStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun requestShizukuPermission(activity: android.app.Activity) {
        ShizukuManager.requestPermission(activity)
    }
}