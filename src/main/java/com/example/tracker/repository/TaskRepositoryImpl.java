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
        task.setCompleted(rs.getBoolean("completed"));
        task.setStatus(rs.getString("status"));
        task.setDeadline(rs.getObject("deadline", LocalDate.class));
        task.setReflection(rs.getString("reflection"));
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
        String sql = "SELECT id, subject_id, title, completed, status, deadline, reflection  FROM TASK WHERE subject_id = ? ORDER BY id";
        return jdbcTemplate.query(sql, taskRowMapper, subjectId);
    }

    /** {@inheritDoc} */
    @Override
    public void insert(Long subjectId, String title, String status, LocalDate deadline, String reflection) {
        String sql = "INSERT INTO TASK (subject_id, title, completed, status, deadline, reflection) VALUES (?, ?, FALSE, ?, ?, ?)";
        jdbcTemplate.update(sql, subjectId, title, status, deadline, reflection);
    }

    /** {@inheritDoc} */
    @Override
    public void updateCompleted(Long taskId, boolean completed) {
        String sql = "UPDATE TASK SET completed = ? WHERE id = ?";
        jdbcTemplate.update(sql, completed, taskId);
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
        String sql = "SELECT COUNT(*) FROM TASK WHERE subject_id = ? AND completed = TRUE";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, subjectId);
        return count != null ? count : 0;
    }

    /** {@inheritDoc} */
    @Override
    public void updateStatus(Long taskId, String status, boolean completed) {
        String sql = "UPDATE TASK SET status = ?, completed = ? WHERE id = ?";
        jdbcTemplate.update(sql, status, completed, taskId);
    }

}
