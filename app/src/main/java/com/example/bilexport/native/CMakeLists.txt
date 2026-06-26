cmake_minimum_required(VERSION 3.22.1)
project("ffmpeg_bridge")

# 设置 C++ 标准
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# FFmpeg 第三方库路径
# 注意：实际编译时需要将预编译的 FFmpeg .so 文件放到对应目录
set(FFMPEG_DIR ${CMAKE_SOURCE_DIR}/third_party/ffmpeg)

# 添加 FFmpeg 头文件路径
include_directories(
    ${FFMPEG_DIR}/include
    ${CMAKE_SOURCE_DIR}/jni
)

# 添加 FFmpeg 库路径
link_directories(
    ${FFMPEG_DIR}/libs/${ANDROID_ABI}
)

# JNI 桥接库
add_library(
    ffmpeg_bridge
    SHARED
    jni/ffmpeg_bridge.cpp
    jni/log_bridge.cpp
)

# 查找 Android log 库
find_library(log-lib log)

# 链接 FFmpeg 库和 Android 系统库
target_link_libraries(
    ffmpeg_bridge
    # FFmpeg 核心库
    avcodec
    avformat
    avutil
    # Android 系统库
    ${log-lib}
)