package com.example.tracker.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * TaskStatsモデルのテストクラス 
 */
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
