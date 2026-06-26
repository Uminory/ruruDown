#ifndef FFMPEG_BRIDGE_H
#define FFMPEG_BRIDGE_H

#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif

/**
 * 初始化 FFmpeg 引擎。
 */
JNIEXPORT void JNICALL
Java_com_example_bilexport_export_ffmpeg_FfmpegEngine_nativeInit(JNIEnv *env, jobject thiz);

/**
 * 执行 remux 操作。
 * @return 0 成功，非 0 失败
 */
JNIEXPORT jint JNICALL
Java_com_example_bilexport_export_ffmpeg_FfmpegEngine_nativeRemux(
    JNIEnv *env,
    jobject thiz,
    jstring videoPath,
    jstring audioPath,
    jstring outputPath,
    jboolean useCopy,
    jboolean overwrite
);

/**
 * 探测媒体文件信息。
 */
JNIEXPORT jobject JNICALL
Java_com_example_bilexport_export_ffmpeg_FfmpegEngine_nativeProbe(
    JNIEnv *env,
    jobject thiz,
    jstring inputPath
);

/**
 * 取消指定 job 的 FFmpeg 操作。
 */
JNIEXPORT void JNICALL
Java_com_example_bilexport_export_ffmpeg_FfmpegEngine_nativeCancel(
    JNIEnv *env,
    jobject thiz,
    jstring jobId
);

/**
 * 释放 FFmpeg 引擎资源。
 */
JNIEXPORT void JNICALL
Java_com_example_bilexport_export_ffmpeg_FfmpegEngine_nativeRelease(JNIEnv *env, jobject thiz);

#ifdef __cplusplus
}
#endif

#endif // FFMPEG_BRIDGE_H