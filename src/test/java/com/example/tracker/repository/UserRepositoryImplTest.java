package com.example.tracker.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import com.example.tracker.model.User;
import com.example.tracker.model.UserProgress;

/**
 * UserRepositoryImplTestのテストクラス
 */
@JdbcTest
@Import({ UserRepositoryImpl.class })
public class UserRepositoryImplTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Long userId;

    @BeforeEach
    void setup() {
        jdbcTemplate.execute("DELETE FROM TASK");
        jdbcTemplate.execute("DELETE FROM SUBJECT");
        jdbcTemplate.execute("DELETE FROM USERS");
        jdbcTemplate.execute("ALTER TABLE USERS ALTER COLUMN id RESTART WITH 1");

        jdbcTemplate.update(
                "INSERT INTO USERS (username, password, enabled, role) VALUES (?, ?, ?, ?)",
                "testuser",
                "$2b$10$8gl4P.2H1sEOH0mU8moubOHvLxrfqr6AKtJ326H3CbgJ2dikD9/ma",
                true,
                "GENERAL");

        userId = jdbcTemplate.queryForObject(
                "SELECT id FROM USERS WHERE username = ?",
                Long.class,
                "testuser");
    }

    @Test
    void testFindByUsername_Found() {
        Optional<User> user = userRepository.findByUsername("testuser");
        assertTrue(user.isPresent());
        assertEquals("testuser", user.get().getUsername());
        assertEquals("GENERAL", user.get().getRole());
    }

    @Test
    void testFindByUsername_NotFound() {
        Optional<User> user = userRepository.findByUsername("nobody");
        assertFalse(user.isPresent());
    }

    @Test
    void testInsert() {
        userRepository.insert("newuser", "password");
        Optional<User> user = userRepository.findByUsername("newuser");
        assertTrue(user.isPresent());
        assertEquals("newuser", user.get().getUsername());
        assertEquals("GENERAL", user.get().getRole());
    }

    @Test
    void testFindAll() {
        userRepository.insert("newuser", "password");
        List<User> users = userRepository.findAll();
        assertEquals(2, users.size());
        assertTrue(users.stream().anyMatch(u -> u.getUsername().equals("testuser")));
        assertTrue(users.stream().anyMatch(u -> u.getUsername().equals("newuser")));
    }

    @Test
    void testUpdateRole() {
        userRepository.updateRole(userId, "ADMIN");
        User user = userRepository.findById(userId);
        assertEquals("ADMIN", user.getRole());
    }

    @Test
    void testFindById() {
        User user = userRepository.findById(userId);
        assertEquals("testuser", user.getUsername());
        assertEquals("GENERAL", user.getRole());
    }

    @Test
    void testFindAllProgress_CountsCompletedAndIncompleted() {
        jdbcTemplate.update("INSERT INTO SUBJECT (name, user_id) VALUES (?, ?)", "数学", userId);
        Long subjectId = jdbcTemplate.queryForObject(
            "SELECT id FROM SUBJECT WHERE user_id = ?", Long.class, userId);
        jdbcTemplate.update("INSERT INTO TASK (subject_id, title, status) VALUES (?, ?, 'DONE')", subjectId, "task1");
        jdbcTemplate.update("INSERT INTO TASK (subject_id, title, status) VALUES (?, ?, 'DONE')", subjectId, "task2");
        jdbcTemplate.update("INSERT INTO TASK (subject_id, title, status) VALUES (?, ?, 'NOT_STARTED')", subjectId, "task3");

        UserProgress p = userRepository.findAllProgress().get(0);

        assertEquals(2, p.getCompletedCount());
        assertEquals(1, p.getIncompletedCount());  
    }
    
    @Test
    void testFindAllProgress_UserWithoutTasksIsNotDropped() {
        List<UserProgress> result = userRepository.findAllProgress();

        assertEquals(1, result.size());
        UserProgress p = result.get(0);
        assertEquals("testuser", p.getUsername());
        assertEquals(0, p.getCompletedCount());
        assertEquals(0, p.getIncompletedCount());
    }

    @Test
    void testFindAllProgress_LastLoginAtCanBeNull() {
        List<UserProgress> result = userRepository.findAllProgress();

        assertEquals(1, result.size());
        assertNull(result.get(0).getLastLoginAt());
    }

    @Test
    void testUpdateLastLoginAt_SetValueAndReadableViaFindAllProgress() {
        LocalDateTime loginAt =LocalDateTime.of(2026, 7, 2, 10, 30,0);

        userRepository.updateLastLoginAt("testuser", loginAt);

        UserProgress p = userRepository.findAllProgress().get(0);
        assertEquals(loginAt, p.getLastLoginAt());
    }

}