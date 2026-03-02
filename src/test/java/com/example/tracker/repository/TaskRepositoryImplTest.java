package com.example.tracker.repository;

import com.example.tracker.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TaskRepositoryImplのテストクラス
 */
@JdbcTest
@Import({SubjectRepositoryImpl.class, TaskRepositoryImpl.class})
class TaskRepositoryImplTest {
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private SubjectRepository subjectRepository;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    private Long subjectId;
    
    @BeforeEach
    void setUp() {
        // テストデータをクリア
        jdbcTemplate.execute("DELETE FROM TASK");
        jdbcTemplate.execute("DELETE FROM SUBJECT");
        jdbcTemplate.execute("ALTER TABLE SUBJECT ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE TASK ALTER COLUMN id RESTART WITH 1");
        
        // テスト用の科目を作成
        subjectRepository.insert("数学");
        subjectId = subjectRepository.findAll().get(0).getId();
    }
    
    @Test
    void testInsert() {
        taskRepository.insert(subjectId, "問題集1-10ページ","未着手", LocalDate.parse("2026-03-01"), "");
        
        List<Task> tasks = taskRepository.findBySubjectId(subjectId);
        assertEquals(1, tasks.size());
        assertEquals("問題集1-10ページ", tasks.get(0).getTitle());
        assertEquals(subjectId, tasks.get(0).getSubjectId());
        assertFalse(tasks.get(0).getCompleted());
    }
    
    @Test
    void testFindBySubjectId() {
        taskRepository.insert(subjectId, "タスク1", "未着手", LocalDate.parse("2026-03-01"), "");
        taskRepository.insert(subjectId, "タスク2", "未着手", LocalDate.parse("2026-03-01"), "");
        taskRepository.insert(subjectId, "タスク3", "未着手", LocalDate.parse("2026-03-01"), "");
        
        List<Task> tasks = taskRepository.findBySubjectId(subjectId);
        assertEquals(3, tasks.size());
        assertEquals("タスク1", tasks.get(0).getTitle());
        assertEquals("タスク2", tasks.get(1).getTitle());
        assertEquals("タスク3", tasks.get(2).getTitle());
    }
    
    @Test
    void testFindBySubjectId_NoTasks() {
        List<Task> tasks = taskRepository.findBySubjectId(subjectId);
        assertTrue(tasks.isEmpty());
    }
    
    @Test
    void testUpdateCompleted() {
        taskRepository.insert(subjectId, "問題集1-10ページ", "未着手", LocalDate.parse("2026-03-01"), "");
        Long taskId = taskRepository.findBySubjectId(subjectId).get(0).getId();
        
        // 未完了の状態を確認
        Task task = taskRepository.findBySubjectId(subjectId).get(0);
        assertFalse(task.getCompleted());
        
        // 完了に更新
        taskRepository.updateCompleted(taskId, true);
        task = taskRepository.findBySubjectId(subjectId).get(0);
        assertTrue(task.getCompleted());
        
        // 未完了に戻す
        taskRepository.updateCompleted(taskId, false);
        task = taskRepository.findBySubjectId(subjectId).get(0);
        assertFalse(task.getCompleted());
    }
    
    @Test
    void testDeleteById() {
        taskRepository.insert(subjectId, "タスク1", "未着手", LocalDate.parse("2026-03-01"), "");
        taskRepository.insert(subjectId, "タスク2", "未着手", LocalDate.parse("2026-03-01"), "");
        
        List<Task> tasks = taskRepository.findBySubjectId(subjectId);
        assertEquals(2, tasks.size());
        
        Long taskIdToDelete = tasks.get(0).getId();
        taskRepository.deleteById(taskIdToDelete);
        
        tasks = taskRepository.findBySubjectId(subjectId);
        assertEquals(1, tasks.size());
        assertEquals("タスク2", tasks.get(0).getTitle());
    }
    
    @Test
    void testCountBySubjectId() {
        assertEquals(0, taskRepository.countBySubjectId(subjectId));
        
        taskRepository.insert(subjectId, "タスク1", "未着手", LocalDate.parse("2026-03-01"), "");
        assertEquals(1, taskRepository.countBySubjectId(subjectId));
        
        taskRepository.insert(subjectId, "タスク2", "未着手", LocalDate.parse("2026-03-01"), "");
        taskRepository.insert(subjectId, "タスク3", "未着手", LocalDate.parse("2026-03-01"), "");
        assertEquals(3, taskRepository.countBySubjectId(subjectId));
    }
    
    @Test
    void testCountCompletedBySubjectId() {
        taskRepository.insert(subjectId, "タスク1", "未着手", LocalDate.parse("2026-03-01"), "");
        taskRepository.insert(subjectId, "タスク2", "未着手", LocalDate.parse("2026-03-01"), "");
        taskRepository.insert(subjectId, "タスク3", "未着手", LocalDate.parse("2026-03-01"), "");
        
        assertEquals(0, taskRepository.countCompletedBySubjectId(subjectId));
        
        List<Task> tasks = taskRepository.findBySubjectId(subjectId);
        taskRepository.updateCompleted(tasks.get(0).getId(), true);
        assertEquals(1, taskRepository.countCompletedBySubjectId(subjectId));
        
        taskRepository.updateCompleted(tasks.get(1).getId(), true);
        assertEquals(2, taskRepository.countCompletedBySubjectId(subjectId));
    }
}
