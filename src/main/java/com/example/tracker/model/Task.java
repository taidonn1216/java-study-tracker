package com.example.tracker.model;

import java.time.LocalDate;

/**
 * タスク（Task）を表すドメインクラス。
 *
 * <p>データベースの {@code TASK} テーブルに対応するPOJO。</p>
 *
 * <h3>対応テーブル</h3>
 * <pre>
 * CREATE TABLE TASK (
 *     id BIGINT AUTO_INCREMENT PRIMARY KEY,
 *     subject_id BIGINT NOT NULL,
 *     title VARCHAR(255) NOT NULL,
 *     status VARCHAR(20) NOT NULL,
 *     deadline DATE,
 *     reflection TEXT,
 *     FOREIGN KEY (subject_id) REFERENCES SUBJECT(id) ON DELETE CASCADE
 * );
 * </pre>
 *
 * @author tracker-team
 * @version 1.0
 * @since 1.0
 * @see Subject
 * @see TaskStatus
 */
public class Task {

    /** タスクID（主キー、自動採番） */
    private Long id;

    /** 所属する科目のID（外部キー） */
    private Long subjectId;

    /** タスクのタイトル */
    private String title;

    /** タスクのステータス(未着手、進行中、完了) */
    private TaskStatus status;

    /** タスクの期限日 */
    private LocalDate deadline;

    /** タスク完了時の学び・振り返り */
    private String reflection;



    /**
     * デフォルトコンストラクタ。
     */
    public Task() {
    }

    /**
     * 全フィールド指定コンストラクタ。
     *
     * @param id タスクID
     * @param subjectId 所属する科目のID
     * @param title タスクのタイトル
     * @param status ステータス
     * @param deadline 期限日
     * @param reflection 振り返り
     */
    public Task(Long id, Long subjectId, String title, TaskStatus status, LocalDate deadline, String reflection) {
        this.id = id;
        this.subjectId = subjectId;
        this.title = title;
        this.status = status;
        this.deadline = deadline;
        this.reflection = reflection;
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
     * このオブジェクトの文字列表現を返す。
     *
     * @return {@code Task{id=..., subjectId=..., title='...', status='...', deadline='...', reflection='...'}} 形式の文字列
     */
    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", subjectId=" + subjectId +
                ", title='" + title + '\'' +
                ", status='" + status +  '\'' +
                ", deadline=" + deadline +
                ", reflection='" + reflection + '\'' +
                '}';
    }

    /**
     * ステータスを返す。
     * 
     * @return ステータス (未着手・進行中・完了)いずれか
     */
    public TaskStatus getStatus() {
        return status;
    }

    /**
     * ステータスの設定を受け取る
     * 
     * @param status 設定するステータス
     */
    public void setStatus(TaskStatus status) {
        this.status = status;
    }
    
    /**
     * 期限日を返す。
     * 
     * @return 期限日 (設定されていない場合は {@code null})
     */
    public LocalDate getDeadline() {
        return deadline;
    }

    /**
     * 期限の設定を受け取る。
     * 
     * @param deadline 設定する期限日
     */
    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    /**
     * 完了時の学び・振り返りを返す。
     * 
     * @return 学び・振り返り
     */
    public String getReflection() {
        return reflection;
    }

    /**
     * 完了時の学び・振り返りの設定を受け取る。
     * 
     * @param reflection 設定する学び・振り返り
     */
    public void setReflection(String reflection) {
        this.reflection = reflection;
    }


}
