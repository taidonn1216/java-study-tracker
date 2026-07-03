package com.example.tracker.model;

/**
 * タスクの統計情報 (TaskStats) クラス。
 * 
 * <p>
 * 全タスクの総数・完了数。未完了数をまとめて保持する。
 * ダッシュボード等でタスク全体の進捗を表示するために使用される。
 * </p>
 * 
 * @author tracker-team
 * @version 1.0
 * @since 1.0
 * @see Task
 */
public class TaskStats {

    /** タスクの総数 */
    private final long total;

    /** 完了済みのタスク */
    private final long completed;

    /** 未完了のタスク */
    private final long incompleted;

    /**
     * タスク統計情報を指定してインスタンスを生成する。
     * 
     * @param total       タスクの総数
     * @param completed   完了済みのタスク数
     * @param incompleted 未完了のタスク数
     */
    public TaskStats(long total, long completed, long incompleted) {
        this.total = total;
        this.completed = completed;
        this.incompleted = incompleted;
    }

    /**
     * タスクの総数を返す。
     * 
     * @return タスクの総数
     */
    public long getTotal() {
        return total;
    }

    /**
     * 完了済みのタスクの総数を返す。
     * 
     * @return 完了済みのタスク数。
     */
    public long getCompleted() {
        return completed;
    }

    /**
     * 未完了のタスクを返す。
     * 
     * @return 未完了のタスク数
     */
    public long getIncompleted() {
        return incompleted;
    }
}
