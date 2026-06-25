 # admin-dashboard 実装手順書：学習状況一覧ダッシュボード（管理者向け）

管理者が全ユーザーの学習進捗（完了タスク数 / 未完了タスク数 / 最終ログイン日時）を一覧で確認できるダッシュボードを作成するための手順書。

## 0. ゴールと現状の差分整理

**作るもの**：管理者が `/admin` で、全ユーザーの「完了タスク数 / 未完了タスク数 / 最終ログイン日時」を一覧表で確認できる画面。

### 現状でできていること
- `/admin/**` は `hasRole("ADMIN")` で保護済み（`SecurityConfig.java`）
- `AdminController#adminTop` が `userRepository.findAll()` で全ユーザーを一覧表示（`AdminController.java`）
- 科目単位の完了/未完了集計ロジックは `TaskStats` / `getTaskStatsForSubject` に存在

### 足りないもの（このGoalで追加するもの）

| 項目 | 現状 | 対応 |
|---|---|---|
| ユーザー単位の完了/未完了タスク数 | 科目単位の集計しかない | 集計SQLを新規追加 |
| 最終ログイン日時 | **そもそもDBに列が無い** | `USERS` に列追加＋ログイン時に記録 |
| 表示用の入れ物（DTO） | 無い | `UserProgress` クラスを新規作成 |

> ⚠️ 最重要ポイント：**「最終ログイン日時」は現状どこにも保存されていません。** DBへの列追加とログイン時の記録処理が必須です。ここが Goal 6 で一番作業量が多い部分です。

---

## ステップ1：DBスキーマに「最終ログイン日時」列を追加

**ファイル**：`src/main/resources/schema.sql`

`USERS` テーブルに列を1つ追加します。

```sql
CREATE TABLE USERS (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    role VARCHAR(20) NOT NULL DEFAULT 'GENERAL',
    last_login_at TIMESTAMP          -- ★追加（初期はNULL = 未ログイン）
);
```

**確認ポイント**：このプロジェクトは起動のたびに `DROP TABLE` → `CREATE` で作り直す構成（schema.sql 冒頭）なので、マイグレーションは不要。サーバー再起動すれば反映されます。

（任意）`src/main/resources/data.sql` のサンプルユーザーに初期値を入れておくと、画面表示の確認がしやすいです。`INSERT` 文に `last_login_at` を足すか、空欄のまま「未ログイン」表示の確認に使ってもOK。

---

## ステップ2：表示用データの入れ物（DTO）を作る

**新規ファイル**：`src/main/java/com/example/tracker/model/UserProgress.java`

`User` をそのまま画面に渡すと集計値が乗りません。一覧1行分を表す専用クラスを作ります（`TaskStats` と同じく、不変オブジェクト＋getterのみのパターンを踏襲）。

保持するフィールド（案）：
- `String username`
- `long completedCount` … 完了タスク数
- `long incompleteCount` … 未完了タスク数
- `LocalDateTime lastLoginAt` … 最終ログイン日時（NULL許容）

> 既存の `TaskStats` の書き方（コンストラクタで全部受け取り、getterのみ）をそのまま真似すると統一感が出ます。

---

## ステップ3：集計クエリ（Repository）を追加

**ファイル**：`src/main/java/com/example/tracker/repository/UserRepository.java` と `UserRepositoryImpl.java`

インターフェースにメソッドを1つ追加：

```java
/** 全ユーザーの学習進捗（完了/未完了タスク数・最終ログイン）を取得する。 */
List<UserProgress> findAllUserProgress();
```

実装側のSQL（案）。`USERS → SUBJECT → TASK` を **LEFT JOIN** でつなぎ、ユーザー単位で集計します。

```sql
SELECT u.username,
       COUNT(CASE WHEN t.status = 'DONE' THEN 1 END)  AS completed_count,
       COUNT(CASE WHEN t.status <> 'DONE' THEN 1 END) AS incomplete_count,
       u.last_login_at
FROM USERS u
LEFT JOIN SUBJECT s ON s.user_id = u.id
LEFT JOIN TASK    t ON t.subject_id = s.id
GROUP BY u.id, u.username, u.last_login_at
ORDER BY u.id
```

**なぜ LEFT JOIN か**：INNER JOIN にすると、科目やタスクを1件も持たないユーザーが一覧から消えてしまいます。「まだ何もしていない人」こそ管理者は見たいので LEFT JOIN が正解です。

**RowMapper のヒント**：`rs.getLong("completed_count")`、`last_login_at` は `rs.getObject("last_login_at", LocalDateTime.class)`（NULLが入りうるので `getObject` を使う）。既存の `userRowMapper`（`UserRepositoryImpl.java`）が参考になります。

---

## ステップ4：ログイン時に「最終ログイン日時」を記録する

ここを忘れると `last_login_at` は永遠にNULLのままです。2段階で実装します。

### 4-1. 更新用メソッドを Repository に追加

```java
// UserRepository
void updateLastLoginAt(String username, LocalDateTime loginAt);
// 実装: UPDATE USERS SET last_login_at = ? WHERE username = ?
```

### 4-2. ログイン成功時に呼び出す

`SecurityConfig.java` の `successHandler` 内で、リダイレクト前に更新を呼びます。

```java
.successHandler((request, response, authentication) -> {
    userRepository.updateLastLoginAt(authentication.getName(), LocalDateTime.now()); // ★追加
    // 既存の ADMIN/一般 振り分けロジックはそのまま
})
```

> この場合 `SecurityConfig` に `UserRepository`（または Service）をコンストラクタインジェクションする必要があります。Service を経由させる方が層がきれいですが、まずは動かすことを優先してもOK。

---

