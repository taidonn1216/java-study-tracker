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
        assertNull(task.getCompleted());
    }
    
    @Test
    void testParameterizedConstructor() {
        Task task = new Task(1L, 2L, "問題集1-10ページ", false);
        assertEquals(1L, task.getId());
        assertEquals(2L, task.getSubjectId());
        assertEquals("問題集1-10ページ", task.getTitle());
        assertFalse(task.getCompleted());
    }
    
    @Test
    void testSettersAndGetters() {
        Task task = new Task();
        task.setId(3L);
        task.setSubjectId(5L);
        task.setTitle("テスト勉強");
        task.setCompleted(true);
        
        assertEquals(3L, task.getId());
        assertEquals(5L, task.getSubjectId());
        assertEquals("テスト勉強", task.getTitle());
        assertTrue(task.getCompleted());
    }
    
    @Test
    void testToString() {
        Task task = new Task(1L, 2L, "宿題", true);
        String expected = "Task{id=1, subjectId=2, title='宿題', completed=true}";
        assertEquals(expected, task.toString());
    }
}
