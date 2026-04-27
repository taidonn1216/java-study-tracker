# Step 6 テスト追加 手順書

## 完了条件（受け入れ基準）

- AユーザーでログインしてもBユーザーのタスクが一切見えない
- URL直打ちで他人の taskId / subjectId を操作しても更新されない
- 既存テスト＋追加テストが全通過

---

## 現状の把握

| カテゴリ | ファイル | 主なギャップ |
|---|---|---|
| Repository | `TaskRepositoryImplTest.java` | `deleteByIdAndUserId` の他ユーザーテストが未実装 |
| Controller | `TrackerControllerTest.java` | 他ユーザーのタスクへの status更新・削除テストが未実装 |

---

## Step 6-2: TaskRepositoryImplTest — 他ユーザーの削除不可テストを追加

**対象ファイル:** `src/test/java/com/example/tracker/repository/TaskRepositoryImplTest.java`

**追加するテスト:**
```java
@Test
void testDeleteByIdAndUserId_OtherUser() {
    Long otherSubjectId = createOtherUserSubject();
    taskRepository.insert(otherSubjectId, "他人のタスク", TaskStatus.NOT_STARTED, LocalDate.parse("2026-03-01"), "");
    Long otherTaskId = jdbcTemplate.queryForObject(
        "SELECT id FROM TASK WHERE subject_id = ?", Long.class, otherSubjectId
    );

    // 自分のuserIdでは削除できない (0件)
    int deleted = taskRepository.deleteByIdAndUserId(otherTaskId, userId);
    assertEquals(0, deleted);
}
```

**確認観点:** `deleteByIdAndUserId` が `WHERE id = ? AND subject_id IN (SELECT id FROM SUBJECT WHERE user_id = ?)` のような条件で実装されていること

---

## Step 6-3: TrackerControllerTest — 他ユーザーのタスク操作テストを追加

**対象ファイル:** `src/test/java/com/example/tracker/controller/TrackerControllerTest.java`

**追加するテスト1: ステータス更新（他ユーザーのtaskId）**
```java
@Test
@WithMockUser(username = "testuser")
void testUpdateTaskStatus_OtherUserTask() throws Exception {
    when(trackerService.currentUserId("testuser")).thenReturn(1L);
    doThrow(new RuntimeException("forbidden"))
        .when(trackerService).updateTaskStatusForCurrentUser(eq(99L), anyLong(), eq(1L), any());

    mockMvc.perform(post("/tasks/99/status")
            .param("subjectId", "2")
            .param("status", "完了"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/"));
}
```

**追加するテスト2: タスク削除（他ユーザーのtaskId）**
```java
@Test
@WithMockUser(username = "testuser")
void testDeleteTask_OtherUserTask() throws Exception {
    when(trackerService.currentUserId("testuser")).thenReturn(1L);
    doThrow(new RuntimeException("forbidden"))
        .when(trackerService).deleteTaskForCurrentUser(eq(99L), eq(1L));

    mockMvc.perform(post("/tasks/99/delete")
            .param("subjectId", "2"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/"));
}
```

> **注意:** このテストが通るには、Controller側で `RuntimeException` をキャッチして `/` にリダイレクトする実装が必要。Controller の実装と照合すること。

---

## Step 6-4: 全テスト実行

```bash
./mvnw test
```

**合格基準:**

| テストクラス | 件数 |
|---|---|
| `SecurityTest` | 3件 グリーン |
| `SubjectRepositoryImplTest` | 8件 グリーン |
| `TaskRepositoryImplTest` | 9件 グリーン（追加1件含む） |
| `TrackerControllerTest` | 15件 グリーン（追加2件含む） |

---

## 作業順序

```
6-2 (Repository追加) → テスト実行 → 6-3 (Controller追加) → 最終テスト実行
```

小さい単位でテストを実行しながら進めると、問題の切り分けがしやすい。