## ステップ5：Service層にメソッドを追加

**ファイル**：`src/main/java/com/example/tracker/service/TrackerService.java`

Controller から Repository を直接触らず、Service を1枚かませる既存の設計に合わせます。

```java
/** 全ユーザーの学習進捗一覧を返す（管理者ダッシュボード用）。 */
public List<UserProgress> getAllUserProgress() {
    return userRepository.findAllUserProgress();
}
```

> 集計ロジックがSQL側に寄っているので Service は薄くてOK。

---

## ステップ6：Controller を修正

**ファイル**：`src/main/java/com/example/tracker/controller/AdminController.java`

既存の `adminTop` に進捗一覧を足すか、別画面 `/admin/dashboard` を新設するか選びます。

**おすすめ**：既存の `/admin` 画面に追加表示（画面を分けず1つにまとめる方がシンプル）。

```java
@GetMapping
public String adminTop(Model model) {
    model.addAttribute("users", userRepository.findAll());          // 既存：ロール変更用
    model.addAttribute("userProgress", trackerService.getAllUserProgress()); // ★追加
    return "admin/index";
}
```

> `TrackerService` をこのControllerにインジェクションする必要があります（現状は `UserRepository` のみ注入）。

---

## ステップ7：画面（Thymeleaf）に一覧表を追加

**ファイル**：`src/main/resources/templates/admin/index.html`

既存のロール変更テーブルの下に、進捗一覧テーブルを追加します。

```html
<h2>学習状況一覧</h2>
<table border="1">
    <thead>
        <tr>
            <th>ユーザー名</th>
            <th>完了タスク数</th>
            <th>未完了タスク数</th>
            <th>最終ログイン日時</th>
        </tr>
    </thead>
    <tbody>
        <tr th:each="p : ${userProgress}">
            <td th:text="${p.username}"></td>
            <td th:text="${p.completedCount}"></td>
            <td th:text="${p.incompleteCount}"></td>
            <td th:text="${p.lastLoginAt != null
                            ? #temporals.format(p.lastLoginAt, 'yyyy/MM/dd HH:mm')
                            : '未ログイン'}"></td>
        </tr>
    </tbody>
</table>
```

> `#temporals.format` は `LocalDateTime` 整形用。NULL（未ログイン）の出し分けも入れておくと親切です。

---

## ステップ8：テストを追加

Repository / Controller / Security の3カテゴリでテストを追加します。

- **Repositoryテスト**：`findAllUserProgress()` が、タスクを持つユーザー・持たないユーザー両方で正しい件数を返すか（特にLEFT JOINで0件ユーザーが消えないこと）。既存の `UserRepositoryImplTest` が雛形。
- **Controllerテスト**：`/admin` に `userProgress` がモデルに乗るか。`AdminControllerTest` が雛形。
- **Securityテスト**：一般ユーザーで `/admin` が 403/リダイレクトされるか（既存 `SecurityTest` でカバー済みか確認）。

---

## ステップ9：動作確認

1. サーバー再起動（schema.sql が再作成される）
2. `admin` でログイン → 一覧に各ユーザーの完了/未完了数が出るか
3. `user` でログイン → ログアウト → 再び `admin` で確認 → `user` の最終ログイン日時が更新されているか
4. タスクを持たないユーザー（例：`admin` 自身）が「0 / 0」で**表示され続ける**こと

---

## ステップ10：README を更新する

このプロジェクトは README に機能・構成・DB設計・テストを記録する運用なので、実装後に以下を反映します。**現状の `README.md` を見て、該当する箇所を更新してください。**

### 更新が必要な箇所一覧

| README のセクション | 現状 | 追記・修正する内容 |
|---|---|---|
| **機能** | 「管理者画面 (ADMIN ロール専用)」 | 「全ユーザーの学習状況一覧（完了/未完了タスク数・最終ログイン日時）」を追記 |
| **プロジェクト構成** | `model/` に `UserProgress.java` が無い | `UserProgress.java`（…ユーザー進捗をまとめたクラス）を追記。新規テストファイルも `test/` 配下に追記 |
| **画面・エンドポイント → 管理者画面** | 「ユーザー一覧表示」「権限変更」の2行 | `GET /admin` の説明を「ユーザー一覧＋学習状況一覧表示」に更新（エンドポイントを分けた場合は行を追加） |
| **データベース → ER図** | `USERS` に `last_login_at` が無い | Mermaid の `USERS {}` に `TIMESTAMP last_login_at` を追記 |
| **テスト → テストの種類** | 既存テスト一覧 | 追加した Repository / Controller テストの観点を追記 |

### あわせて更新する別ファイル

- **`docs/er-diagram.md`** … README の Mermaid と同じく、`USERS` テーブルに `last_login_at` 列を追記（2か所のER図がズレないように）
- **Javadoc** … `UserProgress` など新規クラスを作ったら、ドキュメントコメントを書いた上で `./mvnw javadoc:javadoc` で再生成

> ポイント：**ER図は README と `docs/er-diagram.md` の2か所にあります。** 片方だけ直すと不整合になるので、両方そろえて更新してください。

---

## 作業順序のまとめ（依存関係）

```
1. schema.sql 列追加
2. UserProgress DTO 作成
3. 集計SQL (Repository)              ┐
4. ログイン記録 (Repository+Security) ┘ ← 3,4は並行可
5. Service メソッド
6. Controller 修正
7. 画面 (HTML)
8. テスト
9. 動作確認
10. README / ER図 / Javadoc 更新
```

特に **ステップ1・4（最終ログイン日時の保存）** が新しい概念（DB列追加＋Securityイベント連携）なので、ここから着手すると詰まりやすい所を先に潰せます。
