package com.example.tracker.repository;

import com.example.tracker.model.Task;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * {@link TaskRepository} の JDBC 実装クラス。
 *
 * <p>Spring {@link JdbcTemplate} を使用して {@code TASK} テーブルに
 * 対するCRUD操作および統計クエリを実行する。
 * {@code @Repository} としてSpring DIコンテナに登録される。</p>
 *
 * @author tracker-team
 * @version 1.0
 * @since 1.0
 * @see TaskRepository
 */
@Repository
public class TaskRepositoryImpl implements TaskRepository {

    /** SQL実行用のSpring JdbcTemplate */
    private final JdbcTemplate jdbcTemplate;

    /**
     * {@link java.sql.ResultSet} の行を {@link Task} オブジェクトに変換する {@link RowMapper}。
     */
    private final RowMapper<Task> taskRowMapper = (rs, rowNum) -> {
        Task task = new Task();
        task.setId(rs.getLong("id"));
        task.setSubjectId(rs.getLong("subject_id"));
        task.setTitle(rs.getString("title"));
        task.setCompletedId(rs.getInt("completed_id"));
        task.setStatusName(rs.getString("completed")); 
        task.setDeadline(rs.getDate("deadline").toLocalDate());
        task.setComment(rs.getString("comment"));
        return task;
    };


    /**
     * コンストラクタインジェクション。
     *
     * @param jdbcTemplate Springが提供する {@link JdbcTemplate} インスタンス
     */
    public TaskRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /** {@inheritDoc} */
    @Override
    public List<Task> findBySubjectId(Long subjectId) {
         String sql = """
        SELECT t.id,
               t.subject_id,
               t.title,
               t.completed_id,
               t.deadline,
               t.comment,
               c.completed
        FROM TASK t
        JOIN COMPLETE c ON t.completed_id = c.completed_id
        WHERE t.subject_id = ?
        """;
        return jdbcTemplate.query(sql, taskRowMapper, subjectId);
    }

    /** {@inheritDoc} */
    @Override
    public void insert(Long subjectId, String title, LocalDate deadline, String comment) {
        String sql = "INSERT INTO TASK (subject_id, title, completed_id, deadline, comment) VALUES (?, ?, 1, ?, ?)";
        jdbcTemplate.update(sql, subjectId, title, deadline, comment);
    }

    /** {@inheritDoc} */
    @Override
    public void updateCompleted(Long taskId, int completedId) {
        String sql = "UPDATE TASK SET completed_id = ? WHERE id = ?";
        jdbcTemplate.update(sql, completedId, taskId);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteById(Long taskId) {
        String sql = "DELETE FROM TASK WHERE id = ?";
        jdbcTemplate.update(sql, taskId);
    }

    /** {@inheritDoc} */
    @Override
    public int countBySubjectId(Long subjectId) {
        String sql = "SELECT COUNT(*) FROM TASK WHERE subject_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, subjectId);
        return count != null ? count : 0;
    }

    /** {@inheritDoc} */
    @Override
    public int countCompletedBySubjectId(Long subjectId) {
        String sql = "SELECT COUNT(*) FROM TASK WHERE subject_id = ? AND completed_id = 3";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, subjectId);
        return count != null ? count : 0;
    }
}
