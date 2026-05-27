package com.example.tracker.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

/**
 * Taskモデルのテストクラス
 */
class TaskTest {

    @Test
    void testDefaultConstructor() {
        Task task = new Task();
        assertNull(task.getId());
        assertNull(task.getSubjectId());
        assertNull(task.getTitle());
    }

    @Test
    void testParameterizedConstructor() {
        Task task = new Task(1L, 2L, "問題集1-10ページ", TaskStatus.NOT_STARTED, LocalDate.parse("2026-02-20"), "メモ");
        assertEquals(1L, task.getId());
        assertEquals(2L, task.getSubjectId());
        assertEquals("問題集1-10ページ", task.getTitle());
        assertEquals(TaskStatus.NOT_STARTED, task.getStatus());
        assertEquals(LocalDate.parse("2026-02-20"), task.getDeadline());
        assertEquals("メモ", task.getReflection());
    }

    @Test
    void testSettersAndGetters() {
        Task task = new Task();
        task.setId(3L);
        task.setSubjectId(5L);
        task.setTitle("テスト勉強");
        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setDeadline(LocalDate.parse("2026-02-20"));
        task.setReflection("振り返りメモ");

        assertEquals(3L, task.getId());
        assertEquals(5L, task.getSubjectId());
        assertEquals("テスト勉強", task.getTitle());
        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
        assertEquals(LocalDate.parse("2026-02-20"), task.getDeadline());
        assertEquals("振り返りメモ", task.getReflection());
    }

    @Test
    void testToString() {
        Task task = new Task(1L, 2L, "宿題", TaskStatus.DONE, LocalDate.parse("2026-02-21"), "振り返り");
        String expected = "Task{id=1, subjectId=2, title='宿題', status='DONE', deadline=2026-02-21, reflection='振り返り'}";
        assertEquals(expected, task.toString());
    }
}
