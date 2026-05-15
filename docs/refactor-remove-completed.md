# 手順書：completed フィールド削除リファクタリング

## 背景

`TASK` テーブルに `completed BOOLEAN` と `status VARCHAR` が両方存在し、二重管理になっている。
`status = 'DONE'` を「完了」の唯一の判断基準に統一し、`completed` を全層から削除する。

## 修正方針

- `completed` カラム・フィールド・メソッドを全て削除する
- 「完了」の判定は `status == DONE` に一本化する
- `updateCompletedByIdAndUserId()` は削除する（`status` と独立して `completed` だけ変更できる口をなくす）

---

## 修正ステップ

### Step 1：schema.sql（DB定義）

**ファイル**：`src/main/resources/schema.sql`

`completed BOOLEAN NOT NULL DEFAULT FALSE,` の行を削除する。

```sql
-- 変更前
completed BOOLEAN NOT NULL DEFAULT FALSE,
status VARCHAR(20) NOT NULL,

-- 変更後
status VARCHAR(20) NOT NULL,
```

---

### Step 2：data.sql（サンプルデータ）

**ファイル**：`src/main/resources/data.sql`

全9行のINSERT文から `completed` 列と値（`true`/`false`）を削除する。

```sql
-- 変更前
INSERT INTO TASK (subject_id, title, completed, status, deadline, reflection) VALUES (1, '...', true, 'DONE', '2026-02-26', '');

-- 変更後
INSERT INTO TASK (subject_id, title, status, deadline, reflection) VALUES (1, '...', 'DONE', '2026-02-26', '');
```

---

### Step 3：TaskRepositoryImpl.java（Repository実装）

**ファイル**：`src/main/java/com/example/tracker/repository/TaskRepositoryImpl.java`

変更箇所は5か所。

**① taskRowMapper**：`task.setCompleted(...)` の1行を削除

**② insert() メソッド**：

```java
// 変更前
String sql = "INSERT INTO TASK (subject_id, title, completed, status, deadline, reflection) VALUES (?, ?, FALSE, ?, ?, ?)";
jdbcTemplate.update(sql, subjectId, title, status.name(), deadline, reflection);

// 変更後
String sql = "INSERT INTO TASK (subject_id, title, status, deadline, reflection) VALUES (?, ?, ?, ?, ?)";
jdbcTemplate.update(sql, subjectId, title, status.name(), deadline, reflection);
```

**③ updateCompletedByIdAndUserId() メソッド**：メソッドごと削除

**④ updateStatusByIdAndUserId() メソッド**：`completed = ?` を削除

```java
// 変更前
public int updateStatusByIdAndUserId(Long taskId, TaskStatus status, boolean completed, Long userId) {
    String sql = "UPDATE TASK t SET status = ?, completed = ? WHERE t.id = ? AND EXISTS (...)";
    return jdbcTemplate.update(sql, status.name(), completed, taskId, userId);
}

// 変更後
public int updateStatusByIdAndUserId(Long taskId, TaskStatus status, Long userId) {
    String sql = "UPDATE TASK t SET status = ? WHERE t.id = ? AND EXISTS (...)";
    return jdbcTemplate.update(sql, status.name(), taskId, userId);
}
```

**⑤ findOverdueTasksByUserId() メソッド**：

```sql
-- 変更前
AND t.completed = FALSE

-- 変更後
AND t.status != 'DONE'
```

**⑥ countCompletedBySubjectId() メソッド**：

```java
// 変更前
String sql = "SELECT COUNT(*) FROM TASK WHERE subject_id = ? AND completed = TRUE";

// 変更後
String sql = "SELECT COUNT(*) FROM TASK WHERE subject_id = ? AND status = 'DONE'";
```

---

### Step 4：TaskRepository.java（インターフェース）

**ファイル**：`src/main/java/com/example/tracker/repository/TaskRepository.java`

- `updateCompletedByIdAndUserId()` のメソッド定義を削除
- `updateStatusByIdAndUserId()` のシグネチャから `boolean completed` を削除

```java
// 変更前
int updateStatusByIdAndUserId(Long taskId, TaskStatus status, boolean completed, Long userId);

// 変更後
int updateStatusByIdAndUserId(Long taskId, TaskStatus status, Long userId);
```

---

### Step 5：Task.java（モデル）

**ファイル**：`src/main/java/com/example/tracker/model/Task.java`

