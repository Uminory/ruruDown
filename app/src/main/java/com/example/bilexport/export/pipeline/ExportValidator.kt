package com.example.bilexport.export.pipeline

import android.util.Log
import com.example.bilexport.core.constants.Tags
import com.example.bilexport.core.model.ExportErrorCode
import java.io.File

/**
 * 导出验证器——校验输出文件是否存在且大小合理。
 */
object ExportValidator {

    private const val MIN_VALID_SIZE = 1024L // 至少 1KB

    /**
     * 验证输出文件。
     * @return null 表示验证通过，否则返回错误信息
     */
    fun validate(outputPath: String, videoSize: Long = 0, audioSize: Long = 0): String? {
        val file = File(outputPath)

        if (!file.exists()) {
            return "输出文件不存在: $outputPath"
        }

        if (!file.isFile) {
            return "输出路径不是文件: $outputPath"
        }

        val fileSize = file.length()
        if (fileSize < MIN_VALID_SIZE) {
            return "输出文件过小: ${fileSize} bytes"
        }

        // 如果提供了源文件大小，做一些合理性检查
        if (videoSize > 0 && audioSize > 0) {
            val expectedMin = (videoSize + audioSize) * 0.8
            if (fileSize < expectedMin) {
                Log.w(Tags.EXPORT, "输出文件(${fileSize})明显小于源文件总和(${videoSize + audioSize})")
            }
        }

        return null // 验证通过
    }
}