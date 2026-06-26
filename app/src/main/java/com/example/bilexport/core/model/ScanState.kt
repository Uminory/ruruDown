package com.example.bilexport.core.model

/**
 * 扫描状态
 */
enum class ScanState {
    /** 未扫描 */
    IDLE,
    /** 扫描中 */
    SCANNING,
    /** 扫描完成 */
    COMPLETED,
    /** 扫描失败 */
    FAILED
}