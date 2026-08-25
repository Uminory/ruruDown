# FFmpeg · LGPL v2.1

ruruDown 使用 FFmpeg 进行容器处理与流复制（`-c copy`），从不重新编码，因此导出的
画质与缓存完全一致。FFmpeg 以**动态链接**方式使用，未静态链接，且源码未做任何修改。

## APK 内的文件布局

```

lib/arm64-v8a/libavformat.so   ← FFmpeg, LGPL v2.1
lib/arm64-v8a/libavcodec.so    ← FFmpeg, LGPL v2.1
lib/arm64-v8a/libavutil.so     ← FFmpeg, LGPL v2.1
lib/arm64-v8a/librurumux.so    ← ruruDown 自己的胶水层，仅调用以上三个库的公开 API

```

## 版本

| | |
|---|---|
| 发布版本 | FFmpeg **7.1.5** |
| 分支 | `release/7.1` |
| Commit | `9a4bb2c579a16b0469759743d6917d9e8e3cb8c6` (2026-08-11) |
| 上游 | https://git.ffmpeg.org/ffmpeg.git · https://github.com/FFmpeg/FFmpeg |
| ABI | `arm64-v8a`, Android API 26, NDK r28 (28.2.13676358) |

APK 中实际打包的三份 `.so` 文件原样发布在 [`prebuilt/arm64-v8a/`](prebuilt/arm64-v8a/) 下，
校验值见同目录的 `SHA256SUMS`。

## 编译配置

完整脚本：[`build-ffmpeg.sh`](build-ffmpeg.sh)（即构建本版时使用的脚本）。关键配置如下：

```

--enable-shared --disable-static --disable-symver
--disable-gpl --disable-nonfree
--disable-programs --disable-doc --disable-debug --disable-network
--disable-avdevice --disable-avfilter --disable-swscale --disable-swresample
--disable-postproc --disable-iconv --disable-vulkan
--disable-jni --disable-mediacodec --disable-sdl2
--enable-small
--disable-everything 之后按需开启：
demuxer   mov,matroska,flv,mp3,aac,flac,ogg
muxer     mp4,mov,ipod
protocol  fd,file,pipe
parser    h264,hevc,av1,vp9,aac,aac_latm,opus,flac,mpeg4video
bsf       extract_extradata,h264_mp4toannexb,hevc_mp4toannexb,aac_adtstoasc,
av1_metadata,vp9_superframe,vp9_superframe_split
decoder / encoder   一个都没有

```

即：仅启用 LGPL v2.1 允许的组件，不含任何 GPL-only 或非自由代码
（`--disable-gpl --disable-nonfree`）。设置 → 诊断 页面会显示运行时实际加载的库版本。

## 获取源代码（LGPL §4, §6）

FFmpeg 源码未经修改，就是上面那个 commit：

```bash
git clone https://git.ffmpeg.org/ffmpeg.git ffmpeg
cd ffmpeg
git checkout 9a4bb2c579a16b0469759743d6917d9e8e3cb8c6
bash /path/to/build-ffmpeg.sh   # NDK=... FFMPEG_SRC=$PWD
```

如果该 commit 在上游无法获取，或者你需要一份打包好的源码副本，请在本仓库提交
issue，我会提供与本次发布对应的完整源码。

替换这份库（LGPL §6）

你可以用自己编译的、接口兼容的同名动态库替换 APK 中的对应文件，应用不做任何
完整性校验：

1. 解包 APK（apktool d，或直接当作 zip 解压）；
2. 替换 lib/arm64-v8a/ 下的 libavformat.so / libavcodec.so / libavutil.so；
3. 重新打包，使用自己的密钥签名并安装
   （zipalign -p 4 → apksigner sign --ks your.jks）。

注意 .so 必须保持不带版本号后缀的 soname（--target-os=android 的默认行为），
Android 动态链接器无法识别 libavformat.so.61 这类文件名。

许可全文

../licenses/LGPL-2.1.txt ·
https://www.gnu.org/licenses/old-licenses/lgpl-2.1.html ·
FFmpeg 法律声明：https://ffmpeg.org/legal.html