package com.example.bilexport.export.ffmpeg

/**
 * FFmpeg 命令构建器——将参数组装为 FFmpeg 命令字符串。
 * 参考 biliexport_demo.py 中的 FFmpeg 调用方式。
 */
object FfmpegCommandBuilder {

    /**
     * 构建 FFmpeg remux 命令参数列表。
     */
    fun build(
        videoPath: String,
        audioPath: String,
        outputPath: String,
        options: FfmpegOptions = FfmpegOptions()
    ): List<String> {
        val args = mutableListOf<String>()

        // 覆盖输出
        if (options.overwrite) args.add("-y")

        // 非交互模式
        if (options.noStdin) args.add("-nostdin")

        // 隐藏 banner
        if (options.hideBanner) args.add("-hide_banner")

        // 输入视频
        args.add("-i")
        args.add(videoPath)

        // 输入音频
        args.add("-i")
        args.add(audioPath)

        // 流复制模式
        if (options.useCopy) {
            args.add("-c")
            args.add("copy")
        }

        // 映射视频流
        args.add("-map")
        args.add(options.mapVideo)

        // 映射音频流
        args.add("-map")
        args.add(options.mapAudio)

        // 移除章节
        if (options.removeChapters) {
            args.add("-map_chapters")
            args.add("-1")
        }

        // 移除元数据
        if (options.removeMetadata) {
            args.add("-map_metadata")
            args.add("-1")
        }

        // 快速启动
        if (options.fastStart) {
            args.add("-movflags")
            args.add("+faststart")
        }

        // 输出路径
        args.add(outputPath)

        return args
    }

    /**
     * 将参数列表转换为可读的命令行字符串。
     */
    fun toCommandString(args: List<String>): String {
        return args.joinToString(" ") { if (it.contains(" ")) "\"$it\"" else it }
    }
}