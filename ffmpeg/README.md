# FFmpeg · LGPL v2.1

ruruDown uses FFmpeg for container handling and stream copy (`-c copy`) — never
re-encoding, so the exported quality is bit-identical to the cache. FFmpeg is used
via **dynamic linking**; it is not statically linked, and its source is not modified.

## Layout inside the APK

```

lib/arm64-v8a/libavformat.so   ← FFmpeg, LGPL v2.1
lib/arm64-v8a/libavcodec.so    ← FFmpeg, LGPL v2.1
lib/arm64-v8a/libavutil.so     ← FFmpeg, LGPL v2.1
lib/arm64-v8a/librurumux.so    ← ruruDown's own glue layer, which only calls the public
APIs of the three libraries above

```

## Version

| | |
|---|---|
| Release | FFmpeg **7.1.5** |
| Branch | `release/7.1` |
| Commit | `9a4bb2c579a16b0469759743d6917d9e8e3cb8c6` (2026-08-11) |
| Upstream | https://git.ffmpeg.org/ffmpeg.git · https://github.com/FFmpeg/FFmpeg |
| ABI | `arm64-v8a`, Android API 26, NDK r28 (28.2.13676358) |

The three `.so` files shipped in the APK are published here verbatim under
[`prebuilt/arm64-v8a/`](prebuilt/arm64-v8a/); checksums are in `SHA256SUMS` beside them.

## Build Configuration

Full script: [`build-ffmpeg.sh`](build-ffmpeg.sh) (the exact script used for this build).
The essentials:

```

--enable-shared --disable-static --disable-symver
--disable-gpl --disable-nonfree
--disable-programs --disable-doc --disable-debug --disable-network
--disable-avdevice --disable-avfilter --disable-swscale --disable-swresample
--disable-postproc --disable-iconv --disable-vulkan
--disable-jni --disable-mediacodec --disable-sdl2
--enable-small
--disable-everything, then re-enabled selectively:
demuxer   mov,matroska,flv,mp3,aac,flac,ogg
muxer     mp4,mov,ipod
protocol  fd,file,pipe
parser    h264,hevc,av1,vp9,aac,aac_latm,opus,flac,mpeg4video
bsf       extract_extradata,h264_mp4toannexb,hevc_mp4toannexb,aac_adtstoasc,
av1_metadata,vp9_superframe,vp9_superframe_split
decoder / encoder   none at all

```

That is: only components permitted under LGPL v2.1, with no GPL-only or non-free code
(`--disable-gpl --disable-nonfree`). Settings → Diagnostics shows the library version
actually loaded at runtime.

## Obtaining the Source (LGPL §4, §6)

The FFmpeg source is unmodified and is exactly the commit above:

```bash
git clone https://git.ffmpeg.org/ffmpeg.git ffmpeg
cd ffmpeg
git checkout 9a4bb2c579a16b0469759743d6917d9e8e3cb8c6
bash /path/to/build-ffmpeg.sh   # NDK=... FFMPEG_SRC=$PWD
```

If that commit ever becomes unavailable upstream, or you would rather have a packaged
copy of the exact source, open an issue in this repository and a complete source
archive corresponding to this release will be provided.

Replacing the Library (LGPL §6)

You may replace the shipped libraries with your own interface-compatible builds of the
same soname; the app performs no integrity checking:

1. Unpack the APK (apktool d, or just unzip it);
2. Replace the corresponding files under lib/arm64-v8a/:
   libavformat.so / libavcodec.so / libavutil.so;
3. Repack, sign with your own key, and install
   (zipalign -p 4 → apksigner sign --ks your.jks).

Keep the unversioned soname (which --target-os=android produces by default);
Android's loader will not pick up names like libavformat.so.61.

Full License Text

[LGPL-2.1](../licenses/LGPL-2.1.txt) ·
https://www.gnu.org/licenses/old-licenses/lgpl-2.1.html ·

FFmpeg legal notice: https://ffmpeg.org/legal.html
