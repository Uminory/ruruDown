#include "ffmpeg_bridge.h"
#include <jni.h>
#include <string>
#include <mutex>
#include <atomic>

// FFmpeg 头文件
extern "C" {
#include <libavformat/avformat.h>
#include <libavcodec/avcodec.h>
#include <libavutil/avutil.h>
#include <libavutil/log.h>
}

// Android log
#include <android/log.h>

#define LOG_TAG "BiliExport::FFmpeg"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

static std::atomic<bool> g_initialized{false};
static std::mutex g_mutex;

/**
 * 初始化 FFmpeg 引擎。
 */
JNIEXPORT void JNICALL
Java_com_example_bilexport_export_ffmpeg_FfmpegEngine_nativeInit(JNIEnv *env, jobject thiz) {
    std::lock_guard<std::mutex> lock(g_mutex);
    if (g_initialized.load()) {
        LOGD("FFmpeg already initialized");
        return;
    }

    // 设置 FFmpeg 日志级别
    av_log_set_level(AV_LOG_WARNING);
    LOGD("FFmpeg initialized");
    g_initialized.store(true);
}

/**
 * 执行 remux 操作——将视频和音频分片无损重封装为 MP4。
 * 参考 biliexport_demo.py 中的 FFmpeg 调用方式。
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
) {
    if (!g_initialized.load()) {
        LOGE("FFmpeg not initialized");
        return -1;
    }

    const char *videoPathStr = env->GetStringUTFChars(videoPath, nullptr);
    const char *audioPathStr = env->GetStringUTFChars(audioPath, nullptr);
    const char *outputPathStr = env->GetStringUTFChars(outputPath, nullptr);

    LOGD("Remux: video=%s, audio=%s, output=%s", videoPathStr, audioPathStr, outputPathStr);

    int ret = 0;
    int videoStreamIdx = -1;
    int audioStreamIdx = -1;
    AVFormatContext *inputVideoCtx = nullptr;
    AVFormatContext *inputAudioCtx = nullptr;
    AVFormatContext *outputCtx = nullptr;
    AVPacket *pkt = nullptr;

    // 打开视频输入
    ret = avformat_open_input(&inputVideoCtx, videoPathStr, nullptr, nullptr);
    if (ret < 0) {
        LOGE("Failed to open video input: %s", videoPathStr);
        goto cleanup;
    }
    ret = avformat_find_stream_info(inputVideoCtx, nullptr);
    if (ret < 0) {
        LOGE("Failed to find video stream info");
        goto cleanup;
    }

    // 打开音频输入
    ret = avformat_open_input(&inputAudioCtx, audioPathStr, nullptr, nullptr);
    if (ret < 0) {
        LOGE("Failed to open audio input: %s", audioPathStr);
        goto cleanup;
    }
    ret = avformat_find_stream_info(inputAudioCtx, nullptr);
    if (ret < 0) {
        LOGE("Failed to find audio stream info");
        goto cleanup;
    }

    // 创建输出 context
    ret = avformat_alloc_output_context2(&outputCtx, nullptr, "mp4", outputPathStr);
    if (ret < 0 || !outputCtx) {
        LOGE("Failed to create output context");
        goto cleanup;
    }

    // 找到最佳视频流并复制
    videoStreamIdx = av_find_best_stream(inputVideoCtx, AVMEDIA_TYPE_VIDEO, -1, -1, nullptr, 0);
    if (videoStreamIdx >= 0) {
        AVStream *inVideoStream = inputVideoCtx->streams[videoStreamIdx];
        AVStream *outVideoStream = avformat_new_stream(outputCtx, nullptr);
        if (outVideoStream) {
            avcodec_parameters_copy(outVideoStream->codecpar, inVideoStream->codecpar);
            outVideoStream->time_base = inVideoStream->time_base;
        }
    }

    // 找到最佳音频流并复制
    audioStreamIdx = av_find_best_stream(inputAudioCtx, AVMEDIA_TYPE_AUDIO, -1, -1, nullptr, 0);
    if (audioStreamIdx >= 0) {
        AVStream *inAudioStream = inputAudioCtx->streams[audioStreamIdx];
        AVStream *outAudioStream = avformat_new_stream(outputCtx, nullptr);
        if (outAudioStream) {
            avcodec_parameters_copy(outAudioStream->codecpar, inAudioStream->codecpar);
            outAudioStream->time_base = inAudioStream->time_base;
        }
    }

    // 打开输出文件
    if (!(outputCtx->oformat->flags & AVFMT_NOFILE)) {
        ret = avio_open(&outputCtx->pb, outputPathStr, AVIO_FLAG_WRITE);
        if (ret < 0) {
            LOGE("Failed to open output file: %s", outputPathStr);
            goto cleanup;
        }
    }

    // 写文件头
    ret = avformat_write_header(outputCtx, nullptr);
    if (ret < 0) {
        LOGE("Failed to write header");
        goto cleanup;
    }

    // 分配 packet
    pkt = av_packet_alloc();
    if (!pkt) {
        LOGE("Failed to allocate packet");
        ret = AVERROR(ENOMEM);
        goto cleanup;
    }

    // 复制视频流
    if (videoStreamIdx >= 0) {
        while (av_read_frame(inputVideoCtx, pkt) >= 0) {
            if (pkt->stream_index == videoStreamIdx) {
                // 重新计算时间戳
                AVStream *inStream = inputVideoCtx->streams[videoStreamIdx];
                AVStream *outStream = outputCtx->streams[0]; // 第一个输出流是视频
                pkt->stream_index = 0;
                pkt->pts = av_rescale_q_rnd(pkt->pts, inStream->time_base, outStream->time_base,
                    static_cast<AVRounding>(AV_ROUND_NEAR_INF | AV_ROUND_PASS_MINMAX));
                pkt->dts = av_rescale_q_rnd(pkt->dts, inStream->time_base, outStream->time_base,
                    static_cast<AVRounding>(AV_ROUND_NEAR_INF | AV_ROUND_PASS_MINMAX));
                pkt->duration = av_rescale_q(pkt->duration, inStream->time_base, outStream->time_base);
                av_interleaved_write_frame(outputCtx, pkt);
            }
            av_packet_unref(pkt);
        }
    }

    // 复制音频流
    if (audioStreamIdx >= 0) {
        // 重置读取位置
        avformat_seek_file(inputAudioCtx, -1, 0, 0, 0, 0);
        while (av_read_frame(inputAudioCtx, pkt) >= 0) {
            if (pkt->stream_index == audioStreamIdx) {
                AVStream *inStream = inputAudioCtx->streams[audioStreamIdx];
                AVStream *outStream = outputCtx->streams[videoStreamIdx >= 0 ? 1 : 0];
                pkt->stream_index = videoStreamIdx >= 0 ? 1 : 0;
                pkt->pts = av_rescale_q_rnd(pkt->pts, inStream->time_base, outStream->time_base,
                    static_cast<AVRounding>(AV_ROUND_NEAR_INF | AV_ROUND_PASS_MINMAX));
                pkt->dts = av_rescale_q_rnd(pkt->dts, inStream->time_base, outStream->time_base,
                    static_cast<AVRounding>(AV_ROUND_NEAR_INF | AV_ROUND_PASS_MINMAX));
                pkt->duration = av_rescale_q(pkt->duration, inStream->time_base, outStream->time_base);
                av_interleaved_write_frame(outputCtx, pkt);
            }
            av_packet_unref(pkt);
        }
    }

    // 写文件尾
    av_write_trailer(outputCtx);
    LOGD("Remux completed successfully");

cleanup:
    // 释放资源
    if (pkt) av_packet_free(&pkt);
    if (outputCtx) {
        if (!(outputCtx->oformat->flags & AVFMT_NOFILE)) {
            avio_closep(&outputCtx->pb);
        }
        avformat_free_context(outputCtx);
    }
    if (inputVideoCtx) avformat_close_input(&inputVideoCtx);
    if (inputAudioCtx) avformat_close_input(&inputAudioCtx);

    env->ReleaseStringUTFChars(videoPath, videoPathStr);
    env->ReleaseStringUTFChars(audioPath, audioPathStr);
    env->ReleaseStringUTFChars(outputPath, outputPathStr);

    if (ret < 0) {
        char errbuf[256];
        av_strerror(ret, errbuf, sizeof(errbuf));
        LOGE("Remux failed: %s", errbuf);
    }

    return ret;
}

/**
 * 探测媒体文件信息。
 */
