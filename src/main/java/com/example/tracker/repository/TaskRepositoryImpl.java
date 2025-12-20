package com.example.tracker.repository;

import com.example.tracker.model.Task;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JdbcTemplateを使用したTaskRepositoryの実装
 */
@Repository
public class TaskRepositoryImpl implements TaskRepository {
    
    private final JdbcTemplate jdbcTemplate;
    
    // RowMapper for Task
    private final RowMapper<Task> taskRowMapper = (rs, rowNum) -> {
        Task task = new Task();
        task.setId(rs.getLong("id"));
        task.setSubjectId(rs.getLong("subject_id"));
        task.setTitle(rs.getString("title"));
        task.setCompleted(rs.getBoolean("completed"));
        return task;
    };
    
    public TaskRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    @Override
    public List<Task> findBySubjectId(Long subjectId) {
        String sql = "SELECT id, subject_id, title, completed FROM TASK WHERE subject_id = ? ORDER BY id";
        return jdbcTemplate.query(sql, taskRowMapper, subjectId);
    }
    
    @Override
    public void insert(Long subjectId, String title) {
        String sql = "INSERT INTO TASK (subject_id, title, completed) VALUES (?, ?, FALSE)";
        jdbcTemplate.update(sql, subjectId, title);
    }
    
    @Override
    public void updateCompleted(Long taskId, boolean completed) {
        String sql = "UPDATE TASK SET completed = ? WHERE id = ?";
        jdbcTemplate.update(sql, completed, taskId);
    }
    
    @Override
    public void deleteById(Long taskId) {
        String sql = "DELETE FROM TASK WHERE id = ?";
        jdbcTemplate.update(sql, taskId);
    }
    
    @Override
    public int countBySubjectId(Long subjectId) {
        String sql = "SELECT COUNT(*) FROM TASK WHERE subject_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, subjectId);
        return count != null ? count : 0;
    }
    
    @Override
    public int countCompletedBySubjectId(Long subjectId) {
        String sql = "SELECT COUNT(*) FROM TASK WHERE subject_id = ? AND completed = TRUE";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, subjectId);
        return count != null ? count : 0;
    }
}
