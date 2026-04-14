package com.example.tracker.repository;

import com.example.tracker.model.Task;
import com.example.tracker.model.TaskStatus;
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
    private Long userId;
    
    @BeforeEach
    void setUp() {
        // テストデータをクリア
        jdbcTemplate.execute("DELETE FROM TASK");
        jdbcTemplate.execute("DELETE FROM SUBJECT");
        jdbcTemplate.execute("DELETE FROM USERS");
        jdbcTemplate.execute("ALTER TABLE USERS ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE SUBJECT ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE TASK ALTER COLUMN id RESTART WITH 1");
        
        // テスト用ユーザー作成
        jdbcTemplate.update(
            "INSERT INTO USERS (username, password, enabled) VALUES (?, ?, ?)",
            "testuser",
            "$2b$10$8gl4P.2H1sEOH0mU8moubOHvLxrfqr6AKtJ326H3CbgJ2dikD9/ma",
            true
        ); 
        userId = jdbcTemplate.queryForObject(
            "SELECT id FROM USERS WHERE username = ?",
            Long.class,
            "testuser"
        );

        // テスト用の科目を作成
        subjectRepository.insert("数学", userId);
        subjectId = subjectRepository.findAll().get(0).getId();
    }
    
    @Test
    void testInsert() {
        taskRepository.insert(subjectId, "問題集1-10ページ", TaskStatus.NOT_STARTED, LocalDate.parse("2026-03-01"), "");
        
        List<Task> tasks = taskRepository.findBySubjectIdAndUserId(subjectId, userId);
        assertEquals(1, tasks.size());
        assertEquals("問題集1-10ページ", tasks.get(0).getTitle());
        assertEquals(subjectId, tasks.get(0).getSubjectId());
        assertFalse(tasks.get(0).isCompleted());
    }
    
    @Test
    void testFindBySubjectId() {
        taskRepository.insert(subjectId, "タスク1", TaskStatus.NOT_STARTED, LocalDate.parse("2026-03-01"), "");
        taskRepository.insert(subjectId, "タスク2", TaskStatus.NOT_STARTED, LocalDate.parse("2026-03-01"), "");
        taskRepository.insert(subjectId, "タスク3", TaskStatus.NOT_STARTED, LocalDate.parse("2026-03-01"), "");
        
        List<Task> tasks = taskRepository.findBySubjectIdAndUserId(subjectId, userId);
        assertEquals(3, tasks.size());
        assertEquals("タスク1", tasks.get(0).getTitle());
        assertEquals("タスク2", tasks.get(1).getTitle());
        assertEquals("タスク3", tasks.get(2).getTitle());
    }
    
    @Test
    void testFindBySubjectId_NoTasks() {
        List<Task> tasks = taskRepository.findBySubjectIdAndUserId(subjectId, userId);
        assertTrue(tasks.isEmpty());
    }
    
    @Test
    void testUpdateCompleted() {
        taskRepository.insert(subjectId, "問題集1-10ページ", TaskStatus.NOT_STARTED, LocalDate.parse("2026-03-01"), "");
        Long taskId = taskRepository.findBySubjectIdAndUserId(subjectId, userId).get(0).getId();
        
        // 未完了の状態を確認
        Task task = taskRepository.findBySubjectIdAndUserId(subjectId, userId).get(0);
        assertFalse(task.isCompleted());
        
        // 完了に更新
        int updated1 = taskRepository.updateCompletedByIdAndUserId(taskId, true, userId);
        assertEquals(1, updated1);
        task = taskRepository.findBySubjectIdAndUserId(subjectId, userId).get(0);
        assertTrue(task.isCompleted());
        
        // 未完了に戻す
        int updated2 = taskRepository.updateCompletedByIdAndUserId(taskId, false, userId);
        assertEquals(1, updated2);
        task = taskRepository.findBySubjectIdAndUserId(subjectId, userId).get(0);
        assertFalse(task.isCompleted());
    }
    
    @Test
    void testDeleteById() {
        taskRepository.insert(subjectId, "タスク1", TaskStatus.NOT_STARTED, LocalDate.parse("2026-03-01"), "");
        taskRepository.insert(subjectId, "タスク2", TaskStatus.NOT_STARTED, LocalDate.parse("2026-03-01"), "");
        
        List<Task> tasks = taskRepository.findBySubjectIdAndUserId(subjectId, userId);
        assertEquals(2, tasks.size());
        
        Long taskIdToDelete = tasks.get(0).getId();
        int deleted = taskRepository.deleteByIdAndUserId(taskIdToDelete, userId);
        assertEquals(1, deleted); 
        
        tasks = taskRepository.findBySubjectIdAndUserId(subjectId, userId);
        assertEquals(1, tasks.size());
        assertEquals("タスク2", tasks.get(0).getTitle());
    }
    
    @Test
    void testCountBySubjectId() {
        assertEquals(0, taskRepository.countBySubjectId(subjectId));
        
        taskRepository.insert(subjectId, "タスク1", TaskStatus.NOT_STARTED, LocalDate.parse("2026-03-01"), "");
        assertEquals(1, taskRepository.countBySubjectId(subjectId));
        
        taskRepository.insert(subjectId, "タスク2", TaskStatus.NOT_STARTED, LocalDate.parse("2026-03-01"), "");
        taskRepository.insert(subjectId, "タスク3", TaskStatus.NOT_STARTED, LocalDate.parse("2026-03-01"), "");
        assertEquals(3, taskRepository.countBySubjectId(subjectId));
    }
    
    @Test
    void testCountCompletedBySubjectId() {
        taskRepository.insert(subjectId, "タスク1", TaskStatus.NOT_STARTED, LocalDate.parse("2026-03-01"), "");
        taskRepository.insert(subjectId, "タスク2", TaskStatus.NOT_STARTED, LocalDate.parse("2026-03-01"), "");
        taskRepository.insert(subjectId, "タスク3", TaskStatus.NOT_STARTED, LocalDate.parse("2026-03-01"), "");
        
        assertEquals(0, taskRepository.countCompletedBySubjectId(subjectId));
        
        List<Task> tasks = taskRepository.findBySubjectIdAndUserId(subjectId, userId);
        taskRepository.updateCompletedByIdAndUserId(tasks.get(0).getId(), true, userId);
        assertEquals(1, taskRepository.countCompletedBySubjectId(subjectId));
        
        taskRepository.updateCompletedByIdAndUserId(tasks.get(1).getId(), true, userId);
        assertEquals(2, taskRepository.countCompletedBySubjectId(subjectId));
    }

    @Test
    void testFindBySubjectIdAndStatusAndUserId() {
        taskRepository.insert(subjectId, "未着手", TaskStatus.NOT_STARTED, LocalDate.parse("2026-03-01"), "");
        taskRepository.insert(subjectId, "進行中", TaskStatus.IN_PROGRESS, LocalDate.parse("2026-03-02"), "");
        taskRepository.insert(subjectId, "完了", TaskStatus.DONE, LocalDate.parse("2026-03-03"), "");

        List<Task> inProgress = taskRepository.findBySubjectIdAndStatusAndUserId(subjectId, TaskStatus.IN_PROGRESS, userId);
        assertEquals(1, inProgress.size());
        assertEquals("進行中", inProgress.get(0).getTitle());

        List<Task> done = taskRepository.findBySubjectIdAndStatusAndUserId(subjectId, TaskStatus.DONE, userId);
        assertEquals(1, done.size());
        assertEquals("完了", done.get(0).getTitle());
    }

    @Test
    void testUpdateStatusByIdAndUserId() {
        taskRepository.insert(subjectId, "更新対象", TaskStatus.NOT_STARTED, LocalDate.parse("2026-03-01"), "");
        Long taskId = taskRepository.findBySubjectIdAndUserId(subjectId, userId).get(0).getId();

        int updated = taskRepository.updateStatusByIdAndUserId(taskId, TaskStatus.DONE, true, userId);
        assertEquals(1, updated);

        Task task = taskRepository.findBySubjectIdAndUserId(subjectId, userId).get(0);
        assertEquals(TaskStatus.DONE, task.getStatus());
        assertTrue(task.isCompleted());
    }
}