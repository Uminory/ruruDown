#!/usr/bin/env bash
# 按 LGPL v2.1 自行编译 FFmpeg 共享库（arm64-v8a）。
#
# 为什么自己编：导出只做流复制，整条链路的瓶颈就是 muxer 的 I/O；MediaMuxer 在这件事
# 上的表现远差于 FFmpeg。为什么动态链接：LGPL 要求用户能用自己编译的同名库替换掉
# 我们提供的那一份，静态链接会把这条路堵死（关于页 §FFmpeg 与 LGPL v2.1 写的就是这里）。
#
# 产物：
#   android/app/src/main/jniLibs/arm64-v8a/lib{avformat,avcodec,avutil}.so
#   android/app/src/main/cpp/ffmpeg/include/**            （编译 librurumux.so 用的头）
#
# 用法：bash scripts/build-ffmpeg.sh
# 可覆盖：NDK / FFMPEG_SRC / API / JOBS
set -euo pipefail

NDK=${NDK:-/c/Users/Akari/Android/sdk/ndk/28.2.13676358}
FFMPEG_SRC=${FFMPEG_SRC:-/c/Users/Akari/Android/src/ffmpeg}
API=${API:-26}
JOBS=${JOBS:-$(nproc 2>/dev/null || echo 4)}

ABI=arm64-v8a
TRIPLE=aarch64-none-linux-android$API
case "$(uname -s)" in
    MINGW* | MSYS* | CYGWIN*) HOST_TAG=windows-x86_64 ;;
    Darwin) HOST_TAG=darwin-x86_64 ;;
    *) HOST_TAG=linux-x86_64 ;;
esac
TC=$NDK/toolchains/llvm/prebuilt/$HOST_TAG/bin
[ -x "$TC/clang.exe" ] && X=.exe || X=

ROOT=$(cd "$(dirname "$0")/.." && pwd)
PREFIX=$ROOT/build/ffmpeg/$ABI
JNILIBS=$ROOT/app/src/main/jniLibs/$ABI
INCDIR=$ROOT/app/src/main/cpp/ffmpeg/include

# MSYS 会把 --target=aarch64-... 这类参数里的 / 当路径去翻译，必须关掉
export MSYS2_ARG_CONV_EXCL='*'
export MSYS_NO_PATHCONV=1
# TMPDIR 得同时被两边认：configure 是 MSYS 的 shell，clang.exe 是原生 Windows 程序，
# 后者打不开 /tmp/xxx。C:/... 这种正斜杠混合形式两边都吃。
# （默认那个 C:\Users\...\Temp 不行：反斜杠会被 configure 的 shell 当转义吃掉。）
if command -v cygpath >/dev/null 2>&1; then
    TMPDIR=$(cygpath -m "${TMPDIR:-/tmp}")
    export TMPDIR TMP="$TMPDIR" TEMP="$TMPDIR"
    # 同理，--prefix 会被原样交给 llvm-strip.exe（make install 的最后一步），
    # 所以给 configure 的也得是 C:/... 形式。我们自己的 cp 用 POSIX 那份。
    PREFIX_ARG=$(cygpath -m "$PREFIX")
else
    PREFIX_ARG=$PREFIX
fi
export PATH="$NDK/prebuilt/$HOST_TAG/bin:$TC:$PATH"

echo "== FFmpeg $(cd "$FFMPEG_SRC" && git describe --tags --always 2>/dev/null || echo '?') → $ABI (API $API)"
cd "$FFMPEG_SRC"
[ -f ffbuild/config.mak ] && make distclean >/dev/null 2>&1 || true

# --disable-everything 之后只开流复制真正要用到的东西：
# mov/mp4 解复用 + mp4 复用 + fd 协议 + 判断码流参数要用的 parser/bsf。
# 一个 decoder / encoder 都不开——重编码不在这个应用的能力范围里，开了只是体积。
#
# --host-cc 也指向 NDK 的 clang：这台机器上没有任何宿主编译器（无 MSVC / MinGW），
# 而 configure 会硬性检查宿主 cc 的 C11 支持。这份配置里没有需要**运行**的
# hostprog（vulkan / opencl / hardcoded-tables 全关），所以宿主 cc 只要能编过就够。
./configure \
    --prefix="$PREFIX_ARG" \
    --target-os=android --arch=aarch64 --enable-cross-compile \
    --cc="$TC/clang$X" --cxx="$TC/clang++$X" \
    --ar="$TC/llvm-ar$X" --nm="$TC/llvm-nm$X" \
    --ranlib="$TC/llvm-ranlib$X" --strip="$TC/llvm-strip$X" \
    --host-cc="$TC/clang$X" --host-cflags="--target=$TRIPLE" \
    --extra-cflags="--target=$TRIPLE -O3 -fPIC -DANDROID" \
    --extra-ldflags="--target=$TRIPLE -Wl,-z,max-page-size=16384" \
    --enable-shared --disable-static --disable-symver \
    --disable-gpl --disable-nonfree \
    --disable-programs --disable-doc --disable-debug \
    --disable-avdevice --disable-avfilter --disable-swscale --disable-swresample \
    --disable-postproc --disable-network --disable-iconv --disable-vulkan \
    --disable-jni --disable-mediacodec --disable-sdl2 \
    --enable-small \
    --disable-everything \
    --enable-demuxer=mov,matroska,flv,mp3,aac,flac,ogg \
    --enable-muxer=mp4,mov,ipod \
    --enable-protocol=fd,file,pipe \
    --enable-parser=h264,hevc,av1,vp9,aac,aac_latm,opus,flac,mpeg4video \
    --enable-bsf=extract_extradata,h264_mp4toannexb,hevc_mp4toannexb,aac_adtstoasc,av1_metadata,vp9_superframe,vp9_superframe_split

make -j"$JOBS"
make install

mkdir -p "$JNILIBS" "$INCDIR"
# 只搬 .so 本体。Android 的 APK 不认带版本号后缀的库名，
# configure 的 target-os=android 分支已经把 soname 定成了不带版本号的形式。
find "$PREFIX/lib" -maxdepth 1 -name '*.so' -exec cp -f {} "$JNILIBS/" \;
rm -rf "$INCDIR"
mkdir -p "$INCDIR"
cp -rf "$PREFIX/include/." "$INCDIR/"

echo
echo "== 产物"
ls -l "$JNILIBS"
# readelf 也是原生 exe，路径同样得转一次
"$TC/llvm-readelf$X" -d "$(cygpath -m "$JNILIBS/libavformat.so" 2>/dev/null || echo "$JNILIBS/libavformat.so")" |
    grep -iE 'soname|needed' || true
