<div align="center">

**ruruDown** — bilibili のキャッシュ済み動画を通常の mp4 / m4a / xml として書き出す

[![バージョン](https://img.shields.io/badge/version-1.0.2-blue)](../CHANGELOG.md)
[![Android](https://img.shields.io/badge/Android-8.0%2B-green)](#必要環境)
[![ライセンス](https://img.shields.io/badge/license-freeware%20(closed--source)-orange)](LICENSE.ja.md)
[![ABI](https://img.shields.io/badge/ABI-arm64--v8a-lightgrey)](#必要環境)

[English](../README.md) · [简体中文](README.zh-CN.md) · 日本語

</div>

---

ruruDown は、bilibili クライアントが**端末にすでにキャッシュ済み**の動画を、通常の
`mp4` / `m4a` / `xml` ファイルとして書き出します。ダウンロードもアップロードも行わず、
キャッシュフォルダへ書き込むこともありません。一度読み取ってストリームコピーするだけなので、
出力はキャッシュされた内容とバイト単位で同一です。

> **これは配布専用リポジトリです。** APK、そのドキュメント、および同梱 FFmpeg ライブラリに
> ついて LGPL が要求する素材のみを収めています。アプリケーション自体のソースコードは
> 公開していません（無料のクローズドソース、[LICENSE](LICENSE.ja.md) を参照）。

## ダウンロード

APK は [Releases ページ](https://github.com/Uminory/ruruDown/releases)、または
リポジトリの [`apk/`](../apk/) から直接取得できます。

### 必要環境

| 項目 | 値 |
|---|---|
| バージョン | **1.0.2**（versionCode 102） |
| ファイル | [`apk/ruruDown-1.0.2-arm64-v8a.apk`](../apk/ruruDown-1.0.2-arm64-v8a.apk) |
| サイズ | 12.8 MiB（13,420,493 バイト） |
| ABI | `arm64-v8a` のみ |
| 必要環境 | Android 8.0（API 26）以上 |
| SHA-256 | `703e28afa55e38c2271e7924a7ae161ae7ba0612e537aea29465a26369281987` |

### 署名

APK Signature Scheme **v2 + v3**、4096-bit RSA。署名証明書のフィンガープリント
（**証明書**の SHA-256。APK ファイルの SHA-256 ではありません）：

```
f6fd5f1b610fd8b7c45740955fe6d93ef54e6739a7e9841a2659bbcaae7cb7fe
```

アップグレード時に署名の不一致が表示された場合、その APK はここで公開されたものでは
ありません。

### ダウンロードの検証

```bash
cd apk && sha256sum -c ruruDown-1.0.2-arm64-v8a.apk.sha256
```

## 前提条件：Shizuku

bilibili のキャッシュは別アプリのプライベートディレクトリにあり、通常の権限では
アクセスできません。ruruDown は [Shizuku](https://shizuku.rikka.app/) を通じて
読み取り専用でアクセスします。データは Binder 経由で転送されず、リモートファイル
ディスクリプタ経由で読み取られます。

1. Shizuku をインストールして起動します（ADB または root 不要の方法）。
2. ruruDown を開き、ホーム画面で**認証**をタップし、Shizuku のダイアログで許可します。
3. ホーム画面に戻り、**再スキャン**をタップします。

Shizuku がない場合でもアプリは起動しますが、キャッシュは検出されません。
**設定 → 診断**で、どの段階で失敗しているかを確認できます。

## エクスポート先フォルダ

デフォルトは `Download/ruruDown` です。変更するには**設定 → エクスポート先フォルダ**で
システムのディレクトリ選択ツールを使います。`Download` 以外のフォルダを選ぶ場合、
システム設定で「すべてのファイルへのアクセス」権限が別途必要になることがあります。

## 機能

- キャッシュされた動画をスキャンし、長さ・サイズ・日時で並べ替え。カバー画像、投稿者、
  画質、パート情報を表示。
- 動画を `.mp4` として書き出し（音声・映像ストリームコピー、再エンコードなし、画質は
  キャッシュと完全一致）。
- 音声を `.m4a` として書き出し。
- 弾幕を `.xml` として書き出し（bilibili XML 形式、プレイヤーで直接読み込み可能）。
- バッチエクスポートキュー。フォアグラウンドサービス＋通知で進捗表示し、画面を離れても
  継続します。
- 内蔵プレイヤー：動画は横向き全画面、音声はミニプレイヤーバー。
- 音声ページで長押しして複数選択し、一括削除。
- UI 言語：English / 日本語 / 简体中文 / 繁體中文。システム設定に従うか手動で指定可能。

## プライバシー

本アプリにはネットワーク関連のコードが一切含まれていません（FFmpeg 自体も
`--disable-network` でビルドされています）。アカウント、解析、クラッシュレポートは
ありません。すべてのデータは端末内に留まり、インデックスデータベースとサムネイルは
**設定 → ローカルキャッシュを消去**でいつでも削除できます。

## ライセンス

- **アプリ本体** — 無料のクローズドソース。商用利用および再配布は禁止。詳細は
  [LICENSE](LICENSE.ja.md) を参照。
- **サードパーティコンポーネント** — [THIRD-PARTY](THIRD-PARTY.ja.md) を参照。
- **FFmpeg** — LGPL v2.1 の動的リンクとして使用。ライセンス全文、ビルドスクリプト、
  コンパイル設定、置き換え手順は [`ffmpeg/README.ja.md`](../ffmpeg/README.ja.md) および
  [`licenses/LGPL-2.1.txt`](../licenses/LGPL-2.1.txt) にあります。

書き出したコンテンツの著作権は原作者に帰属します。個人的なオフライン視聴にのみ使用し、
再配布は行わないでください。

## リポジトリ構成

```
├─ README.md            英語ドキュメント（正本）
├─ LICENSE.md           ruruDown 自体のライセンス
├─ THIRD-PARTY.md       同梱サードパーティコンポーネントとそのライセンス
├─ CHANGELOG.md         リリース履歴
├─ apk/                 公開 APK と .sha256 チェックサム
├─ docs/                上記 4 文書の翻訳
├─ licenses/            ライセンス全文（Apache-2.0、LGPL-2.1、MIT）
└─ ffmpeg/              LGPL 素材：ビルドスクリプト、設定、プリビルド .so
```

## 免責事項

本ソフトウェアは「現状のまま」提供され、いかなる種類の明示的または黙示的保証も
伴いません。bilibili および Shizuku プロジェクト（RikkaApps）とは一切関係ありません。
