package com.example.tracker.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SubjectSummaryモデルのテストクラス
 */
class SubjectSummaryTest {

    @Test
    void testConstructor() {
        Subject subject = new Subject(1L, "数学");
        SubjectSummary summary = new SubjectSummary(subject, 10, 7);

        assertEquals(subject, summary.getSubject());
        assertEquals(10, summary.getTotalTasks());
        assertEquals(7, summary.getCompletedTasks());
    }

    @Test
    void testSettersAndGetters() {
        Subject subject1 = new Subject(1L, "英語");
        Subject subject2 = new Subject(2L, "理科");
        SubjectSummary summary = new SubjectSummary(subject1, 5, 3);

        summary.setSubject(subject2);
        summary.setTotalTasks(8);
        summary.setCompletedTasks(4);

        assertEquals(subject2, summary.getSubject());
        assertEquals(8, summary.getTotalTasks());
        assertEquals(4, summary.getCompletedTasks());
    }

    @Test
    void testGetProgressPercentage_Normal() {
        Subject subject = new Subject(1L, "数学");
        SubjectSummary summary = new SubjectSummary(subject, 10, 7);

        assertEquals(70, summary.getProgressPercentage());
    }

    @Test
    void testGetProgressPercentage_AllCompleted() {
        Subject subject = new Subject(1L, "数学");
        SubjectSummary summary = new SubjectSummary(subject, 5, 5);

        assertEquals(100, summary.getProgressPercentage());
    }

    @Test
    void testGetProgressPercentage_NoCompleted() {
        Subject subject = new Subject(1L, "数学");
        SubjectSummary summary = new SubjectSummary(subject, 10, 0);

        assertEquals(0, summary.getProgressPercentage());
    }

    @Test
    void testGetProgressPercentage_NoTasks() {
        Subject subject = new Subject(1L, "数学");
        SubjectSummary summary = new SubjectSummary(subject, 0, 0);

        assertEquals(0, summary.getProgressPercentage());
    }

    @Test
    void testGetProgressPercentage_Rounding() {
        Subject subject = new Subject(1L, "数学");
        SubjectSummary summary = new SubjectSummary(subject, 3, 1);

        assertEquals(33, summary.getProgressPercentage());
    }
}
