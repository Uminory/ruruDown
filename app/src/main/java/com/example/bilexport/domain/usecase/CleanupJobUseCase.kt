package com.example.bilexport.domain.usecase

import com.example.bilexport.data.cache.TempPathManager

/**
 * 清理 job 临时目录和过期日志。
 */
class CleanupJobUseCase(
    private val tempPathManager: TempPathManager
) {
    fun execute(jobId: String) {
        tempPathManager.cleanupJobDir(jobId)
    }

    fun cleanupAll() {
        tempPathManager.cleanupAll()
    }

    fun cleanupOld() {
        tempPathManager.cleanupOld()
    }
}