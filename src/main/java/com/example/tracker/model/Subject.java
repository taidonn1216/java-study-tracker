package com.example.tracker.model;

/**
 * 科目（Subject）を表すドメインクラス。
 *
 * <p>データベースの {@code SUBJECT} テーブルに対応するPOJO。
 * 科目の識別子 ({@code id}) と科目名 ({@code name}) を保持する。</p>
 *
 * <h3>対応テーブル</h3>
 * <pre>
 * CREATE TABLE SUBJECT (
 *     id   BIGINT AUTO_INCREMENT PRIMARY KEY,
 *     name VARCHAR(255) NOT NULL
 * );
 * </pre>
 *
 * @author tracker-team
 * @version 1.0
 * @since 1.0
 * @see Task
 * @see SubjectSummary
 */
public class Subject {

    /** 科目ID（主キー、自動採番） */
    private Long id;

    /** 科目名 */
    private String name;

    /**
     * デフォルトコンストラクタ。
     */
    public Subject() {
    }

    /**
     * 全フィールド指定コンストラクタ。
     *
     * @param id   科目ID
     * @param name 科目名
     */
    public Subject(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * 科目IDを返す。
     *
     * @return 科目ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 科目IDを設定する。
     *
     * @param id 科目ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 科目名を返す。
     *
     * @return 科目名
     */
    public String getName() {
        return name;
    }

    /**
     * 科目名を設定する。
     *
     * @param name 科目名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * このオブジェクトの文字列表現を返す。
     *
     * @return {@code Subject{id=..., name='...'}} 形式の文字列
     */
    @Override
    public String toString() {
        return "Subject{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
