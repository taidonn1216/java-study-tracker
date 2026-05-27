package com.example.tracker.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Subjectモデルのテストクラス
 */
class SubjectTest {

    @Test
    void testDefaultConstructor() {
        Subject subject = new Subject();
        assertNull(subject.getId());
        assertNull(subject.getName());
    }

    @Test
    void testParameterizedConstructor() {
        Subject subject = new Subject(1L, "数学");
        assertEquals(1L, subject.getId());
        assertEquals("数学", subject.getName());
    }

    @Test
    void testSettersAndGetters() {
        Subject subject = new Subject();
        subject.setId(2L);
        subject.setName("英語");

        assertEquals(2L, subject.getId());
        assertEquals("英語", subject.getName());
    }

    @Test
    void testToString() {
        Subject subject = new Subject(1L, "理科");
        String expected = "Subject{id=1, name='理科'}";
        assertEquals(expected, subject.toString());
    }
}
