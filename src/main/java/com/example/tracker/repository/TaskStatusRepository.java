package com.example.tracker.repository;

import com.example.tracker.model.TaskStaus;
import java.util.List;

/**
 * タスク進捗（COMPLETE）データへのアクセスを抽象化するリポジトリインターフェース。
 *
 * <p>{@code TASK} テーブルにCRUD操作および統計クエリを提供する。</p>
 *
 * @author tracker-team
 * @version 1.0
 * @since 1.0
 * @see TaskStatusRepositoryImpl
 * @see TaskStatus
 */
public interface TaskStatusRepository {
     /**
     * タスクの完了管理IDにと同じ完了フラグを取得する
     *
     * @param completeId 完了管理ID
     * @return 完了フラグを返す
     */
    List<TaskStaus> taskStatusRefarence(int completedId);
    
    /**
     *完了フラグテーブルの値がない場合、テーブルの値を挿入する 
     *
     */
    void insertTaskStatus();
    
} 
