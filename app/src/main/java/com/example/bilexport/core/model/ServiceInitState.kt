package com.example.bilexport.core.model

/**
 * Shizuku / UserService 初始化状态机。
 * 替换原来的 delay(2000)，让扫描启动变为状态驱动。
 */
sealed interface ServiceInitState {
    data object Idle : ServiceInitState
    data object Binding : ServiceInitState
    data object Ready : ServiceInitState
    data object Timeout : ServiceInitState
    data class Failed(val reason: String) : ServiceInitState
}
