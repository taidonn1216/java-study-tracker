# Goal 5 実装手順書：ユーザー管理画面

管理者権限を持つユーザーだけがアクセスできるユーザー管理画面を実装する。

## 完成イメージ

- `/admin` にアクセスすると登録済みユーザーの一覧が表示される
- 各ユーザーの権限を GENERAL ↔ ADMIN に変更できる
- 自分自身の権限は変更できない（自己ロックアウト防止）

---

## Step 1：`UserRepository` にメソッドを追加する

**対象ファイル：** `src/main/java/com/example/tracker/repository/UserRepository.java`

インターフェースに以下の2メソッドを追加する。

```java
List<User> findAll();

void updateRole(Long id, String role);
```

`import java.util.List;` も忘れずに追加する。

> **確認ポイント：** インターフェースはメソッドの「宣言だけ」で、実装は書かない。

---

## Step 2：`UserRepositoryImpl` に実装を追加する

**対象ファイル：** `src/main/java/com/example/tracker/repository/UserRepositoryImpl.java`

Step 1 で宣言した2メソッドを実装する。

---

### ⚠️ ハマりポイント：RowMapper と SELECT カラムの不一致

既存の `userRowMapper` は `password` カラムも読み取るように書かれている。  
`findAll()` でこの RowMapper をそのまま使う場合は、SQL に `password` を含める必要がある。

**選択肢A：SQL に `password` を含める**

```java
@Override
public List<User> findAll() {
    String sql = "SELECT id, username, password, role FROM USERS";
    return jdbcTemplate.query(sql, userRowMapper);
}
```

**選択肢B：`findAll()` 専用の RowMapper を追加する（パスワードを取得しない）**

一覧表示にパスワードは不要なので、専用の RowMapper を用意する。  
クラスのフィールドに以下を追加する：

```java
private final RowMapper<User> userListRowMapper = (rs, rowNum) -> {
    User user = new User();
    user.setId(rs.getLong("id"));
    user.setUsername(rs.getString("username"));
    user.setRole(rs.getString("role"));
    return user;
};
```

`findAll()` ではこちらを使う：

```java
@Override
public List<User> findAll() {
    String sql = "SELECT id, username, role FROM USERS";
    return jdbcTemplate.query(sql, userListRowMapper);
}
```

> **どちらを選ぶか：** 動けばどちらでもOK。選択肢Bのほうが「必要なデータだけ取得する」という設計として丁寧。

---

**updateRole の実装：**

```java
@Override
public void updateRole(Long id, String role) {
    String sql = "UPDATE USERS SET role = ? WHERE id = ?";
    jdbcTemplate.update(sql, role, id);
}
```

> **確認ポイント：** アプリを起動してコンパイルエラーが出ないこと。

---

## Step 3：`AdminController` にユーザー一覧表示を追加する

**対象ファイル：** `src/main/java/com/example/tracker/controller/AdminController.java`

① `UserRepository` をコンストラクタインジェクションで受け取る。  
② 既存の `adminTop()` メソッドを修正し、`findAll()` の結果を `Model` に追加して渡す。

```java
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;

    public AdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public String adminTop(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin/index";
    }
}
```

> **確認ポイント：** `Model` を引数に追加することを忘れない。

---

## Step 4：`admin/index.html` にユーザー一覧を表示する

**対象ファイル：** `src/main/resources/templates/admin/index.html`

Thymeleaf の `th:each` でユーザーリストをテーブル表示する。  
まずはボタンなしで**一覧が表示されること**だけを確認する。

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head><title>ユーザー管理</title></head>
<body>
    <h1>ユーザー管理画面</h1>

    <table border="1">
        <thead>
            <tr>
                <th>ID</th>
                <th>ユーザー名</th>
                <th>権限</th>
                <th>操作</th>
            </tr>
        </thead>
        <tbody>
            <tr th:each="user : ${users}">
                <td th:text="${user.id}"></td>
                <td th:text="${user.username}"></td>
                <td th:text="${user.role}"></td>
                <td>（後で追加）</td>
            </tr>
        </tbody>
    </table>
</body>
</html>
```

> **確認ポイント：** ブラウザで `/admin` にアクセスしてテーブルが見えること。

---

## Step 5：権限変更ボタンを追加する

**対象ファイル：** `src/main/resources/templates/admin/index.html`

各行の「操作」列に POST フォームのボタンを追加する。  
現在の権限が `GENERAL` なら「ADMIN に昇格」、`ADMIN` なら「GENERAL に変更」ボタンを表示する。

```html
<td>
    <!-- 現在 GENERAL の場合：ADMIN に昇格するボタン -->
    <form th:if="${user.role == 'GENERAL'}"
          th:action="@{/admin/users/{id}/role(id=${user.id})}" method="post">
        <input type="hidden" name="role" value="ADMIN" />
        <button type="submit">ADMIN に昇格</button>
    </form>

    <!-- 現在 ADMIN の場合：GENERAL に変更するボタン -->
    <form th:if="${user.role == 'ADMIN'}"
          th:action="@{/admin/users/{id}/role(id=${user.id})}" method="post">
        <input type="hidden" name="role" value="GENERAL" />
        <button type="submit">GENERAL に変更</button>
    </form>
</td>
```

> **確認ポイント：** ボタンは見えるが、まだ押しても 405 エラーになる（Step 6 で解決）。

---

## Step 6：`AdminController` に権限変更エンドポイントを追加する

**対象ファイル：** `src/main/java/com/example/tracker/controller/AdminController.java`

```java
@PostMapping("/users/{id}/role")
public String updateRole(@PathVariable Long id,
                         @RequestParam String role) {
    userRepository.updateRole(id, role);
    return "redirect:/admin";
}
```

> **確認ポイント：** ボタンを押すと権限が変わり、一覧に反映されること。

---

## Step 7：自己ロックアウト防止を追加する

**対象ファイル：** `src/main/java/com/example/tracker/controller/AdminController.java`

ログイン中の自分自身の権限は変更できないようにする。  
`Authentication` は引数に追加するだけで Spring が自動で注入してくれる。

```java
@PostMapping("/users/{id}/role")
public String updateRole(@PathVariable Long id,
                         @RequestParam String role,
                         Authentication authentication) {
    User targetUser = userRepository.findById(id);
    String currentUsername = authentication.getName();

    if (targetUser.getUsername().equals(currentUsername)) {
        return "redirect:/admin?error=self";
    }

    userRepository.updateRole(id, role);
    return "redirect:/admin";
}
```

> **確認ポイント：** 自分自身の行のボタンを押しても権限が変わらないこと。

---

## 全体の進捗イメージ

```
Step 1 → Step 2  : データアクセス層の準備（UserRepository / Impl）
Step 3           : コントローラーとデータの接続
Step 4           : 画面に一覧表示（動作確認①）
Step 5           : 権限変更ボタンのUI追加
Step 6           : 権限変更の動作実装（動作確認②）
Step 7           : セキュリティ強化（自己ロックアウト防止）
```

---

## 補足：`UserRepository` に `findById` が必要になる場合

Step 7 で `findById(Long id)` を使う場合は、`UserRepository` にも追加が必要。

```java
Optional<User> findById(Long id);
```

実装の SQL：

```sql
SELECT id, username, role FROM USERS WHERE id = ?
```
