package com.example.tracker.repository;

import com.example.tracker.model.Subject;
import com.example.tracker.model.SubjectSummary;
import com.example.tracker.model.TaskStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SubjectRepositoryImplのテストクラス
 */
@JdbcTest
@Import({SubjectRepositoryImpl.class, TaskRepositoryImpl.class})
class SubjectRepositoryImplTest {
    
    @Autowired
    private SubjectRepository subjectRepository;
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Long userId;
    
    @BeforeEach
    void setUp() {
        // テストデータをクリア
        jdbcTemplate.execute("DELETE FROM TASK");
        jdbcTemplate.execute("DELETE FROM SUBJECT");
        jdbcTemplate.execute("DELETE FROM USERS");
        jdbcTemplate.execute("ALTER TABLE SUBJECT ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE TASK ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE USERS ALTER COLUMN id RESTART WITH 1");

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

    }
    
    @Test
    void testInsert() {
        subjectRepository.insert("数学", userId);
        
        List<Subject> subjects = subjectRepository.findAll();
        assertEquals(1, subjects.size());
        assertEquals("数学", subjects.get(0).getName());
    }
    
    @Test
    void testFindAll() {
        subjectRepository.insert("数学", userId);
        subjectRepository.insert("英語", userId);
        subjectRepository.insert("理科", userId);
        
        List<Subject> subjects = subjectRepository.findAll();
        assertEquals(3, subjects.size());
        assertEquals("数学", subjects.get(0).getName());
        assertEquals("英語", subjects.get(1).getName());
        assertEquals("理科", subjects.get(2).getName());
    }
    
    @Test
    void testFindById_Found() {
        subjectRepository.insert("数学", userId);
        
        Optional<Subject> subject = subjectRepository.findByIdAndUserId(1L, userId);
        assertTrue(subject.isPresent());
        assertEquals("数学", subject.get().getName());
    }
    
    @Test
    void testFindById_NotFound() {
        Optional<Subject> subject = subjectRepository.findByIdAndUserId(999L, userId);
        assertFalse(subject.isPresent());
    }

    @Test
    void testFindByidAndUserId_OtherUser() {
        //別ユーザーを作成
        jdbcTemplate.update(
            "INSERT INTO USERS (username, password, enabled) VALUES (?, ?, ?)",
            "otheruser", "password", true
        );
        Long otherUserId = jdbcTemplate.queryForObject(
            "SELECT id FROM USERS WHERE username = ?", Long.class, "otheruser"
        );

        //別ユーザーの科目を作成
        subjectRepository.insert("他人の科目", otherUserId);
        Long otherSubjectId = subjectRepository.findAll().get(0).getId();

        // 自分のuserIdでは取得できない
        Optional<Subject> result = subjectRepository.findByIdAndUserId(otherSubjectId, userId);
        assertFalse(result.isPresent());
    }
    
    @Test
    void testDeleteById() {
        subjectRepository.insert("数学", userId);
        subjectRepository.insert("英語", userId);
        
        List<Subject> subjects = subjectRepository.findAll();
        assertEquals(2, subjects.size());
        
        Long idToDelete = subjects.get(0).getId();
        subjectRepository.deleteByIdAndUserId(idToDelete, userId);
        
        subjects = subjectRepository.findAll();
        assertEquals(1, subjects.size());
        assertEquals("英語", subjects.get(0).getName());
    }

    @Test
    void testDeleteByIdAndUser_OtherUser() {
        //別ユーザーを作成
        jdbcTemplate.update(
            "INSERT INTO USERS (username, password, enabled) VALUES(?, ?, ?)",
            "otheruser", "password", true
        );
        Long otherUserId = jdbcTemplate.queryForObject(
            "SELECT id FROM USERS WHERE username = ?", Long.class, "otheruser"
        );

        //別ユーザーの科目を作成
        subjectRepository.insert("他人の科目", otherUserId);
        Long otherSubjectId = subjectRepository.findAll().get(0).getId();

        //自分のuserIdでは削除できない (0件)
        int deleted = subjectRepository.deleteByIdAndUserId(otherSubjectId, userId);
        assertEquals(0, deleted);
    }
    
    @Test
    void testFindAllWithTaskStats_NoTasks() {
        subjectRepository.insert("数学", userId);
        subjectRepository.insert("英語", userId);
        
        List<SubjectSummary> summaries = subjectRepository.findAllWithTaskStatsByUserId(userId);
        assertEquals(2, summaries.size());
        
        assertEquals("数学", summaries.get(0).getSubject().getName());
        assertEquals(0, summaries.get(0).getTotalTasks());
        assertEquals(0, summaries.get(0).getCompletedTasks());
    }
    
    @Test
    void testFindAllWithTaskStats_WithTasks() {
        subjectRepository.insert("数学", userId);
        Long subjectId = subjectRepository.findAll().get(0).getId();
        
        // タスクを追加
        taskRepository.insert(subjectId, "問題集1-10ページ", TaskStatus.NOT_STARTED, LocalDate.parse("2026-03-01"), "");
        taskRepository.insert(subjectId, "問題集11-20ページ", TaskStatus.NOT_STARTED, LocalDate.parse("2026-03-01"), "");
        taskRepository.insert(subjectId, "テスト勉強", TaskStatus.NOT_STARTED, LocalDate.parse("2026-03-01"), "");
        
        // 1つのタスクを完了にする
        List<Long> taskIds = jdbcTemplate.queryForList(
            "SELECT id FROM TASK WHERE subject_id = ? ORDER BY id", 
            Long.class, 
            subjectId
        );
        taskRepository.updateCompletedByIdAndUserId(taskIds.get(0), true, userId);
        
        List<SubjectSummary> summaries = subjectRepository.findAllWithTaskStatsByUserId(userId);
        assertEquals(1, summaries.size());
        assertEquals("数学", summaries.get(0).getSubject().getName());
        assertEquals(3, summaries.get(0).getTotalTasks());
        assertEquals(1, summaries.get(0).getCompletedTasks());
    }
}
