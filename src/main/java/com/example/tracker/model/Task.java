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
 *     completed  BOOLEAN DEFAULT FALSE,
 *     FOREIGN KEY (subject_id) REFERENCES SUBJECT(id) ON DELETE CASCADE
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

    /** 完了フラグ（{@code true}: 完了、{@code false}: 未完了） */
    private Boolean completed;

    /**　タスクのステータス(未着手、進行中、完了) */
    private String status;

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
     * @param id        　タスクID
     * @param subjectId 　所属する科目のID
     * @param title     　タスクのタイトル
     * @param completed 　完了フラグ
     * @param status      ステータス
     * @param deadline    期限日
     * @param reflection  振り返り
     */
    public Task(Long id, Long subjectId, String title, Boolean completed,String status, LocalDate deadline, String reflection) {
        this.id = id;
        this.subjectId = subjectId;
        this.title = title;
        this.completed = completed;
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
     * 完了状態を返す。
     *
     * @return 完了している場合は {@code true}、未完了の場合は {@code false}
     */
    public Boolean getCompleted() {
        return completed;
    }

    /**
     * 完了状態を設定する。
     *
     * @param completed {@code true} で完了、{@code false} で未完了
     */
    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    /**
     * このオブジェクトの文字列表現を返す。
     *
     * @return {@code Task{id=..., subjectId=..., title='...', completed=...}} 形式の文字列
     */
    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", subjectId=" + subjectId +
                ", title='" + title + '\'' +
                ", completed=" + completed +
                ", status='" + status + '\'' +
                ", deadline=" + deadline +
                ", reflection='" + reflection + '\'' +
                '}';
    }

    /**
     * ステータスを返す。
     * @return ステータス {未着手、進行中、完了いずれかを返す}
     */
    public String getStatus() {
        return status;
    }

    /**
     * ステータスの設定を受け取る
     * @param status 未着手、進行中、完了いずれかを受け取りstatusに代入する。
     */
    public void setStatus(String status) {
        this.status = status;
    }
    
    /**
     * 期限日を返す。
     * @return 期限日 {年・月・日を返す}
     */
    public LocalDate getDeadline() {
        return deadline;
    }

    /**
     * 期限の設定を受け取る。
     * @param deadline 年・月・日を受け取りdeadlineに代入する。
     */
    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    /**
     * 完了時の学び・振り返りを返す。
     * @return　書いてくれたもの {文字}
     */
    public String  getReflection() {
        return reflection;
    }

    /**
     * 完了時の学び・振り返りの設定を受け取る。
     * @param reflection 書いてくれたものをreflectionに代入する。
     */
    public void setReflection(String reflection) {
        this.reflection = reflection;
    }


}
