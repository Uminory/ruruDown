#include <jni.h>
#include <android/log.h>

#define LOG_TAG "BiliExport::JNI"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

/**
 * 日志桥接——将 Native 层日志输出到 Android logcat。
 */

extern "C" {

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    LOGD("JNI_OnLoad: ffmpeg_bridge loaded");
    return JNI_VERSION_1_6;
}

JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *vm, void *reserved) {
    LOGD("JNI_OnUnload: ffmpeg_bridge unloaded");
}

} // extern "C"