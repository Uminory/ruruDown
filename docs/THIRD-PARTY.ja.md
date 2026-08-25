# サードパーティコンポーネント

ruruDown 1.0.2（arm64-v8a）には、以下のサードパーティコンポーネントが同梱されて
います。FFmpeg を除き、すべて Apache License 2.0 または MIT ライセンスの下で提供
されています。どちらのライセンスも、著作権表示とライセンス通知を保持する限り、
クローズドソースでの配布を許可します。本ファイルがその通知となります。

## FFmpeg · LGPL v2.1 — 動的リンク

| | |
|---|---|
| バージョン | 7.1.5 (`release/7.1`, commit `9a4bb2c579a16b0469759743d6917d9e8e3cb8c6`) |
| コンポーネント | `libavformat.so`, `libavcodec.so`, `libavutil.so` |
| 用途 | コンテナ処理とストリームコピー（`-c copy`）、再エンコードなし |
| ライセンス | LGPL v2.1 — [`licenses/LGPL-2.1.txt`](../licenses/LGPL-2.1.txt) |
| ソースと置き換え手順 | [`ffmpeg/README.ja.md`](../ffmpeg/README.ja.md) |

著作権は FFmpeg プロジェクトの貢献者に帰属します。ソースは未改変であり、ライブラリは
LGPL §6 に従って置き換え可能です。上記リンクのドキュメントを参照してください。

## Apache License 2.0

全文：https://www.apache.org/licenses/LICENSE-2.0

| コンポーネント | バージョン | 用途 | 著作権 |
|---|---|---|---|
| Kotlin stdlib | 2.2.10 | 言語ランタイム | JetBrains s.r.o. and contributors |
| kotlinx-coroutines-android | 1.10.2 | 並行処理 | JetBrains s.r.o. and contributors |
| androidx.core:core-ktx | 1.19.0 | プラットフォーム互換性 | The Android Open Source Project |
| androidx.activity:activity-compose | 1.13.0 | Activity 統合 | The Android Open Source Project |
| androidx.lifecycle (runtime-ktx, viewmodel-compose) | 2.11.0 | ライフサイクルと ViewModel | The Android Open Source Project |
| androidx.documentfile | 1.1.0 | エクスポート先フォルダへの SAF アクセス | The Android Open Source Project |
| Jetpack Compose (ui, ui-graphics, foundation, material3, tooling-preview) | BOM 2026.03.01 | UI ツールキット | The Android Open Source Project |
| androidx.graphics:path (`libandroidx.graphics.path.so`) | 推移的依存関係 | Compose のパス補間 | The Android Open Source Project |
| AndroidX Media3 (exoplayer, common) | 1.11.0 | 内蔵プレイヤー | The Android Open Source Project |
| Coil 3 (coil-compose) | 3.4.0 | サムネイルのデコードとキャッシュ | Coil Contributors |
| Haze | 1.7.2 | ボトムバーとパネルのリアルタイムブラー | Chris Banes |

## MIT License

| コンポーネント | バージョン | 用途 | 著作権 |
|---|---|---|---|
| Shizuku API (`dev.rikka.shizuku:api`) | 13.1.5 | root 不要の特権的読み取りチャネル | © 2021 RikkaW |
| Shizuku Provider (`dev.rikka.shizuku:provider`) | 13.1.5 | 同上 | © 2021 RikkaW |

```
Permission is hereby granted, free of charge, to any person obtaining a copy of this
software and associated documentation files (the "Software"), to deal in the Software
without restriction, including without limitation the rights to use, copy, modify,
merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to the following
conditions:

The above copyright notice and this permission notice shall be included in all copies
or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
```

Shizuku は RikkaApps が開発したものであり、ruruDown は Shizuku プロジェクトとは
一切関係ありません。

## アプリケーション自体

`librurumux.so`（FFmpeg の公開 API を呼び出すグルーレイヤー）およびすべての Kotlin
コードは ruruDown に帰属し、[LICENSE](LICENSE.ja.md) の下でライセンスされ、
ソースコードは公開されていません。
