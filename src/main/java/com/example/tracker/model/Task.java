package com.example.tracker.model;

import java.time.LocalDate;

/**
 * タスク（Task）を表すドメインクラス。
 *
 * <p>データベースの {@code TASK} テーブルに対応するPOJO。
 * 各タスクは1つの {@link Subject 科目} に紐づき、タイトルと完了状態を持つ。</p>
 *
 * <h3>対応テーブル</h3>
 * <pre>
 * CREATE TABLE TASK (
 *     id         BIGINT AUTO_INCREMENT PRIMARY KEY,
 *     subject_id BIGINT NOT NULL,
 *     title      VARCHAR(255) NOT NULL,
 *     completed_id INT DEFAULT 1,
 *     deadline DATE,
 *     comment VARCHAR(255),
 *     FOREIGN KEY (subject_id) REFERENCES SUBJECT(id) ON DELETE CASCADE
 *     FOREIGN KEY (completed_id) REFERENCES COMPLETE(completed_id)
    );
 * );
 * </pre>
 *
 * @author tracker-team
 * @version 1.0
 * @since 1.0
 * @see Subject
 */

public class Task {

    /** タスクID（主キー、自動採番） */
    private Long id;

    /** 所属する科目のID（外部キー） */
    private Long subjectId;

    /** タスクのタイトル */
    private String title;

    /** 完了フラグID（{@code 1}: 未完了、{@code 2}: 進行中、{@code 3} : 完了） */
    private int completedId;
    
    /** タスクの完了フラグ */
    private String statusName;
    
    /** タスクの期限 */
    private LocalDate deadline;
    
    /** タスクのコメント */
    private String comment;
    
    /**
     * デフォルトコンストラクタ。
     */
    public Task(){
    }
        /**
     * 全フィールド指定コンストラクタ。
     * @param id タスクID
     * @param subjectId 科目ID
     * @param title タスクのタイトル
     * @param completedId 完了フラグID（1:未完了、2:進行中、3:完了）
     * @param deadline タスクの期限
     * @param comment タスクのコメント
     */
    public Task(Long id, Long subjectId, String title, int completedId) {
        this.id = id;
        this.subjectId = subjectId;
        this.title = title;
        this.completedId = completedId;
    }
    /**
     * タスクIDを返す。
     *
     * @return タスクID
     */
    public Long getId() {
        return id;
    }

    /**
     * タスクIDを設定する。
     *
     * @param id タスクID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 所属する科目のIDを返す。
     *
     * @return 科目ID
     */
    public Long getSubjectId() {
        return subjectId;
    }

    /**
     * 所属する科目のIDを設定する。
     *
     * @param subjectId 科目ID
     */
    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    /**
     * タスクのタイトルを返す。
     *
     * @return タスクタイトル
     */
    public String getTitle() {
        return title;
    }

    /**
     * タスクのタイトルを設定する。
     *
     * @param title タスクタイトル
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 完了IDを返す。
     *
     * @return 未完了の場合は {@code 1}、進行中の場合は{@code 2}、完了している場合は {@code 3}
     */
    public int getCompletedId() {
        return completedId;
    }

    /**
     * 完了IDを設定する。
     *
     * @param completed {@code 1} で未完了、{@code 2}で進行中、{@code 3} で完了
     */
    public void setCompletedId(int completedId) {
        this.completedId = completedId;
    }

    /**
     * 完了状態を返す。
     *
     * @return 完了状態を返す
     */
    public String getStatusName() {
        return statusName;
    }
    
    /**
     * 完了状態を設定する。
     *
     * @param statusName 完了状態
     */
    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
    
    /**
     * 期限を返す。
     *
     * @return LocalDate型で期限の日付を返す
     */
    public LocalDate getDeadline(){
        return deadline;
    }

    /**
     * 期限を設定する。
     *
     * @param deadline LocalDate型で期限の日付を設定する
     */
    public void setDeadline(LocalDate deadline){
        this.deadline = deadline;
    }
    
    /**
     * タスクのコメントを返す。
     *
     * @return タスクのコメント
     */
    public String getComment(){
        return comment;
    }
    
    /**
     * タスクのコメントを設定する。
     *
     * @param comment タスクのコメント
     */
    public void setComment(String comment){
        this.comment = comment;
    }

    /**
     * このオブジェクトの文字列表現を返す。
     *
     * @return {@code Task{id=..., subjectId=..., title='...', completed_id=..., deadline=..., comment="..."}} 形式の文字列
     */
    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", subjectId=" + subjectId +
                ", title='" + title + '\'' +
                ", completed_id=" + completedId +
                ", deadline=" + deadline +
                ", comment=" + comment +
                '}';
    }
}
