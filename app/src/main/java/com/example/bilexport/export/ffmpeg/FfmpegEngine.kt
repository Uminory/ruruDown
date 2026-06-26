package com.example.bilexport.export.ffmpeg

import android.util.Log
import com.example.bilexport.core.constants.Tags
import com.example.bilexport.core.model.ExportErrorCode
import com.example.bilexport.export.pipeline.ExportResult
import java.io.File

/**
 * FFmpeg 引擎——通过 JNI 调用 FFmpeg 原生库进行重封装。
 * 只做 FFmpeg 相关逻辑，不承载业务规则。
 */
class FfmpegEngine {

    companion object {
        private var initialized = false

        init {
            try {
                System.loadLibrary("ffmpeg_bridge")
                initialized = true
            } catch (e: UnsatisfiedLinkError) {
                Log.e(Tags.FFMPEG, "FFmpeg 原生库加载失败", e)
                initialized = false
            }
        }
    }

    /**
     * 初始化 FFmpeg 引擎。
     */
    fun init(): Boolean {
        if (!initialized) {
            Log.e(Tags.FFMPEG, "FFmpeg 引擎未初始化：原生库未加载")
            return false
        }
        try {
            nativeInit()
            Log.d(Tags.FFMPEG, "FFmpeg 引擎初始化成功")
            return true
        } catch (e: Exception) {
            Log.e(Tags.FFMPEG, "FFmpeg 引擎初始化失败", e)
            return false
        }
    }

    /**
     * 执行 remux 操作——将视频和音频分片无损重封装为 MP4。
     * @return 返回码，0 表示成功，非 0 表示失败
     */
    fun remux(
        videoPath: String,
        audioPath: String,
        outputPath: String,
        options: FfmpegOptions = FfmpegOptions()
    ): Int {
        if (!initialized) {
            Log.e(Tags.FFMPEG, "FFmpeg 未初始化，无法执行 remux")
            return -1
        }

        // 验证输入文件
        if (!File(videoPath).exists()) {
            Log.e(Tags.FFMPEG, "视频文件不存在: $videoPath")
            return -2
        }
        if (!File(audioPath).exists()) {
            Log.e(Tags.FFMPEG, "音频文件不存在: $audioPath")
            return -3
        }

        // 确保输出目录存在
        File(outputPath).parentFile?.mkdirs()

        // 构建命令
        val args = FfmpegCommandBuilder.build(videoPath, audioPath, outputPath, options)
        val cmdStr = FfmpegCommandBuilder.toCommandString(args)
        Log.d(Tags.FFMPEG, "执行命令: $cmdStr")

        return try {
            nativeRemux(videoPath, audioPath, outputPath, options.useCopy, options.overwrite)
        } catch (e: Exception) {
            Log.e(Tags.FFMPEG, "remux 执行异常", e)
            -99
        }
    }

    /**
     * 探测媒体文件信息。
     */
    fun probe(inputPath: String): MediaProbeResult {
        if (!initialized) return MediaProbeResult()
        return try {
            nativeProbe(inputPath)
        } catch (e: Exception) {
            Log.e(Tags.FFMPEG, "probe 执行异常", e)
            MediaProbeResult()
        }
    }

    /**
     * 取消指定 job 的 FFmpeg 操作。
     */
    fun cancel(jobId: String) {
        try {
            nativeCancel(jobId)
        } catch (e: Exception) {
            Log.e(Tags.FFMPEG, "cancel 执行异常", e)
        }
    }

    /**
     * 释放 FFmpeg 引擎资源。
     */
    fun release() {
        try {
            nativeRelease()
        } catch (e: Exception) {
            Log.e(Tags.FFMPEG, "release 执行异常", e)
        }
    }

    // Native 方法声明
    private external fun nativeInit()
    private external fun nativeRemux(
        videoPath: String,
        audioPath: String,
        outputPath: String,
        useCopy: Boolean,
        overwrite: Boolean
    ): Int
    private external fun nativeProbe(inputPath: String): MediaProbeResult
    private external fun nativeCancel(jobId: String)
    private external fun nativeRelease()
}