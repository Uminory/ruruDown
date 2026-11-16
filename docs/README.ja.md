<div align="center">

**ruruDown** — bilibili のキャッシュ済み動画を通常の mp4 / m4a / xml / srt / txt として書き出す

[![バージョン](https://img.shields.io/badge/version-1.0.4.2-blue)](../CHANGELOG.md)
[![Android](https://img.shields.io/badge/Android-8.0%2B-green)](#必要環境)
[![ライセンス](https://img.shields.io/badge/license-freeware%20(closed--source)-orange)](LICENSE.ja.md)
[![ABI](https://img.shields.io/badge/ABI-arm64--v8a-lightgrey)](#必要環境)

[English](../README.md) · [简体中文](README.zh-CN.md) · 日本語

</div>

---

ruruDown は、bilibili クライアントが**端末にすでにキャッシュ済み**の動画を、通常の
`mp4` / `m4a` / `xml` / `srt` / `txt` ファイルとして書き出します。

> **これは配布専用リポジトリです。** APK、そのドキュメント、および同梱 FFmpeg ライブラリに
> ついて LGPL が要求する素材のみを収めています。ruruDown は無料のクローズドソースです
> （[LICENSE](LICENSE.ja.md) を参照）。

## ダウンロード

APK は [Releases ページ](https://github.com/Uminory/ruruDown/releases)、または
リポジトリの [`apk/`](../apk/) から直接取得できます。

### 必要環境

| 項目    | 値                                                                         |
| ----- | ------------------------------------------------------------------------- |
| バージョン | **1.0.4.2**（versionCode 108）                                                |
| ファイル  | [`apk/ruruDown-1.0.4.2-arm64-v8a.apk`](../apk/ruruDown-1.0.4.2-arm64-v8a.apk) |
| サイズ   | 4.4 MiB                                                                   |
| ABI   | `arm64-v8a` のみ                                                            |
| 必要環境  | Android 8.0 以上                                                            |

## 前提条件：Shizuku

bilibili のキャッシュはプライベートフォルダにあり、通常の権限ではアクセスできません。
ruruDown は [Shizuku](https://shizuku.rikka.app/) を通じて読み取り専用でアクセスします。

1. Shizuku をインストールして起動します。
2. ruruDown を開き、ホーム画面で**認証**をタップし、Shizuku のダイアログで許可します。
3. ホーム画面に戻り、**再スキャン**をタップします。

スキャンとキャッシュからの書き出しには Shizuku の許可が必要です。端末にあるローカル MP4
からのクリップ書き出しは Shizuku なしでも利用できます。アクセスの問題は**設定 → 診断**で
確認できます。

## エクスポート先フォルダ

デフォルトは `Download/ruruDown` です。**設定 → エクスポート先フォルダ**から、
端末または外部ストレージの任意の共有フォルダを選べます。

## 機能

- キャッシュされた動画をスキャン：国内版・国際版 bilibili クライアントのキャッシュを
  同じライブラリに統合。スキャンパスの追加にも対応。
- ライブラリーと音声ページをタイトル・投稿者・BV ID・ファイル名で検索。
- 動画を `.mp4` として書き出し。
- 音声を `.m4a` として書き出し。
- 詳細ページの**プロパティ**を展開して、動画の実際のメディア情報を表示。
- 弾幕を `.xml` として書き出し
- キャッシュ字幕を `.srt` または本文のみの `.txt` として、動画と同名で書き出し。
- 動画と同じフォルダにある同名 `.srt` を CC ボタンで表示。
- クリップ切り出しで映像または音声のみのクリップを書き出せます。
- 内蔵プレイヤー
- 弾幕（**設定 → 弾幕設定**で調整）。

## プライバシー

本アプリにはネットワーク機能が一切ありません。**設定 → ローカルキャッシュを消去**は
メディアのスキャンキャッシュとサムネイルを削除します。書き出し記録は保持します。

## ライセンス

- **アプリ本体** — 無料のクローズドソース。商用利用および再配布は禁止。詳細は
  [LICENSE](LICENSE.ja.md) を参照。
- **サードパーティコンポーネント** — [THIRD-PARTY](THIRD-PARTY.ja.md) を参照。
- **FFmpeg** — LGPL v2.1 のもとで使用。ライセンス全文、ビルドスクリプト、置き換え手順は
  [`ffmpeg/README.ja.md`](../ffmpeg/README.ja.md) および
  [`licenses/LGPL-2.1.txt`](../licenses/LGPL-2.1.txt) にあります。

書き出したコンテンツの著作権は原作者に帰属します。個人的なオフライン視聴にのみ使用し、
再配布は行わないでください。

## 免責事項

本ソフトウェアは「現状のまま」提供され、いかなる種類の明示的または黙示的保証も
伴いません。bilibili および Shizuku プロジェクト（RikkaApps）とは一切関係ありません。