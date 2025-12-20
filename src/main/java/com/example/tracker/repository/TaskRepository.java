package com.example.tracker.repository;

import com.example.tracker.model.Task;
import java.util.List;

/**
 * Task Repository インターフェース
 * タスクデータへのアクセスを抽象化
 */
public interface TaskRepository {
    
    /**
     * 指定した科目IDに紐づくすべてのタスクを取得する
     */
    List<Task> findBySubjectId(Long subjectId);
    
    /**
     * 新しいタスクを登録する
     */
    void insert(Long subjectId, String title);
    
    /**
     * タスクの完了状態を更新する
     */
    void updateCompleted(Long taskId, boolean completed);
    
    /**
     * タスクを削除する
     */
    void deleteById(Long taskId);
    
    /**
     * 指定した科目のタスク総数を取得する
     */
    int countBySubjectId(Long subjectId);
    
    /**
     * 指定した科目の完了済みタスク数を取得する
     */
    int countCompletedBySubjectId(Long subjectId);
}
