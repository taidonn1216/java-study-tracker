package com.example.tracker.repository;

import com.example.tracker.model.Task;
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
     * @return タスクのリスト（空の場合は空リスト）
     */
    List<Task> findBySubjectId(Long subjectId);

    /**
     * 新しいタスクを未完了状態 ({@code completed = FALSE}) で登録する。
     *
     * @param subjectId タスクを追加する科目のID
     * @param title     タスクのタイトル（NULL不可）
     */
    void insert(Long subjectId, String title);

    /**
     * タスクの完了状態を更新する。
     *
     * @param taskId    更新対象のタスクID
     * @param completed 新しい完了状態（{@code true}: 完了、{@code false}: 未完了）
     */
    void updateCompleted(Long taskId, boolean completed);

    /**
     * 指定したIDのタスクを削除する。
     *
     * @param taskId 削除対象のタスクID
     */
    void deleteById(Long taskId);

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
}
