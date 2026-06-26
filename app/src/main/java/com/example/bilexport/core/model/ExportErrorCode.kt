package com.example.bilexport.core.model

/**
 * 导出错误码（所有错误必须可分类，不允许只返回"失败"）
 */
enum class ExportErrorCode(
    val userMessage: String,
    val recoveryHint: String
) {
    PERMISSION_DENIED("权限不足", "请在设置中授予存储权限"),
    SHIZUKU_UNAVAILABLE("Shizuku 不可用", "请确保 Shizuku 正在运行并已授权"),
    SOURCE_DIR_NOT_FOUND("源目录不存在", "请检查视频缓存是否已被清理"),
    ENTRY_JSON_INVALID("entry.json 解析失败", "缓存文件可能已损坏，请尝试重新缓存"),
    MEDIA_FILES_MISSING("媒体文件缺失", "视频或音频分片不完整，请重新缓存"),
    COPY_FAILED("文件复制失败", "请检查存储空间是否充足"),
    FFMPEG_INIT_FAILED("FFmpeg 初始化失败", "请重启应用或重新安装"),
    FFMPEG_RUNTIME_ERROR("FFmpeg 执行出错", "请查看日志排查具体原因"),
    OUTPUT_WRITE_FAILED("输出文件写入失败", "请检查目标目录权限和存储空间"),
    OUTPUT_MOVE_FAILED("输出文件移动失败", "请检查目标目录权限"),
    DB_WRITE_FAILED("数据库写入失败", "请重启应用"),
    CANCELLED("已取消", ""),
    UNKNOWN("未知错误", "请查看日志排查")
}