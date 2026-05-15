package com.example.tracker.repository;

import com.example.tracker.model.Task;
import com.example.tracker.model.TaskStatus;
import java.time.LocalDate;
import java.util.List;

/**
 * タスク（Task）データへのアクセスを抽象化するリポジトリインターフェース。
 *
 * <p>{@code TASK} テーブルにCRUD操作および統計クエリを提供する。</p>
 *
 * @author tracker-team
 * @version 1.0
 * @since 1.0
 * @see TaskRepositoryImpl
 * @see Task
 */
public interface TaskRepository {

    /**
     * 指定した科目IDに紐づくすべてのタスクをID昇順で取得する。
     *
     * @param subjectId 科目ID
     * @param userId ログインユーザーID(所有者条件)
     * @return タスクのリスト（空の場合は空リスト）
     */
    List<Task> findBySubjectIdAndUserId(Long subjectId, Long userId);

    /**
     * 指定した科目IDとステータスに紐づくタスクを取得する。
     * 
     * @param subjectId 科目ID
     * @param status ステータス (未着手、進行中、完了)
     * @param userId ログインユーザーID(所有者条件)
     * @return 絞り込まれたタスクのリスト
     */
    List<Task> findBySubjectIdAndStatusAndUserId(Long subjectId, TaskStatus status, Long userId);
   
    /**
     * 指定した日付より期限が古く、かつ未完了のタスクを取得する。
     * 
     * @param userId ログインユーザーID(所有者条件)
     * @param today 現在の日付
     * @return 期限切れのタスクリスト(期限の古い順)
     */
    List<Task> findOverdueTasksByUserId(Long userId, LocalDate today);

   
    /**
     * 新しいタスクを登録する。
     *
     * @param subjectId タスクを追加する科目のID
     * @param title タスクのタイトル（NULL不可）
     * @param status タスクのステータス
     * @param deadline タスクの期限
     * @param reflection タスクの振り返り
     */
    void insert(Long subjectId, String title, TaskStatus status, LocalDate deadline, String reflection);

    /**
     * 指定した科目のタスク総数を取得する。
     *
     * @param subjectId 科目ID
     * @return タスク総数
     */
    int countBySubjectId(Long subjectId);

    /**
     * 指定した科目の完了済みタスク数を取得する。
     *
     * @param subjectId 科目ID
     * @return 完了済みタスク数
     */
    int countCompletedBySubjectId(Long subjectId);
    
    /**
     * タスクのステータスと完了状態を、所有者条件付きで更新する。
     * 
     * @param taskId 更新対象のタスクID
     * @param status 新しいステータス
     * @param userId ログインユーザーID(所有者条件)
     * @return 更新件数 (0: 対象なし / 1: 更新成功)
     */
    int updateStatusByIdAndUserId(Long taskId, TaskStatus status, Long userId);

    /**
     * 振り返り内容を、所有者条件付きで更新する。
     * 
     * @param taskId 更新対象のタスクID
     * @param reflection 新しい振り返り内容
     * @param userId ログインユーザーID(所有者条件)
     * @return 更新件数 (0: 対象なし / 1: 更新成功)
     */
    int updateReflectionByIdAndUserId(Long taskId, String reflection, Long userId);
    
    /**
     * 指定したタスクを、所有者条件付きで削除する。
     * 
     * @param taskId 削除対象のタスクID
     * @param userId ログインユーザーID(所有者条件)
     * @return 削除件数 (0: 対象なし / 1: 削除成功)
     */
    int deleteByIdAndUserId(Long taskId, Long userId);

    /**
     * 指定したタスクが条件を満たすのか判定する。
     * 
     * @param taskId タスクID
     * @param subjectId 科目ID
     * @param userId ログインユーザーID(所有者条件)
     * @return 条件を満たす場合は {@code true}
     */
    boolean existsByIdAndSubjectIdAndUserId(Long taskId, Long subjectId, Long userId);
}
