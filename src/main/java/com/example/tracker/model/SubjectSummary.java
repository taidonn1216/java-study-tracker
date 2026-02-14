package com.example.tracker.model;

/**
 * 科目サマリー（SubjectSummary）クラス。
 *
 * <p>{@link Subject 科目} の情報と、紐づくタスクの統計情報（総数・完了数）を
 * まとめて保持する。科目一覧画面で進捗率を表示するために使用される。</p>
 *
 * @author tracker-team
 * @version 1.0
 * @since 1.0
 * @see Subject
 * @see Task
 */
public class SubjectSummary {

    /** 科目エンティティ */
    private Subject subject;

    /** 科目に紐づくタスクの総数 */
    private int totalTasks;

    /** 科目に紐づく完了済みタスク数 */
    private int completedTasks;

    /**
     * 科目とタスク統計情報を指定してインスタンスを生成する。
     *
     * @param subject        科目エンティティ
     * @param totalTasks     タスク総数
     * @param completedTasks 完了済みタスク数
     */
    public SubjectSummary(Subject subject, int totalTasks, int completedTasks) {
        this.subject = subject;
        this.totalTasks = totalTasks;
        this.completedTasks = completedTasks;
    }

    /**
     * 科目エンティティを返す。
     *
     * @return 科目
     */
    public Subject getSubject() {
        return subject;
    }

    /**
     * 科目エンティティを設定する。
     *
     * @param subject 科目
     */
    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    /**
     * タスク総数を返す。
     *
     * @return タスク総数
     */
    public int getTotalTasks() {
        return totalTasks;
    }

    /**
     * タスク総数を設定する。
     *
     * @param totalTasks タスク総数
     */
    public void setTotalTasks(int totalTasks) {
        this.totalTasks = totalTasks;
    }

    /**
     * 完了済みタスク数を返す。
     *
     * @return 完了済みタスク数
     */
    public int getCompletedTasks() {
        return completedTasks;
    }

    /**
     * 完了済みタスク数を設定する。
     *
     * @param completedTasks 完了済みタスク数
     */
    public void setCompletedTasks(int completedTasks) {
        this.completedTasks = completedTasks;
    }

    /**
     * 進捗率を計算して返す。
     *
     * <p>計算式: {@code completedTasks / totalTasks * 100}（小数点以下切り捨て）。
     * タスクが存在しない場合（{@code totalTasks == 0}）は {@code 0} を返す。</p>
     *
     * @return 進捗率（0〜100 の整数値）
     */
    public int getProgressPercentage() {
        if (totalTasks == 0) {
            return 0;
        }
        return (int) ((double) completedTasks / totalTasks * 100);
    }
}
