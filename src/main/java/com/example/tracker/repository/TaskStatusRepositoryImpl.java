package com.example.tracker.repository;

import com.example.tracker.model.TaskStaus;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TaskStatusRepositoryImpl implements TaskStatusRepository{
    /** SQL実行用のSpring JdbcTemplate */
    private final JdbcTemplate jdbcTemplate;
    
    /**
     * コンストラクタインジェクション。
     *
     * @param jdbcTemplate Springが提供する {@link JdbcTemplate} インスタンス
     */
    public TaskStatusRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

     /**
     * {@link java.sql.ResultSet} の行を {@link TaskStatus} オブジェクトに変換する {@link RowMapper}。
     */
    private final RowMapper<TaskStaus> completed = (rs, rowNum) -> {
        TaskStaus taskStaus = new TaskStaus();
        taskStaus.setStatus(rs.getString("completed"));
        return taskStaus;
    };


    /** {@inheritDoc} */
    public List<TaskStaus> taskStatusRefarence(int completedId){
        String sql ="SELECT completed FROM COMPLETE WHERE completed_id = ?";
        return jdbcTemplate.query(sql, completed, completedId); 
    }

     /** {@inheritDoc} */
    public void insertTaskStatus(){
        String sql1 = "INSERT INTO COMPLETE (completed) SELECT ('未完了') WHERE NOT EXISTS ( SELECT completed FROM COMPLETE WHERE completed = '未完了')";
        String sql2 = "INSERT INTO COMPLETE (completed) SELECT ('進行中') WHERE NOT EXISTS ( SELECT completed FROM COMPLETE WHERE completed = '進行中')";
        String sql3 = "INSERT INTO COMPLETE (completed) SELECT ('完了') WHERE NOT EXISTS ( SELECT completed FROM COMPLETE WHERE completed = '完了')";
        jdbcTemplate.update(sql1);
        jdbcTemplate.update(sql2);
        jdbcTemplate.update(sql3);
    }
}