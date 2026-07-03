package com.example.tracker.model;

import java.time.LocalDateTime;

/**
 * ユーザーごとの学習進捗(UserProgress) を表すクラス。
 * 
 * <p>
 * 1ユーザー分の「完了タスク数」「未完了タスク数」「最終ログイン日時」 を保持する。
 * 管理者ダッシュボードで全ユーザーの進捗を一覧表示するために使用される。
 * </p>
 * 
 * <p>
 * このクラスは特定のテーブルには対応せず、
 * {@code USERS}・{@code SUBJECT}・{@code TASK} を結合・集計した結果を保持する。
 * </p>
 * 
 * @author tracker-team
 * @version 1.0
 * @since 1.0
 */
public class UserProgress {

    /** ユーザー名 */
    private final String username;

    /** 完了タスク数 */
    private final long completedCount;

    /** 未完了タスク数 */
    private final long incompletedCount;

    /** 最終ログイン日時 (未ログインの場合は null) */
    private final LocalDateTime lastLoginAt;

    /**
     * 学習進捗情報を指定してインスタンスを生成する。
     * 
     * @param username         ユーザー名
     * @param completedCount   完了タスク数
     * @param incompletedCount 未完了タスク数
     * @param lastLoginAt      最終ログイン日時 (未ログインの場合 null)
     */
    public UserProgress(String username, long completedCount, long incompletedCount, LocalDateTime lastLoginAt) {
        this.username = username;
        this.completedCount = completedCount;
        this.incompletedCount = incompletedCount;
        this.lastLoginAt = lastLoginAt;

    }

    /**
     * ユーザー名を返す
     * 
     * @return ユーザー名
     */
    public String getUsername() {
        return username;
    }

    /**
     * 全タスク(完了+未完了) を返す
     * 
     * @return 全タスク数
     */
    public long getTotalCount() {
        return completedCount + incompletedCount;
    }

    /**
     * 完了タスク数を返す
     * 
     * @return 完了タスク数
     */
    public long getCompletedCount() {
        return completedCount;
    }

    /**
     * 未完了タスク数を返す
     * 
     * @return 未完了タスク数
     */
    public long getIncompletedCount() {
        return incompletedCount;
    }

    /**
     * 最終ログイン日時を返す
     * 
     * @return 最終ログイン日時
     */
    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

}
