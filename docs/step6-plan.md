# Step 6 作業手順書：Controller を Service 経由に完全移行する

## 現状の整理

Step 5 でリポジトリ・テストの `userId` 対応は完了している。  
次のゴールは **Controller がリポジトリを直接呼ばず、すべて Service 経由で動く** 状態にすること。

### 現在 Controller で壊れている箇所（コンパイルエラー）

| 箇所 | 問題 |
|---|---|
| `index()` 75行目 | `getSubjectSummariesForCurrentUser()` が Service に存在しない |
| `index()` 76行目 | `getOverdueTasksForCurrentUser()` が Service に存在しない / `usernama` タイポ |
| `createSubject()` 91行目 | `subjectRepository` は Controller に注入されていないのに直接呼んでいる |
| `deleteSubject()` 105行目 | 同上 |
| `subjectDetails()` 135〜157行目 | 同上（`subjectRepository`, `taskRepository` 直接呼び出し） |
| `createTask()` 247行目 | 同上 |
| `deleteTask()` 262行目 | 同上 |
| `completeTask()` 281行目 | 同上 |
| `toggleCompleteTask()` 302行目 | 同上 |
| `updateReflection()` 319行目 | 同上 |

---

## 作業の大方針

```
①  Service にメソッドを追加する（Controller の受け皿を作る）
②  Controller の各メソッドを Service 呼び出しに置き換える
③  全ステップ完了後にアプリ起動 → ブラウザ確認
```

**1ステップ = 1メソッドを直す** で進める。  
各ステップ後の確認は `./mvnw compile` でエラーが減っているかだけ確認する。  
**アプリ起動は Step 6-6 が終わってから（それまではコンパイルエラーが残るため起動不可）。**

---

## Step 6-1：`index()` を直す

### 6-1-A：TrackerService にメソッドを2つ追加する

**追加場所：** [TrackerService.java](../src/main/java/com/example/tracker/service/TrackerService.java)

追加するメソッド ①

```java
// import も追加: import com.example.tracker.model.SubjectSummary; import java.util.List;
public List<SubjectSummary> getSubjectSummariesForCurrentUser(String username) {
    Long userId = currentUserId(username);
    return subjectRepository.findAllWithTaskStatsByUserId(userId);
}
```

追加するメソッド ②

```java
// import も追加: import com.example.tracker.model.Task; import java.time.LocalDate;
public List<Task> getOverdueTasksForCurrentUser(String username, LocalDate today) {
    Long userId = currentUserId(username);
    return taskRepository.findOverdueTasksByUserId(userId, today);
}
```

### 6-1-B：Controller の `index()` のタイポを直す

