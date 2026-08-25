# FFmpeg · LGPL v2.1

ruruDown は FFmpeg をコンテナ処理とストリームコピー（`-c copy`）に使用しており、
再エンコードは一切行いません。そのため、エクスポートされた画質はキャッシュと
完全に一致します。FFmpeg は**動的リンク**で使用されており、静的リンクはされて
おらず、ソースコードも改変されていません。

## APK 内のレイアウト

```

lib/arm64-v8a/libavformat.so   ← FFmpeg, LGPL v2.1
lib/arm64-v8a/libavcodec.so    ← FFmpeg, LGPL v2.1
lib/arm64-v8a/libavutil.so     ← FFmpeg, LGPL v2.1
lib/arm64-v8a/librurumux.so    ← ruruDown 独自のグルーレイヤー。上記 3 つの公開 API のみを
呼び出します

```

## バージョン

| | |
|---|---|
| リリース | FFmpeg **7.1.5** |
| ブランチ | `release/7.1` |
| コミット | `9a4bb2c579a16b0469759743d6917d9e8e3cb8c6` (2026-08-11) |
| アップストリーム | https://git.ffmpeg.org/ffmpeg.git · https://github.com/FFmpeg/FFmpeg |
| ABI | `arm64-v8a`, Android API 26, NDK r28 (28.2.13676358) |

APK に同梱されている 3 つの `.so` ファイルは、[`prebuilt/arm64-v8a/`](prebuilt/arm64-v8a/) に
そのまま公開されています。チェックサムは同じディレクトリの `SHA256SUMS` にあります。

## ビルド設定

完全なスクリプト：[`build-ffmpeg.sh`](build-ffmpeg.sh)（このビルドで使用したものと同一）。
主要な設定は以下の通りです：

```

--enable-shared --disable-static --disable-symver
--disable-gpl --disable-nonfree
--disable-programs --disable-doc --disable-debug --disable-network
--disable-avdevice --disable-avfilter --disable-swscale --disable-swresample
--disable-postproc --disable-iconv --disable-vulkan
--disable-jni --disable-mediacodec --disable-sdl2
--enable-small
--disable-everything の後、選択的に再有効化：
demuxer   mov,matroska,flv,mp3,aac,flac,ogg
muxer     mp4,mov,ipod
protocol  fd,file,pipe
parser    h264,hevc,av1,vp9,aac,aac_latm,opus,flac,mpeg4video
bsf       extract_extradata,h264_mp4toannexb,hevc_mp4toannexb,aac_adtstoasc,
av1_metadata,vp9_superframe,vp9_superframe_split
decoder / encoder   なし

```

つまり、LGPL v2.1 で許可されるコンポーネントのみを有効にしており、GPL のみの
コードや非フリーなコードは一切含まれていません（`--disable-gpl --disable-nonfree`）。
設定 → 診断 画面で、実行時に実際に読み込まれているライブラリのバージョンを
確認できます。

## ソースコードの入手（LGPL §4, §6）

FFmpeg のソースコードは未改変であり、上記のコミットそのものです：

```bash
git clone https://git.ffmpeg.org/ffmpeg.git ffmpeg
cd ffmpeg
git checkout 9a4bb2c579a16b0469759743d6917d9e8e3cb8c6
bash /path/to/build-ffmpeg.sh   # NDK=... FFMPEG_SRC=$PWD
```

もし将来的にこのコミットがアップストリームで取得できなくなった場合、または
正確なソースのパッケージ版が必要な場合は、本リポジトリの issue を開いて
いただければ、このリリースに対応する完全なソースアーカイブを提供します。

ライブラリの置き換え（LGPL §6）

同梱されているライブラリを、同じ soname を持つ独自ビルドの互換ライブラリに
置き換えることができます。アプリは整合性チェックを一切行いません：

1. APK を展開します（apktool d、または単に zip として解凍）。
2. lib/arm64-v8a/ の下にある libavformat.so / libavcodec.so / libavutil.so
   を置き換えます。
3. 再パッケージし、自分の鍵で署名してインストールします
   （zipalign -p 4 → apksigner sign --ks your.jks）。

soname はバージョン番号なしのままにしてください（--target-os=android のデフォルト
動作）。Android のダイナミックリンカは libavformat.so.61 のような名前を認識しません。

ライセンス全文

[LGPL-2.1](../licenses/LGPL-2.1.txt) ·
https://www.gnu.org/licenses/old-licenses/lgpl-2.1.html ·

FFmpeg 法的通知：https://ffmpeg.org/legal.html
