# 学習進捗トラッカー（tracker）

Spring Boot + Thymeleaf + JDBC(H2) で作った、**科目**と**タスク**の学習進捗を管理するシンプルなWebアプリです。

## 機能

- 科目一覧表示（進捗％・完了/総タスク数）
- 科目の追加 / 削除
- 科目詳細（タスク一覧）
- タスクの追加 / 完了・未完了切り替え / 削除

## 技術スタック

- Java: **25**（`pom.xml` の `java.version`）
- Spring Boot: 3.5.9
- Spring Web / Thymeleaf
- Spring JDBC（`JdbcTemplate`）
- H2 Database（インメモリ）
- Maven（Wrapper同梱: `./mvnw`）

## 起動方法

```bash
./mvnw spring-boot:run
```

起動後、ブラウザで以下にアクセスします:

- アプリ: http://localhost:8080/

## 画面/エンドポイント

- 科目一覧
  - `GET /`
  - 科目追加: `POST /subjects` (`name`)
  - 科目削除: `POST /subjects/{id}/delete`
- 科目詳細（タスク一覧）
  - `GET /subjects/{id}`
  - タスク追加: `POST /subjects/{subjectId}/tasks` (`title`)
  - タスク完了切替: `POST /tasks/{taskId}/complete` (`subjectId`, `completed`)
  - タスク削除: `POST /tasks/{taskId}/delete` (`subjectId`)

## データベース

H2（インメモリ）を使用し、起動時に以下で初期化します。

- スキーマ: `src/main/resources/schema.sql`
- サンプルデータ: `src/main/resources/data.sql`

### H2 Console

- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- User Name: `sa`
- Password: （空）

設定は `src/main/resources/application.properties` を参照してください。

## テスト

```bash
./mvnw test
```
