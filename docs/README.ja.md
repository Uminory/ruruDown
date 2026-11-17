<div align="center">

**ruruDown** — bilibili のキャッシュ済み動画を通常の mp4 / m4a / xml / srt / txt として書き出す

[![バージョン](https://img.shields.io/badge/version-1.0.4.3-blue)](../CHANGELOG.md)
[![Android](https://img.shields.io/badge/Android-8.0%2B-green)](#必要環境)
[![ライセンス](https://img.shields.io/badge/license-freeware%20(closed--source)-orange)](LICENSE.ja.md)
[![ABI](https://img.shields.io/badge/ABI-arm64--v8a-lightgrey)](#必要環境)

[English](../README.md) · [简体中文](README.zh-CN.md) · 日本語

</div>

---

ruruDown は、bilibili Android クライアントで**端末にキャッシュ済み**の動画を、
通常の `mp4` / `m4a` / `xml` / `srt` / `txt` ファイルとして書き出します。

> **これは配布用リポジトリです。** APK、ドキュメント、同梱 FFmpeg ライブラリーの LGPL
> 関連資料を収めています。ruruDown は無料のクローズドソースアプリです。
> [LICENSE](LICENSE.ja.md) を参照してください。

## ダウンロード

[Releases ページ](https://github.com/Uminory/ruruDown/releases)、または
リポジトリの [`apk/`](../apk/) から APK を取得できます。

### 必要環境

| 項目 | 値 |
| --- | --- |
| バージョン | **1.0.4.3**（versionCode 109） |
| ファイル | [`apk/ruruDown-1.0.4.3-arm64-v8a.apk`](../apk/ruruDown-1.0.4.3-arm64-v8a.apk) |
| サイズ | 4.4 MiB |
| ABI | `arm64-v8a` |
| 必要環境 | Android 8.0 以上 |

## 前提条件：Shizuku

スキャンとキャッシュからの書き出しには [Shizuku](https://shizuku.rikka.app/) の許可が必要です。

1. Shizuku をインストールして起動します。
2. ruruDown を開き、ホーム画面で**認証**をタップし、Shizuku のダイアログで許可します。
3. ホーム画面に戻り、**再スキャン**をタップします。

端末内の MP4 からクリップを直接書き出せます。アクセスの問題は**設定 → 診断**で確認できます。

## エクスポート先フォルダー

既定の保存先は `Download/ruruDown` です。**設定 → エクスポート先フォルダー**から、
端末または外部ストレージの共有フォルダーを選べます。

## 機能

- bilibili の国内版・国際版クライアントのキャッシュ動画をスキャンし、同じライブラリーにまとめます。スキャンパスの追加にも対応。
- 番組や複数パートの動画をコレクションとして閲覧。
- キャッシュ、出力フォルダー、端末ストレージの使用量を確認。
- ライブラリーと音声ページで、タイトル・投稿者・BV ID・ファイル名から検索。
- 動画を `.mp4` として書き出し。
- 音声を `.m4a` として書き出し。
- 詳細画面の**プロパティ**を展開して、動画の実際のメディア情報を確認。
- 弾幕を `.xml` として書き出し。
- キャッシュ字幕を `.srt` または本文のみの `.txt` として動画と同名で書き出し。
- 動画と同じフォルダーにある同名 `.srt` 字幕を CC ボタンで表示。
- 動画または音声のみのクリップを書き出し。
- 内蔵動画プレーヤー。
- 弾幕を表示し、**設定 → 弾幕設定**でスタイルを調整。

## プライバシー

メディアは端末内で処理します。**設定 → ローカルキャッシュを消去**でスキャンキャッシュと
サムネイルを削除し、書き出し記録を保持します。

## ライセンス

- **アプリ本体** — 無料のクローズドソース。商用利用および再配布は禁止。
  詳細は [LICENSE](LICENSE.ja.md) を参照。
- **サードパーティコンポーネント** — [THIRD-PARTY](THIRD-PARTY.ja.md) を参照。
- **FFmpeg** — LGPL v2.1 に基づき使用。ライセンス全文、ビルドスクリプト、置き換え手順は
  [`ffmpeg/README.ja.md`](../ffmpeg/README.ja.md) および
  [`licenses/LGPL-2.1.txt`](../licenses/LGPL-2.1.txt) にあります。

書き出したコンテンツの著作権は原作者に帰属します。個人的なオフライン視聴に使用し、
原作者の許諾条件に従ってください。

## 免責事項

本ソフトウェアは「現状のまま」提供され、いかなる保証も伴いません。
bilibili および Shizuku プロジェクト（RikkaApps）とは関係ありません。
