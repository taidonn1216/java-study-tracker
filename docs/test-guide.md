# テスト追加手順書

今回のリファクタリング（TaskStats導入）に伴うテストの追加・修正。

対象ファイル:
- `src/test/java/com/example/tracker/model/TaskStatsTest.java`（新規作成）
- `src/test/java/com/example/tracker/controller/TrackerControllerTest.java`（既存修正）

---

## Step 1 — `TaskStatsTest.java` を新規作成する

**目的**: `TaskStats` のコンストラクタ・getter が正しく動作することを確認する。

`SubjectSummaryTest.java` のスタイルに合わせて作成する。

```java
package com.example.tracker.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaskStatsTest {

    @Test
    void testConstructor() {
        TaskStats stats = new TaskStats(10L, 7L, 3L);

        assertEquals(10L, stats.getTotal());
        assertEquals(7L, stats.getCompleted());
        assertEquals(3L, stats.getIncompleted());
    }

    @Test
    void testAllCompleted() {
        TaskStats stats = new TaskStats(5L, 5L, 0L);

        assertEquals(5L, stats.getTotal());
        assertEquals(5L, stats.getCompleted());
        assertEquals(0L, stats.getIncompleted());
    }

    @Test
    void testNoTasks() {
        TaskStats stats = new TaskStats(0L, 0L, 0L);

        assertEquals(0L, stats.getTotal());
        assertEquals(0L, stats.getCompleted());
        assertEquals(0L, stats.getIncompleted());
    }

    @Test
    void testNoCompleted() {
        TaskStats stats = new TaskStats(5L, 0L, 5L);

        assertEquals(5L, stats.getTotal());
        assertEquals(0L, stats.getCompleted());
        assertEquals(5L, stats.getIncompleted());
    }
}
```

---

## Step 2 — `TrackerControllerTest.java` を修正する

**目的**: コントローラーが `getTaskStatsForSubject` を呼び出すように変更されたため、
既存テストのモック設定が不足している。追加しないと `NullPointerException` でテストが落ちる。

### 2-1. import を追加する

ファイル上部のimport群に追加する。

```java
import com.example.tracker.model.TaskStats;
```

### 2-2. `testSubjectDetails` を修正する

`getTaskStatsForSubject` のモックが抜けているため追加する。

**変更前**
```java
when(trackerService.currentUserId("testuser")).thenReturn(1L);
when(trackerService.getSubjectForCurrentUser(1L, 1L)).thenReturn(subject);
when(trackerService.getTasksForSubject(1L, 1L)).thenReturn(tasks);
```

**変更後**
```java
when(trackerService.currentUserId("testuser")).thenReturn(1L);
when(trackerService.getSubjectForCurrentUser(1L, 1L)).thenReturn(subject);
when(trackerService.getTasksForSubject(1L, 1L)).thenReturn(tasks);
when(trackerService.getTaskStatsForSubject(1L, 1L)).thenReturn(new TaskStats(3L, 1L, 2L));
```

### 2-3. `testSubjectDetails_NoTasks` を修正する

同様にモックを追加する。

**変更前**
```java
when(trackerService.currentUserId("testuser")).thenReturn(1L);
when(trackerService.getSubjectForCurrentUser(1L, 1L)).thenReturn(subject);
when(trackerService.getTasksForSubject(1L, 1L)).thenReturn(Collections.emptyList());
```

**変更後**
```java
when(trackerService.currentUserId("testuser")).thenReturn(1L);
when(trackerService.getSubjectForCurrentUser(1L, 1L)).thenReturn(subject);
when(trackerService.getTasksForSubject(1L, 1L)).thenReturn(Collections.emptyList());
when(trackerService.getTaskStatsForSubject(1L, 1L)).thenReturn(new TaskStats(0L, 0L, 0L));
```

---

## 作業順序まとめ

```
Step 1: TaskStatsTest.java を新規作成
Step 2: TrackerControllerTest.java を修正
  2-1: import 追加
  2-2: testSubjectDetails にモック追加
  2-3: testSubjectDetails_NoTasks にモック追加
```

修正後は `./mvnw test` でテストが全て通ることを確認する。