削除するもの：
- `private boolean completed;` フィールド
- `/** 完了フラグ */` Javadocコメント
- `isCompleted()` メソッド
- `setCompleted()` メソッド
- コンストラクタの `boolean completed` 引数と `this.completed = completed;`
- `toString()` 内の `", completed=" + completed +`

---

### Step 6：SubjectRepositoryImpl.java

**ファイル**：`src/main/java/com/example/tracker/repository/SubjectRepositoryImpl.java`

`findAllWithTaskStatsByUserId()` の集計クエリを変更する。

```sql
-- 変更前
SUM(CASE WHEN t.completed = TRUE THEN 1 ELSE 0 END) as completed_tasks

-- 変更後
SUM(CASE WHEN t.status = 'DONE' THEN 1 ELSE 0 END) as completed_tasks
```

---

### Step 7：TrackerService.java

**ファイル**：`src/main/java/com/example/tracker/service/TrackerService.java`

`updateTaskStatusForCurrentUser()` から `completed` の計算と引数渡しを削除する。

```java
// 変更前
boolean completed = (status == TaskStatus.DONE);
int updated = taskRepository.updateStatusByIdAndUserId(taskId, status, completed, userId);

// 変更後
int updated = taskRepository.updateStatusByIdAndUserId(taskId, status, userId);
```

---

### Step 8：TrackerController.java

**ファイル**：`src/main/java/com/example/tracker/controller/TrackerController.java`

`subjectDetails()` の完了タスク数カウントを変更する。

```java
// 変更前
long completedTasks = allTasks.stream().filter(task -> Boolean.TRUE.equals(task.isCompleted())).count();

// 変更後
long completedTasks = allTasks.stream().filter(task -> task.getStatus() == TaskStatus.DONE).count();
```

---

### Step 9：subject_details.html

**ファイル**：`src/main/resources/templates/subject_details.html`

タスク行の CSS クラス付与条件を変更する。

```html
<!-- 変更前 -->
th:classappend="${task.completed} ? 'completed' : ''"

<!-- 変更後 -->
th:classappend="${task.status.name() == 'DONE'} ? 'completed' : ''"
```

---

### Step 10：TaskRepositoryImplTest.java（テスト修正）

**ファイル**：`src/test/java/com/example/tracker/repository/TaskRepositoryImplTest.java`

**① testUpdateCompleted テスト**：メソッドごと削除（`updateCompletedByIdAndUserId` が存在しなくなるため）

**② testCountCompletedBySubjectId テスト**：`updateCompletedByIdAndUserId` の呼び出しを `updateStatusByIdAndUserId` に変更

```java
// 変更前
taskRepository.updateCompletedByIdAndUserId(tasks.get(0).getId(), true, userId);

// 変更後
taskRepository.updateStatusByIdAndUserId(tasks.get(0).getId(), TaskStatus.DONE, userId);
```

**③ testInsert テスト**：`assertFalse(tasks.get(0).isCompleted())` を削除

**④ testUpdateStatusByIdAndUserId テスト**：`assertTrue(task.isCompleted())` を削除

---

## 動作確認

各ステップ完了後に以下を実行してビルドエラーがないか確認する。

```bash
# Mac / Linux
./mvnw test

# Windows
.\mvnw.cmd test
```

## 修正ファイル一覧

| # | ファイル | 主な変更内容 |
|---|---|---|
| 1 | `src/main/resources/schema.sql` | `completed` カラム削除 |
| 2 | `src/main/resources/data.sql` | INSERT文から `completed` 削除 |
| 3 | `src/main/java/.../repository/TaskRepositoryImpl.java` | 5か所修正・メソッド削除 |
| 4 | `src/main/java/.../repository/TaskRepository.java` | インターフェース修正 |
| 5 | `src/main/java/.../model/Task.java` | フィールド・メソッド削除 |
| 6 | `src/main/java/.../repository/SubjectRepositoryImpl.java` | 集計クエリ修正 |
| 7 | `src/main/java/.../service/TrackerService.java` | 引数削除 |
| 8 | `src/main/java/.../controller/TrackerController.java` | 完了判定変更 |
| 9 | `src/main/resources/templates/subject_details.html` | CSS クラス条件変更 |
| 10 | `src/test/java/.../repository/TaskRepositoryImplTest.java` | テスト修正・削除 |
