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
@Import({SubjectRepositoryImpl.class, TaskRepositoryImpl.class, TaskStatusRepositoryImpl.class})
class TaskRepositoryImplTest {
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private SubjectRepository subjectRepository;
    
    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    private Long subjectId;
    
    @BeforeEach
    void setUp() {
        // テストデータをクリア
        jdbcTemplate.execute("DELETE FROM TASK");
        jdbcTemplate.execute("DELETE FROM SUBJECT");
        jdbcTemplate.execute("DELETE FROM COMPLETE");
        jdbcTemplate.execute("ALTER TABLE SUBJECT ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE TASK ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE COMPLETE ALTER COLUMN completed_id RESTART WITH 1");
        
        // テスト用の科目を作成
        subjectRepository.insert("数学");
        subjectId = subjectRepository.findAll().get(0).getId();
    }
    
    @Test
    void testInsert() {
        taskStatusRepository.insertTaskStatus();
        taskRepository.insert(subjectId, "問題集1-10ページ", LocalDate.of(2026, 12, 4), "");
        
        List<Task> tasks = taskRepository.findBySubjectId(subjectId);
        assertEquals(1, tasks.size());
        assertEquals("問題集1-10ページ", tasks.get(0).getTitle());
        assertEquals(subjectId, tasks.get(0).getSubjectId());
        assertEquals(1,tasks.get(0).getCompletedId());
    }
    
    @Test
    void testFindBySubjectId() {
        taskStatusRepository.insertTaskStatus();
        taskRepository.insert(subjectId, "タスク1", LocalDate.of(2026,12,5), "");
        taskRepository.insert(subjectId, "タスク2", LocalDate.of(2026,12,7),"");
        taskRepository.insert(subjectId, "タスク3", LocalDate.of(2014,12,4),"");
        
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
        taskStatusRepository.insertTaskStatus();
        taskRepository.insert(subjectId, "問題集1-10ページ", LocalDate.of(2025,12,5),"");
        Long taskId = taskRepository.findBySubjectId(subjectId).get(0).getId();
        
        // 未完了の状態を確認
        Task task = taskRepository.findBySubjectId(subjectId).get(0);
        assertEquals(1,task.getCompletedId());
        
        // 完了に更新
        taskRepository.updateCompleted(taskId, 3);
        task = taskRepository.findBySubjectId(subjectId).get(0);
        assertEquals(3,task.getCompletedId());
        
        // 未完了に戻す
        taskRepository.updateCompleted(taskId, 1);
        task = taskRepository.findBySubjectId(subjectId).get(0);
        assertEquals(1,task.getCompletedId());
    }
    
    @Test
    void testDeleteById() {
        taskStatusRepository.insertTaskStatus();
        taskRepository.insert(subjectId, "タスク1", LocalDate.of(2024,12,4), "" );
        taskRepository.insert(subjectId, "タスク2", LocalDate.of(2027,1,12),"");
        
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
        taskStatusRepository.insertTaskStatus();
        assertEquals(0, taskRepository.countBySubjectId(subjectId));
        
        taskRepository.insert(subjectId, "タスク1", LocalDate.of(2012,4,14), "");
        assertEquals(1, taskRepository.countBySubjectId(subjectId));
        
        taskRepository.insert(subjectId, "タスク2", LocalDate.of(2024,2,29),"");
        taskRepository.insert(subjectId, "タスク3", LocalDate.of(2022,2,22),"");
        assertEquals(3, taskRepository.countBySubjectId(subjectId));
    }
    
    @Test
    void testCountCompletedBySubjectId() {
        taskStatusRepository.insertTaskStatus();
        taskRepository.insert(subjectId, "タスク1", LocalDate.of(2022,2,5),"");
        taskRepository.insert(subjectId, "タスク2", LocalDate.of(2027,12,24),"");
        taskRepository.insert(subjectId, "タスク3", LocalDate.of(2024,12,24),"");
        
        assertEquals(0, taskRepository.countCompletedBySubjectId(subjectId));
        
        List<Task> tasks = taskRepository.findBySubjectId(subjectId);
        taskRepository.updateCompleted(tasks.get(0).getId(), 3);
        assertEquals(1, taskRepository.countCompletedBySubjectId(subjectId));
        
        taskRepository.updateCompleted(tasks.get(1).getId(), 1);
        assertEquals(1, taskRepository.countCompletedBySubjectId(subjectId));
    }
}
