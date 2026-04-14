# 学習進捗トラッカー（tracker）

Spring Boot + Thymeleaf + JDBC(H2) で作った、**科目**と**タスク**の学習進捗を管理するシンプルなWebアプリです。

---

## 目次

1. [機能](#機能)
2. [技術スタック](#技術スタック)
3. [プロジェクト構成](#プロジェクト構成)
4. [前提条件](#前提条件)
5. [起動方法](#起動方法)
6. [画面・エンドポイント](#画面エンドポイント)
7. [データベース](#データベース)
8. [ドキュメント](#ドキュメント)
9. [テスト](#テスト)

---

## 機能

- 科目一覧表示（進捗％・完了/総タスク数）
- 科目の追加 / 削除
- 科目詳細（タスク一覧）
- タスクの追加 / 完了・未完了切り替え / 削除
- トップページでの期限切れのタスク警告表示
- タスクのステータス管理(未着手・進行中・完了) および絞り込み
- タスク一覧の並び替え(登録順・期限順)
- タスク振り返り(コメント欄)の記録
- ユーザー認証(ログイン・ログアウト)
---

## 技術スタック

| 技術 | バージョン / 説明 |
|---|---|
| Java | **25**（`pom.xml` の `java.version`） |
| Spring Boot | 3.5.9 |
| Spring Web | HTTPリクエストを処理するWebフレームワーク |
| Thymeleaf | HTMLテンプレートエンジン（サーバー側で画面を生成） |
| Spring JDBC | `JdbcTemplate` でSQLを実行するデータアクセス |
| H2 Database | インメモリ（メモリ上で動く軽量DB。再起動でデータリセット） |
| Maven | ビルド・依存管理ツール（Wrapper同梱: `./mvnw`） |
| Spring Security | ユーザー認証・認可 |

---

## プロジェクト構成

> Spring Bootのプロジェクトは「役割ごとにフォルダを分ける」のが基本です。  
> 以下のように、**モデル（データの形）→ リポジトリ（DBアクセス）→ コントローラー（画面の制御）** という3層構造になっています。

```
java-tracker/
├── pom.xml                          ... Maven設定（依存ライブラリやJavaバージョンの指定）
├── mvnw / mvnw.cmd                  ... Maven Wrapper（Mavenをインストールしなくても使える）
├── README.md                        ... このファイル
│
├── docs/                            ... ドキュメント
│   ├── er-diagram.md                ... ER図（データベース設計図）
│   └── javadoc/                     ... Javadoc API リファレンス（HTML）
│       └── index.html               ... Javadocトップページ
│
└── src/
    ├── main/
    │   ├── java/com/example/tracker/
    │   │   ├── TrackerApplication.java          ... ★ アプリのエントリーポイント（起動クラス）
    │   │   │
    │   │   ├── model/                           ... 📦 モデル層（データの「形」を定義）
    │   │   │   ├── Subject.java                 ...   科目を表すクラス（id, name）
    │   │   │   ├── Task.java                    ...   タスクを表すクラス（id, subjectId, title, completed）
    │   │   │   └── SubjectSummary.java          ...   科目＋進捗統計をまとめたクラス
    │   │   │
    │   │   ├── repository/                      ... 🗄️ リポジトリ層（DBとのやり取り）
    │   │   │   ├── SubjectRepository.java       ...   科目リポジトリ（インターフェース＝メソッドの定義）
    │   │   │   ├── SubjectRepositoryImpl.java   ...   科目リポジトリ（実装＝実際のSQL処理）
    │   │   │   ├── TaskRepository.java          ...   タスクリポジトリ（インターフェース）
    │   │   │   └── TaskRepositoryImpl.java      ...   タスクリポジトリ（実装）
    │   │   │
    │   │   └── controller/                      ... 🎮 コントローラー層（画面からのリクエストを処理）
    │   │       └── TrackerController.java       ...   全エンドポイントを定義
    │   │
    │   └── resources/
    │       ├── application.properties           ... アプリ設定（DB接続先、ポート番号など）
    │       ├── schema.sql                       ... テーブル定義SQL（起動時に自動実行）
    │       ├── data.sql                         ... サンプルデータ投入SQL（起動時に自動実行）
    │       └── templates/                       ... 🖥️ HTMLテンプレート（Thymeleaf）
    │           ├── index.html                   ...   科目一覧ページ
    │           └── subject_details.html         ...   科目詳細（タスク一覧）ページ
    │
    └── test/                                    ... テストコード
        └── java/com/example/...
```

### 3層アーキテクチャの流れ

```
[ブラウザ] ⇄ [Controller] ⇄ [Repository] ⇄ [H2 Database]
                  ↕                ↕
              [Thymeleaf]      [Model]
              (HTML生成)      (データの形)
```

1. **ブラウザ** からHTTPリクエスト（例: `GET /`）が送られる
2. **Controller** がリクエストを受け取り、Repositoryにデータを要求する
3. **Repository** がJdbcTemplateでSQLを実行し、Modelオブジェクトとしてデータを返す
4. **Controller** がModelをThymeleafテンプレートに渡し、HTMLを生成して返す

---

## 前提条件

- **Java 25** 以上がインストールされていること
  - 確認コマンド: `java -version`
- Maven のインストールは不要（Wrapperが同梱されています）

---

## 起動方法

```bash
# Windows の場合
.\mvnw.cmd spring-boot:run

# Mac / Linux の場合
./mvnw spring-boot:run
```

起動後、ブラウザで以下にアクセスします:

- **アプリ**: [http://localhost:8080/](http://localhost:8080/)

> **補足**: H2はインメモリDBのため、アプリを停止するとデータはリセットされます。  
> 起動するたびに `data.sql` のサンプルデータが投入されます。

---

## 画面・エンドポイント

### 科目一覧（トップページ）

| 操作 | HTTP | パス | パラメータ |
|---|---|---|---|
| 一覧表示・期限切れの警告表示 | `GET` | `/` | なし |
| 科目追加 | `POST` | `/subjects` | `name`（科目名） |
| 科目削除 | `POST` | `/subjects/{id}/delete` | なし |

### 科目詳細（タスク一覧）

| 操作 | HTTP | パス | パラメータ |
|---|---|---|---|
| 詳細表示 (絞り込み・ソート) | `GET` | `/subjects/{id}` | `statusFilter`, `sortOrder` |
| タスク追加 | `POST` | `/subjects/{subjectId}/tasks` | `title`（タスク名）, `status` (ステータス), `deadline`(期日), `reflection`(振り返り欄)|
| タスク完了切替 | `POST` | `/tasks/{taskId}/complete` | `subjectId`, `completed` |
| 振り返りの保存 | `POST` | `/tasks/{taskId}/reflection` | `subjectId`, `reflection` |
| タスク削除 | `POST` | `/tasks/{taskId}/delete` | `subjectId` |

### ユーザー認証

| 操作 | HTTP | パス | パラメータ |
|---|---|---|---|
| ログイン画面表示 | `GET` | `/login` | なし |
| ログイン処理 |　`POST` | `/login` | `username`, `password` |
| ログアウト　| `POST` | `/login` | なし |s

---

## データベース

H2（インメモリ）を使用し、起動時に以下のファイルで自動初期化されます。

| ファイル | 役割 |
|---|---|
| `src/main/resources/schema.sql` | テーブル定義（`SUBJECT`, `TASK`） |
| `src/main/resources/data.sql` | サンプルデータの投入 |

### ER図

科目（SUBJECT）とタスク（TASK）の関係は以下のとおりです。  
詳細は [`docs/er-diagram.md`](docs/er-diagram.md) を参照してください。

```
SUBJECT (1) ──── (N) TASK
  科目              タスク
```

### H2 Console（DBの中身をブラウザから確認）

| 項目 | 値 |
|---|---|
| URL | [http://localhost:8080/h2-console](http://localhost:8080/h2-console) |
| JDBC URL | `jdbc:h2:mem:testdb` |
| User Name | `sa` |
| Password | （空欄のまま） |

> **H2 Console とは？**: アプリが使っているデータベースの中身を、ブラウザ上で直接SQLを打って確認できるツールです。

設定は `src/main/resources/application.properties` を参照してください。

---

## ドキュメント

| ドキュメント | 場所 | 説明 |
|---|---|---|
| ER図 | [`docs/er-diagram.md`](docs/er-diagram.md) | テーブル設計をMermaid記法で図示 |
| Javadoc | [`docs/javadoc/index.html`](docs/javadoc/index.html) | 全クラス・メソッドのAPIリファレンス（HTML） |

### Javadocの再生成

ソースコードを変更した場合、以下のコマンドでJavadocを更新できます:

```bash
# Windows
.\mvnw.cmd javadoc:javadoc

# Mac / Linux
./mvnw javadoc:javadoc
```

---

## テスト

```bash
# Windows
.\mvnw.cmd test

# Mac / Linux
./mvnw test
```