**修正場所：** [TrackerController.java:76](../src/main/java/com/example/tracker/controller/TrackerController.java#L76)

```java
// 修正前
trackerService.getOverdueTasksForCurrentUser(usernama, LocalDate.now());

// 修正後
trackerService.getOverdueTasksForCurrentUser(username, LocalDate.now());
```

### 6-1-C：コンパイル確認

```bash
./mvnw compile
```

- `usernama` と Service メソッド不在のエラー2件が消えていることを確認
- 残り8件のエラーはこの時点では残ったままで OK

---

## Step 6-2：`createSubject()` を直す

### 6-2-A：Controller の `createSubject()` を修正する

**修正場所：** [TrackerController.java:89〜93](../src/main/java/com/example/tracker/controller/TrackerController.java#L89)

`@AuthenticationPrincipal` を追加し、`trackerService.createSubjectForCurrentUser()` を呼ぶように変更する。  
（このメソッドは TrackerService にすでにある）

```java
@PostMapping("/subjects")
public String createSubject(
        @RequestParam("name") String name,
        @AuthenticationPrincipal UserDetails userDetails) {
    Long userId = trackerService.currentUserId(userDetails.getUsername());
    trackerService.createSubjectForCurrentUser(name, userId);
    return "redirect:/";
}
```

### 6-2-B：コンパイル確認

```bash
./mvnw compile
```

- 91行目のエラーが消えていることを確認

---

## Step 6-3：`deleteSubject()` を直す

### 6-3-A：TrackerService にメソッドを追加する

```java
public void deleteSubjectForCurrentUser(Long subjectId, Long userId) {
    int deleted = subjectRepository.deleteByIdAndUserId(subjectId, userId);
    if (deleted == 0) {
        throw new RuntimeException("Subject not found or forbidden");
    }
}
```

### 6-3-B：Controller の `deleteSubject()` を修正する

**修正場所：** [TrackerController.java:103〜107](../src/main/java/com/example/tracker/controller/TrackerController.java#L103)

```java
@PostMapping("/subjects/{id}/delete")
public String deleteSubject(
        @PathVariable("id") Long id,
        @AuthenticationPrincipal UserDetails userDetails) {
    Long userId = trackerService.currentUserId(userDetails.getUsername());
    trackerService.deleteSubjectForCurrentUser(id, userId);
    return "redirect:/";
}
```

### 6-3-C：コンパイル確認

```bash
./mvnw compile
```

- 105行目のエラーが消えていることを確認

---

## Step 6-4：`subjectDetails()` を直す

※ このメソッドは変更箇所が多いので、いちばん丁寧に進める。

### 6-4-A：TrackerService にメソッドを2つ追加する

タスク一覧取得（全件）：

```java
// import も追加: import com.example.tracker.model.TaskStatus;
public List<Task> getTasksForSubject(Long subjectId, Long userId) {
    return taskRepository.findBySubjectIdAndUserId(subjectId, userId);
}
```

タスク一覧取得（ステータス絞り込み）：

```java
public List<Task> getTasksByStatus(Long subjectId, TaskStatus status, Long userId) {
    return taskRepository.findBySubjectIdAndStatusAndUserId(subjectId, status, userId);
}
```

### 6-4-B：Controller の `subjectDetails()` を修正する

**修正場所：** [TrackerController.java:128〜199](../src/main/java/com/example/tracker/controller/TrackerController.java#L128)

変更点は3箇所：
1. 引数に `@AuthenticationPrincipal UserDetails userDetails` を追加
2. `subjectRepository.findById(id)` → `trackerService.getSubjectForCurrentUser()` に変更
3. `taskRepository.findBySubjectId()` など → `trackerService.getTasksForSubject()` などに変更

```java
@GetMapping("/subjects/{id}")
public String subjectDetails(
        @PathVariable("id") Long id,
        @RequestParam(name = "statusFilter", required = false) String statusFilter,
        @RequestParam(name = "sortOrder", required = false, defaultValue = "idAsc") String sortOrder,
        Model model,
        @AuthenticationPrincipal UserDetails userDetails) {

    Long userId = trackerService.currentUserId(userDetails.getUsername());

    // subjectOpt → Subject を直接取得（見つからなければ RuntimeException → リダイレクト）
    Subject subject;
    try {
        subject = trackerService.getSubjectForCurrentUser(id, userId);
    } catch (RuntimeException e) {
        return "redirect:/";
    }

    // 全タスク（統計用）
    List<Task> allTasks = trackerService.getTasksForSubject(id, userId);
    long totalTasks = allTasks.size();
    long completedTasks = allTasks.stream().filter(task -> Boolean.TRUE.equals(task.isCompleted())).count();
    long incompleteTasks = totalTasks - completedTasks;

    // 表示用タスク（絞り込み）
    List<Task> displayTasks;
    if (statusFilter == null || statusFilter.isEmpty()) {
        displayTasks = allTasks;
    } else {
        TaskStatus parsedFilter = TaskStatus.fromValue(statusFilter);
        displayTasks = trackerService.getTasksByStatus(id, parsedFilter, userId);
    }

    // ③ 並び替え（変更なし）
    // ... 既存の並び替えコードをそのまま残す ...

    model.addAttribute("subject", subject);
    model.addAttribute("tasks", displayTasks);
    model.addAttribute("totalTasks", totalTasks);
    model.addAttribute("completedTasks", completedTasks);
    model.addAttribute("incompleteTasks", incompleteTasks);
    model.addAttribute("statusFilter", statusFilter);
    model.addAttribute("sortOrder", sortOrder);

    return "subject_details";
}
```

### 6-4-C：コンパイル確認

```bash
./mvnw compile
```

- 135・146・157行目のエラーが消えていることを確認

---

## Step 6-5：`createTask()` を直す

### 6-5-A：Controller の `createTask()` を修正する

**修正場所：** [TrackerController.java:215〜249](../src/main/java/com/example/tracker/controller/TrackerController.java#L215)

`@AuthenticationPrincipal` を追加し、所有権チェック後にタスクを追加する。

```java
@PostMapping("/subjects/{subjectId}/tasks")
public String createTask(
        @PathVariable("subjectId") Long subjectId,
        @RequestParam("title") String title,
        @RequestParam("status") String status,
        @RequestParam("deadline") String deadline,
        @RequestParam("reflection") String reflection,
        RedirectAttributes redirectAttributes,
        @AuthenticationPrincipal UserDetails userDetails) {

    Long userId = trackerService.currentUserId(userDetails.getUsername());

    // 科目の所有権チェック（自分の科目かどうか）
    try {
        trackerService.getSubjectForCurrentUser(subjectId, userId);
    } catch (RuntimeException e) {
        return "redirect:/";
    }

    // 以下、既存のバリデーション + taskRepository.insert() を trackerService 経由に変更
    // taskRepository.insert(...) → trackerService に委譲するか、
    // または直接 taskRepository を Service に移譲するメソッドを作る

    // ... 既存のバリデーションロジックはそのまま ...
    taskRepository.insert(subjectId, title, parsedStatus, parsedDeadline, reflection);
    // ↑ ここは Step 6-5-B で Service メソッドを作るか、
    //   既存の taskRepository.insert() を Service に移す

    return "redirect:/subjects/" + subjectId;
}
```

### 6-5-B：TrackerService にメソッドを追加する（必要なら）

```java
public void createTask(Long subjectId, String title, TaskStatus status,
                       LocalDate deadline, String reflection) {
    taskRepository.insert(subjectId, title, status, deadline, reflection);
}
```

### 6-5-C：コンパイル確認

```bash
./mvnw compile
```

- 247行目のエラーが消えていることを確認

---

## Step 6-6：残りのメソッドを直す

以下も同様のパターンで1つずつ直す。  
**各メソッドを直したら都度 `./mvnw compile` でエラーが減っているか確認すること。**

### 6-6-A：`deleteTask()` を直す（262行目）

Service にメソッドを追加する：

```java
public void deleteTaskForCurrentUser(Long taskId, Long userId) {
    int deleted = taskRepository.deleteByIdAndUserId(taskId, userId);
    if (deleted == 0) {
        throw new RuntimeException("Task not found or forbidden");
    }
}
```

Controller を修正：`@AuthenticationPrincipal` を追加し、上記メソッドを呼ぶ。

---

### 6-6-B：`completeTask()` を直す（281行目）

`trackerService.updateTaskStatusForCurrentUser()` は Service にすでにある。  
Controller に `@AuthenticationPrincipal` を追加して呼び出しを切り替えるだけ。

---

### 6-6-C：`toggleCompleteTask()` を直す（302行目）

Service にメソッドを追加する：

```java
public void toggleCompleteForCurrentUser(Long taskId, boolean completed, Long userId) {
    int updated = taskRepository.updateCompletedByIdAndUserId(taskId, completed, userId);
    if (updated == 0) {
        throw new RuntimeException("Task not found or forbidden");
    }
}
```

Controller を修正：`@AuthenticationPrincipal` を追加し、上記メソッドを呼ぶ。

---

### 6-6-D：`updateReflection()` を直す（319行目）

Service にメソッドを追加する：

```java
public void updateReflectionForCurrentUser(Long taskId, String reflection, Long userId) {
    int updated = taskRepository.updateReflectionByIdAndUserId(taskId, reflection, userId);
    if (updated == 0) {
        throw new RuntimeException("Task not found or forbidden");
    }
}
```

Controller を修正：`@AuthenticationPrincipal` を追加し、上記メソッドを呼ぶ。

---

### 6-6-E：最終コンパイル確認

```bash
./mvnw compile
```

- **BUILD SUCCESS** になること（エラー0件）

---

## Step 6-7：仕上げ・動作確認

### チェックリスト

- [ ] Controller に `subjectRepository` / `taskRepository` の直接呼び出しがないこと
- [ ] Controller の全メソッドに `@AuthenticationPrincipal` が付いていること（または不要な理由が明確）
- [ ] アプリを起動してすべての画面が正常に動くこと
- [ ] `./mvnw test` でテストが通ること

---

## 参考：Service ↔ Repository ↔ Controller の対応表

| Controller のやりたいこと | 呼ぶべき Service メソッド | Service の中で呼ぶ Repository メソッド |
|---|---|---|
| トップページの科目一覧取得 | `getSubjectSummariesForCurrentUser(username)` | `findAllWithTaskStatsByUserId(userId)` |
| 期限切れタスク取得 | `getOverdueTasksForCurrentUser(username, today)` | `findOverdueTasksByUserId(userId, today)` |
| 科目作成 | `createSubjectForCurrentUser(name, userId)` | `insert(name, userId)` |
| 科目削除 | `deleteSubjectForCurrentUser(subjectId, userId)` | `deleteByIdAndUserId(subjectId, userId)` |
| 科目取得（所有権チェック付き） | `getSubjectForCurrentUser(subjectId, userId)` | `findByIdAndUserId(subjectId, userId)` |
| タスク一覧取得（全件） | `getTasksForSubject(subjectId, userId)` | `findBySubjectIdAndUserId(subjectId, userId)` |
| タスク一覧取得（絞り込み） | `getTasksByStatus(subjectId, status, userId)` | `findBySubjectIdAndStatusAndUserId(...)` |
| タスク作成 | `createTask(subjectId, title, status, deadline, reflection)` | `insert(...)` |
| タスク削除 | `deleteTaskForCurrentUser(taskId, userId)` | `deleteByIdAndUserId(taskId, userId)` |
| ステータス更新 | `updateTaskStatusForCurrentUser(taskId, subjectId, userId, status)` | `updateStatusByIdAndUserId(...)` |
| 完了フラグ更新 | `toggleCompleteForCurrentUser(taskId, completed, userId)` | `updateCompletedByIdAndUserId(...)` |
| 振り返り更新 | `updateReflectionForCurrentUser(taskId, reflection, userId)` | `updateReflectionByIdAndUserId(...)` |
