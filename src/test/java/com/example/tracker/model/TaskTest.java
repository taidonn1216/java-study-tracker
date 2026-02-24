package com.example.tracker.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
        assertEquals(0, task.getCompletedId());
    }
    
    @Test
    void testParameterizedConstructor() {
        Task task = new Task(1L, 2L, "問題集1-10ページ", 1);
        assertEquals(1L, task.getId());
        assertEquals(2L, task.getSubjectId());
        assertEquals("問題集1-10ページ", task.getTitle());
        assertEquals(1, task.getCompletedId());
    }
    
    @Test
    void testSettersAndGetters() {
        Task task = new Task();
        task.setId(3L);
        task.setSubjectId(5L);
        task.setTitle("テスト勉強");
        task.setCompletedId(3);
        
        assertEquals(3L, task.getId());
        assertEquals(5L, task.getSubjectId());
        assertEquals("テスト勉強", task.getTitle());
        assertEquals(3, task.getCompletedId());
    }
    
    @Test
    void testToString() {
        Task task = new Task(1L, 2L, "宿題", 1);
        String expected = "Task{id=1, subjectId=2, title='宿題', completed_id=1}";
        assertEquals(expected, task.toString());
    }
}
