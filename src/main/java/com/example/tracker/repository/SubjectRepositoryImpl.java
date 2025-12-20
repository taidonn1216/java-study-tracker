package com.example.tracker.repository;

import com.example.tracker.model.Subject;
import com.example.tracker.model.SubjectSummary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JdbcTemplateを使用したSubjectRepositoryの実装
 */
@Repository
public class SubjectRepositoryImpl implements SubjectRepository {
    
    private final JdbcTemplate jdbcTemplate;
    
    // RowMapper for Subject
    private final RowMapper<Subject> subjectRowMapper = (rs, rowNum) -> {
        Subject subject = new Subject();
        subject.setId(rs.getLong("id"));
        subject.setName(rs.getString("name"));
        return subject;
    };
    
    public SubjectRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    @Override
    public List<Subject> findAll() {
        String sql = "SELECT id, name FROM SUBJECT ORDER BY id";
        return jdbcTemplate.query(sql, subjectRowMapper);
    }
    
    @Override
    public List<SubjectSummary> findAllWithTaskStats() {
        String sql = """
            SELECT 
                s.id,
                s.name,
                COUNT(t.id) as total_tasks,
                SUM(CASE WHEN t.completed = TRUE THEN 1 ELSE 0 END) as completed_tasks
            FROM SUBJECT s
            LEFT JOIN TASK t ON s.id = t.subject_id
            GROUP BY s.id, s.name
            ORDER BY s.id
            """;
        
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Subject subject = new Subject();
            subject.setId(rs.getLong("id"));
            subject.setName(rs.getString("name"));
            
            int totalTasks = rs.getInt("total_tasks");
            int completedTasks = rs.getInt("completed_tasks");
            
            return new SubjectSummary(subject, totalTasks, completedTasks);
        });
    }
    
    @Override
    public Optional<Subject> findById(Long id) {
        String sql = "SELECT id, name FROM SUBJECT WHERE id = ?";
        List<Subject> results = jdbcTemplate.query(sql, subjectRowMapper, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
    
    @Override
    public void insert(String name) {
        String sql = "INSERT INTO SUBJECT (name) VALUES (?)";
        jdbcTemplate.update(sql, name);
    }
    
    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM SUBJECT WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
