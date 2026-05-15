package com.example.tracker.repository;

import com.example.tracker.model.Subject;
import com.example.tracker.model.SubjectSummary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * {@link SubjectRepository} の JDBC 実装クラス。
 *
 * <p>Spring {@link JdbcTemplate} を使用して {@code SUBJECT} テーブルに
 * 対するCRUD操作を実行する。{@code @Repository} としてSpring DIコンテナに登録される。</p>
 *
 * @author tracker-team
 * @version 1.0
 * @since 1.0
 * @see SubjectRepository
 */
@Repository
public class SubjectRepositoryImpl implements SubjectRepository {

    /** SQL実行用のSpring JdbcTemplate */
    private final JdbcTemplate jdbcTemplate;

    /**
     * {@link java.sql.ResultSet} の行を {@link Subject} オブジェクトに変換する {@link RowMapper}。
     */
    private final RowMapper<Subject> subjectRowMapper = (rs, rowNum) -> {
        Subject subject = new Subject();
        subject.setId(rs.getLong("id"));
        subject.setName(rs.getString("name"));
        return subject;
    };

    /**
     * コンストラクタインジェクション。
     *
     * @param jdbcTemplate Springが提供する {@link JdbcTemplate} インスタンス
     */
    public SubjectRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /** {@inheritDoc} */
    @Override
    public List<Subject> findAll() {
        String sql = "SELECT id, name FROM SUBJECT ORDER BY id";
        return jdbcTemplate.query(sql, subjectRowMapper);
    }

    /**
     * {@inheritDoc}
     *
     * <p>内部では以下のSQLを実行し、LEFT JOIN で TASK テーブルを集計する:</p>
     * <pre>
     * SELECT s.id, s.name,
     *        COUNT(t.id) as total_tasks,
     *        SUM(CASE WHEN t.status = 'DONE' THEN 1 ELSE 0 END) as completed_tasks
     * FROM SUBJECT s LEFT JOIN TASK t ON s.id = t.subject_id
     * WHERE s.user_id = ?
     * GROUP BY s.id, s.name ORDER BY s.id
     * </pre>
     * 
     */
    @Override
    public List<SubjectSummary> findAllWithTaskStatsByUserId(Long userId) {
        String sql = """
            SELECT 
                s.id, s.name,
                COUNT(t.id) as total_tasks,
                SUM(CASE WHEN t.status = 'DONE' THEN 1 ELSE 0 END) as completed_tasks
            FROM SUBJECT s
            LEFT JOIN TASK t ON s.id = t.subject_id
            WHERE s.user_id = ?
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
        }, userId);
    }
    
    /** {@inheritDoc} */
    @Override
    public Optional<Subject> findByIdAndUserId(Long id, Long userId) {
        String sql = "SELECT id, name FROM SUBJECT WHERE id = ? AND user_id = ?";
        List<Subject> results = jdbcTemplate.query(sql, subjectRowMapper, id, userId);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    /** {@inheritDoc} */
    @Override
    public void insert(String name, Long userId) {
        String sql = "INSERT INTO SUBJECT (name, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, name, userId);
    }

    /** {@inheritDoc} */
    @Override
    public int deleteByIdAndUserId(Long id, Long userId) {
        String sql = "DELETE FROM SUBJECT WHERE id = ? AND user_id = ?";
        return jdbcTemplate.update(sql, id, userId);
    }
}
