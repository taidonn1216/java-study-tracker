package com.example.tracker.repository;

import com.example.tracker.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * {@link UserRepository} のJDBC実装クラス。
 * 
 * <p>Spring {@link JdbcTemplate} を使用して {@code USERS} テーブルに対する操作を実行する。
 * {@code @Repository} としてSpring DIコンテナに登録される</p>
 * 
 * @author tracker-team
 * @version 1.0
 * @since 1.0
 * @see UserRepository
 */
@Repository
public class UserRepositoryImpl implements UserRepository {

    /** SQL実行用のSpring JdbcTemplate */
    private final JdbcTemplate jdbcTemplate;

    /**
     * {@link java.sql.ResultSet} の行を {@link User} オブジェクトに変換する {@link RowMapper}
     */
    private final RowMapper<User> userRowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setRole(rs.getString("role"));
        return user;
    };

     /**
      * コンストラクタインジェクション
      * 
      * @param jdbcTemplate Springが提供する {@link JdbcTemplate} インスタンス
      */
     public UserRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
     }

     /** {@inheritDoc} */
    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT id, username, password, role FROM USERS WHERE username = ?";
        List<User> users = jdbcTemplate.query(sql,userRowMapper,username);
        return users.stream().findFirst();
    }
    
    /** {@inheritDoc} */
    @Override
    public void insert(String username, String password) {
        String sql = "INSERT INTO USERS (username, password) VALUES (?, ?)";
        jdbcTemplate.update(sql, username, password);
    }

    /** {@inheritDoc} */
    @Override
    public List<User> findAll() {
        String sql = "SELECT id, username, password,role FROM USERS";
        return jdbcTemplate.query(sql, userRowMapper);
    }

    /** {@inheritDoc} */
    @Override
    public void updateRole(Long id, String role) {
        String sql = "UPDATE USERS SET role = ? WHERE id = ?";
        jdbcTemplate.update(sql, role, id);
    }

    /** {@inheritDoc} */
    @Override
    public User findById(Long id) {
        String sql = "SELECT id, username, password, role FROM USERS WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, userRowMapper, id);
    }
}