JNIEXPORT jobject JNICALL
Java_com_example_bilexport_export_ffmpeg_FfmpegEngine_nativeProbe(
    JNIEnv *env,
    jobject thiz,
    jstring inputPath
) {
    // 获取 MediaProbeResult 类
    jclass probeClass = env->FindClass("com/example/bilexport/export/ffmpeg/MediaProbeResult");
    if (!probeClass) {
        LOGE("Failed to find MediaProbeResult class");
        return nullptr;
    }

    jmethodID constructor = env->GetMethodID(probeClass, "<init>", "(ZZLjava/lang/String;Ljava/lang/String;IIJJ)V");
    if (!constructor) {
        LOGE("Failed to find MediaProbeResult constructor");
        return nullptr;
    }

    const char *pathStr = env->GetStringUTFChars(inputPath, nullptr);
    AVFormatContext *ctx = nullptr;

    int ret = avformat_open_input(&ctx, pathStr, nullptr, nullptr);
    if (ret < 0) {
        env->ReleaseStringUTFChars(inputPath, pathStr);
        return env->NewObject(probeClass, constructor,
            JNI_FALSE, JNI_FALSE,
            env->NewStringUTF(""), env->NewStringUTF(""),
            0, 0, 0L, 0L);
    }

    avformat_find_stream_info(ctx, nullptr);

    bool hasVideo = false, hasAudio = false;
    const char *videoCodec = "", *audioCodec = "";
    int width = 0, height = 0;
    int64_t duration = ctx->duration / AV_TIME_BASE;
    int64_t bitRate = ctx->bit_rate;

    for (unsigned int i = 0; i < ctx->nb_streams; i++) {
        AVStream *stream = ctx->streams[i];
        if (stream->codecpar->codec_type == AVMEDIA_TYPE_VIDEO) {
            hasVideo = true;
            videoCodec = avcodec_get_name(stream->codecpar->codec_id);
            width = stream->codecpar->width;
            height = stream->codecpar->height;
        } else if (stream->codecpar->codec_type == AVMEDIA_TYPE_AUDIO) {
            hasAudio = true;
            audioCodec = avcodec_get_name(stream->codecpar->codec_id);
        }
    }

    jobject result = env->NewObject(probeClass, constructor,
        hasVideo ? JNI_TRUE : JNI_FALSE,
        hasAudio ? JNI_TRUE : JNI_FALSE,
        env->NewStringUTF(videoCodec),
        env->NewStringUTF(audioCodec),
        width, height, duration, bitRate);

    avformat_close_input(&ctx);
    env->ReleaseStringUTFChars(inputPath, pathStr);

    return result;
}

/**
 * 取消指定 job 的 FFmpeg 操作。
 * 注意：当前实现为简化版本，完整实现需要维护 job 映射并支持中断。
 */
JNIEXPORT void JNICALL
Java_com_example_bilexport_export_ffmpeg_FfmpegEngine_nativeCancel(
    JNIEnv *env,
    jobject thiz,
    jstring jobId
) {
    const char *jobIdStr = env->GetStringUTFChars(jobId, nullptr);
    LOGD("Cancel requested for job: %s", jobIdStr);
    // TODO: 实现基于 jobId 的中断机制
    // 当前受限于 FFmpeg API 的线程安全特性，完整取消机制需要配合超时和回调
    env->ReleaseStringUTFChars(jobId, jobIdStr);
}

/**
 * 释放 FFmpeg 引擎资源。
 */
JNIEXPORT void JNICALL
Java_com_example_bilexport_export_ffmpeg_FfmpegEngine_nativeRelease(JNIEnv *env, jobject thiz) {
    std::lock_guard<std::mutex> lock(g_mutex);
    LOGD("FFmpeg engine released");
    g_initialized.store(false);
}