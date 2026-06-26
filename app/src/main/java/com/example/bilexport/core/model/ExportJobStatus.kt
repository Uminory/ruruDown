package com.example.bilexport.core.model

/**
 * 导出任务状态（细粒度状态机）
 */
enum class ExportJobStatus {
    /** 等待排队 */
    PENDING,
    /** 准备中 */
    PREPARING,
    /** 复制输入文件到临时目录 */
    COPYING_INPUT,
    /** FFmpeg 正在运行 */
    RUNNING_FFMPEG,
    /** 校验输出文件 */
    VERIFYING,
    /** 移动输出到目标目录 */
    MOVING_OUTPUT,
    /** 成功 */
    SUCCESS,
    /** 失败 */
    FAILED,
    /** 已取消 */
    CANCELLED;

    val isTerminal: Boolean get() = this == SUCCESS || this == FAILED || this == CANCELLED
    val isRunning: Boolean get() = this == PREPARING || this == COPYING_INPUT ||
            this == RUNNING_FFMPEG || this == VERIFYING || this == MOVING_OUTPUT
}