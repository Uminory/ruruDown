package com.example.bilexport.export.pipeline

import android.content.Context
import android.media.MediaScannerConnection
import android.util.Log
import com.example.bilexport.core.constants.Tags
import com.example.bilexport.core.model.*
import com.example.bilexport.core.util.FileNameSanitizer
import com.example.bilexport.data.cache.TempPathManager
import com.example.bilexport.domain.repository.ExportRepository
import com.example.bilexport.export.ffmpeg.FfmpegEngine
import com.example.bilexport.export.ffmpeg.FfmpegOptions
import com.example.bilexport.privileged.source.RemoteFileCopier
import java.io.File

/**
 * 导出流水线——执行完整的单条导出流程。
 * 步骤固定，不可被 UI 绕过。
 *
 * 流程：
 * 1. 校验媒体条目是否完整
 * 2. 创建独立 jobTempDir
 * 3. 通过 Shizuku 复制 video.m4s 和 audio.m4s 到 jobTempDir
 * 4. JNI 调用 FFmpeg remux
 * 5. 校验输出文件
 * 6. 移动输出文件到目标导出目录
 * 7. 清理 jobTempDir
 */
class ExportPipeline(
    private val ffmpegEngine: FfmpegEngine,
    private val remoteFileCopier: RemoteFileCopier,
    private val tempPathManager: TempPathManager,
    private val context: Context? = null
) {
    suspend fun execute(job: ExportJob, mediaItem: MediaItem, exportTypes: Set<ExportType> = setOf(ExportType.VIDEO)): ExportResult {
        val startTime = System.currentTimeMillis()
        val jobId = job.jobId
        var sharedDir = ""

        try {
            // 1. 校验媒体条目是否完整
            if (!mediaItem.isCompleted) {
                return ExportResult.Failure(
                    ExportErrorCode.MEDIA_FILES_MISSING,
                    "媒体条目未完成缓存"
                )
            }

            // 2. 创建临时目录（用于 FFmpeg 输出和 Shizuku 输入中转）
            val jobDir = tempPathManager.createJobDir(jobId)
            // Shizuku 服务运行在 shell UID，无法写入 App 私有目录 — 改用 /data/local/tmp/
            sharedDir = "/data/local/tmp/bili_export/$jobId"

            // 3. 复制媒体文件到临时目录
            val subDir = mediaItem.sourcePath.removePrefix("${mediaItem.avid}/")
            val tempVideoPath = "$sharedDir/video.m4s"
            val tempAudioPath = "$sharedDir/audio.m4s"
            val needsVideoCopy = exportTypes.contains(ExportType.VIDEO)
            val needsAudioCopy = exportTypes.contains(ExportType.AUDIO) || exportTypes.contains(ExportType.VIDEO)
            Log.w(Tags.EXPORT, "[$jobId] PIPELINE: sourcePath=${mediaItem.sourcePath} avid=${mediaItem.avid} subDir=$subDir typeTag=${mediaItem.typeTag}")
            Log.w(Tags.EXPORT, "[$jobId] PIPELINE: types=$exportTypes needsVideo=$needsVideoCopy needsAudio=$needsAudioCopy sharedDir=$sharedDir")
            val copied = if (needsVideoCopy || needsAudioCopy) {
                remoteFileCopier.copyMediaFiles(avid = mediaItem.avid, subDir = subDir, typeTag = mediaItem.typeTag, destDir = sharedDir)
            } else { 2 }
            Log.w(Tags.EXPORT, "[$jobId] PIPELINE: copied=$copied tempVideo=$tempVideoPath exists=${File(tempVideoPath).exists()} tempAudio=$tempAudioPath exists=${File(tempAudioPath).exists()}")
            // 视频导出需要2个文件，仅音频需要音频文件存在，仅弹幕不需文件
            val minRequired = when {
                exportTypes.contains(ExportType.VIDEO) -> 2
                exportTypes.contains(ExportType.AUDIO) -> 1
                else -> 0
            }
            if (copied < minRequired) {
                tempPathManager.cleanupJobDir(jobId)
                return ExportResult.Failure(ExportErrorCode.COPY_FAILED, "复制媒体文件失败: 需要$minRequired 成功$copied")
            }

            // 4. 导出视频（MP4 remux）
            val outputDir = job.outputDir.ifEmpty { job.outputDir }
            val finalOutputDir = File(outputDir)
            if (!finalOutputDir.exists()) finalOutputDir.mkdirs()
            // 使用 job 中已处理的 outputName 作为基础文件名
            val baseName = FileNameSanitizer.sanitize(
                job.outputName.removeSuffix(".mp4").ifEmpty { mediaItem.displayTitle }
            )

            // 确保输出目录可写，否则 fallback 到 app 专属目录
            val actualOutputDir = ensureWritable(finalOutputDir)

            val exportedPaths = mutableListOf<String>()
            var lastError: String? = null

            if (exportTypes.contains(ExportType.VIDEO)) {
                try {
                    Log.w(Tags.EXPORT, "[$jobId] VIDEO: remux start")
                    val tempOutputPath = File(jobDir, "$baseName.mp4").absolutePath
                    val options = FfmpegOptions(useCopy = true, overwrite = true, fastStart = true)
                    val returnCode = ffmpegEngine.remux(tempVideoPath, tempAudioPath, tempOutputPath, options)
                    if (returnCode != 0) {
                        lastError = "FFmpeg 返回码: $returnCode"
                    } else {
                        val validationError = ExportValidator.validate(tempOutputPath, File(tempVideoPath).length(), File(tempAudioPath).length())
                        if (validationError != null) {
                            lastError = validationError
                        } else {
                            val targetPath = if (job.overwriteExisting) File(actualOutputDir, "$baseName.mp4").absolutePath
                            else resolveOutputPath(File(actualOutputDir, "$baseName.mp4")).absolutePath
                            File(tempOutputPath).copyTo(File(targetPath), overwrite = true)
                            File(tempOutputPath).delete()
                            exportedPaths.add(targetPath)
                            Log.w(Tags.EXPORT, "[$jobId] VIDEO: ok $targetPath")
                        }
                    }
                } catch (e: Exception) {
                    Log.e(Tags.EXPORT, "[$jobId] VIDEO error: ${e.message}")
                    lastError = "视频: ${e.message}"
                }
            }

            if (exportTypes.contains(ExportType.AUDIO)) {
                try {
                    val audioOutputPath = if (job.overwriteExisting) File(actualOutputDir, "$baseName.m4a").absolutePath
                    else resolveOutputPath(File(actualOutputDir, "$baseName.m4a")).absolutePath
                    Log.w(Tags.EXPORT, "[$jobId] AUDIO: src=$tempAudioPath dst=$audioOutputPath")
                    // 先复制到 app 缓存再移到目标（避免 /data/local/tmp 源文件的 EPERM 问题）
                    val tempM4a = File(jobDir, "$baseName.m4a")
                    File(tempAudioPath).copyTo(tempM4a, overwrite = true)
                    tempM4a.copyTo(File(audioOutputPath), overwrite = true)
                    tempM4a.delete()
                    Log.w(Tags.EXPORT, "[$jobId] AUDIO: ok $audioOutputPath")
                    exportedPaths.add(audioOutputPath)
                } catch (e: Exception) {
                    Log.e(Tags.EXPORT, "[$jobId] AUDIO error: ${e.message}")
                    lastError = "音频: ${e.message}"
                }
            }

            if (exportTypes.contains(ExportType.DANMAKU)) {
                try {
                    val danmakuOutputPath = if (job.overwriteExisting) File(actualOutputDir, "$baseName.xml").absolutePath
                    else resolveOutputPath(File(actualOutputDir, "$baseName.xml")).absolutePath
                    Log.w(Tags.EXPORT, "[$jobId] DANMAKU: read avid=${mediaItem.avid} subDir=$subDir")
                    val danmakuBytes = remoteFileCopier.readDanmakuXml(mediaItem.avid, subDir)
                    if (danmakuBytes != null) {
                        val tempXml = File(jobDir, "$baseName.xml")
                        tempXml.writeBytes(danmakuBytes)
                        tempXml.copyTo(File(danmakuOutputPath), overwrite = true)
                        tempXml.delete()
                        exportedPaths.add(danmakuOutputPath)
                        Log.w(Tags.EXPORT, "[$jobId] DANMAKU: ok $danmakuOutputPath size=${danmakuBytes.size}")
                    } else {
                        Log.w(Tags.EXPORT, "[$jobId] DANMAKU: read returned null")
                    }
                } catch (e: Exception) {
                    Log.e(Tags.EXPORT, "[$jobId] DANMAKU error: ${e.message}")
                    lastError = "弹幕: ${e.message}"
                }
            }

            // 7. 清理临时目录
            tempPathManager.cleanupJobDir(jobId)
            try { File(sharedDir).deleteRecursively() } catch (_: Exception) {}

            val elapsed = System.currentTimeMillis() - startTime
            val primaryPath = exportedPaths.firstOrNull() ?: ""
            Log.w(Tags.EXPORT, "[$jobId] 导出完成: $exportedPaths errors=${lastError ?: "无"} (耗时 ${elapsed}ms)")

            exportedPaths.forEach { path ->
                context?.let { ctx ->
                    MediaScannerConnection.scanFile(ctx, arrayOf(path), null) { _, _ -> }
                }
            }

            return if (exportedPaths.isNotEmpty()) {
                ExportResult.Success(primaryPath, elapsed)
            } else {
                ExportResult.Failure(ExportErrorCode.OUTPUT_WRITE_FAILED, lastError ?: "所有导出项均失败")
            }

        } catch (e: Exception) {
            Log.e(Tags.EXPORT, "[$jobId] 导出异常", e)
            tempPathManager.cleanupJobDir(jobId)
            try { File(sharedDir).deleteRecursively() } catch (_: Exception) {}
            return ExportResult.Failure(
                ExportErrorCode.UNKNOWN,
                "导出异常: ${e.message}"
            )
        }
    }

    /**
     * 解决输出路径冲突：如果目标已存在，添加序号后缀。
     */
    private fun resolveOutputPath(target: File): File {
        if (!target.exists()) return target

        val parent = target.parentFile ?: return target
        val name = target.nameWithoutExtension
        val ext = target.extension

        var counter = 1
        var resolved: File
        do {
            resolved = File(parent, "${name}_($counter).$ext")
            counter++
        } while (resolved.exists())

        return resolved
    }

    /** 确保输出目录可写。若主目录写入失败，fallback 到 app 专属 Movies 目录。 */
    private fun ensureWritable(dir: File): File {
        if (!dir.exists()) dir.mkdirs()
        // 实际创建测试文件验证可写性
        val test = File(dir, ".rw_test")
        try {
            test.writeText("1")
            test.delete()
            return dir
        } catch (e: Exception) {
            // 不可写，fallback
            val fallback = File(context?.getExternalFilesDir(android.os.Environment.DIRECTORY_MOVIES), dir.name)
            if (!fallback.exists()) fallback.mkdirs()
            Log.w(Tags.EXPORT, "EPERM fallback: ${dir.absolutePath} -> ${fallback.absolutePath}")
            return fallback
        }
    }
}