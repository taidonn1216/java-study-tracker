package com.example.tracker.repository;

import com.example.tracker.model.Task;
import com.example.tracker.model.TaskStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * {@link TaskRepository} の JDBC 実装クラス。
 *
 * <p>
 * Spring {@link JdbcTemplate} を使用して {@code TASK} テーブルに
 * 対するCRUD操作および統計クエリを実行する。
 * {@code @Repository} としてSpring DIコンテナに登録される。
 * </p>
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
        task.setStatus(TaskStatus.fromValue(rs.getString("status")));
        task.setDeadline(rs.getObject("deadline", LocalDate.class));
        String reflection = rs.getString("reflection");
        task.setReflection(reflection != null ? reflection : "");
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
    public List<Task> findBySubjectIdAndUserId(Long subjectId, Long userId) {
        String sql = """
                SELECT t.id, t.subject_id, t.title, t.status, t.deadline, t.reflection
                FROM TASK t
                JOIN SUBJECT s ON s.id = t.subject_id
                WHERE t.subject_id = ? AND s.user_id = ?
                ORDER BY t.id;
                """;
        return jdbcTemplate.query(sql, taskRowMapper, subjectId, userId);
    }

    /** {@inheritDoc} */
    @Override
    public List<Task> findBySubjectIdAndStatusAndUserId(Long subjectId, TaskStatus status, Long userId) {
        String sql = """
                SELECT t.id, t.subject_id, t.title, t.status, t.deadline, t.reflection
                FROM TASK t
                JOIN SUBJECT s ON s.id = t.subject_id
                WHERE t.subject_id = ? AND t.status = ? AND s.user_id = ?
                ORDER BY t.id
                """;
        return jdbcTemplate.query(sql, taskRowMapper, subjectId, status.name(), userId);
    }

    /** {@inheritDoc} */
    @Override
    public void insert(Long subjectId, String title, TaskStatus status, LocalDate deadline, String reflection) {
        String sql = "INSERT INTO TASK (subject_id, title, status, deadline, reflection) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, subjectId, title, status.name(), deadline, reflection);
    }

    /** {@inheritDoc} */
    @Override
    public int deleteByIdAndUserId(Long taskId, Long userId) {
        String sql = """
                DELETE FROM TASK t
                WHERE t.id = ?
                    AND EXISTS (
                        SELECT 1
                        FROM SUBJECT s
                        WHERE s.id = t.subject_id
                          AND s.user_id = ?
                    )
                """;
        return jdbcTemplate.update(sql, taskId, userId);
    }

    /** {@inheritDoc} */
    @Override
    public int updateStatusByIdAndUserId(Long taskId, TaskStatus status, Long userId) {
        String sql = """
                UPDATE TASK t
                SET status = ?
                WHERE t.id = ?
                  AND EXISTS (
                      SELECT 1
                      FROM SUBJECT s
                      WHERE s.id = t.subject_id
                        AND s.user_id = ?
                  )
                """;
        return jdbcTemplate.update(sql, status.name(), taskId, userId);
    }

    /** {@inheritDoc} */
    @Override
    public int updateReflectionByIdAndUserId(Long taskId, String reflection, Long userId) {
        String sql = """
                UPDATE TASK t
                SET reflection = ?
                WHERE t.id = ?
                  AND EXISTS (
                      SELECT 1
                      FROM SUBJECT s
                      WHERE s.id = t.subject_id
                        AND s.user_id = ?
                  )
                """;
        return jdbcTemplate.update(sql, reflection, taskId, userId);
    }

    /** {@inheritDoc} */
    @Override
    public List<Task> findOverdueTasksByUserId(Long userId, LocalDate today) {
        String sql = """
                SELECT t.id, t.subject_id, t.title, t.status, t.deadline, t.reflection
                FROM TASK t
                JOIN SUBJECT s ON s.id = t.subject_id
                WHERE s.user_id = ?
                  AND t.deadline < ?
                  AND t.status != 'DONE'
                ORDER BY t.deadline ASC
                """;
        return jdbcTemplate.query(sql, taskRowMapper, userId, today);
    }

    /** {@inheritDoc} */
    @Override
    public boolean existsByIdAndSubjectIdAndUserId(Long taskId, Long subjectId, Long userId) {
        String sql = """
                SELECT COUNT(*)
                FROM TASK t
                JOIN SUBJECT s ON s.id = t.subject_id
                WHERE t.id = ? AND t.subject_id = ? AND s.user_id = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, taskId, subjectId, userId);
        return count != null && count > 0;
    }
}
