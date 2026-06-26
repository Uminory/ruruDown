package com.example.bilexport.privileged.permission

/**
 * 权限状态封装。
 */
data class PermissionState(
    val shizukuAvailable: Boolean = false,
    val shizukuPermissionGranted: Boolean = false,
    val storagePermissionGranted: Boolean = false
) {
    val isReady: Boolean
        get() = shizukuAvailable && shizukuPermissionGranted && storagePermissionGranted
}