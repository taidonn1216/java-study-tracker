package com.example.tracker.model;

/**
 * SubjectSummary (科目サマリー) クラス
 * 科目情報とそのタスク統計を保持する
 */
public class SubjectSummary {
    private Subject subject;
    private int totalTasks;
    private int completedTasks;
    
    public SubjectSummary(Subject subject, int totalTasks, int completedTasks) {
        this.subject = subject;
        this.totalTasks = totalTasks;
        this.completedTasks = completedTasks;
    }
    
    public Subject getSubject() {
        return subject;
    }
    
    public void setSubject(Subject subject) {
        this.subject = subject;
    }
    
    public int getTotalTasks() {
        return totalTasks;
    }
    
    public void setTotalTasks(int totalTasks) {
        this.totalTasks = totalTasks;
    }
    
    public int getCompletedTasks() {
        return completedTasks;
    }
    
    public void setCompletedTasks(int completedTasks) {
        this.completedTasks = completedTasks;
    }
    
    /**
     * 進捗率を計算する(0-100の整数値)
     */
    public int getProgressPercentage() {
        if (totalTasks == 0) {
            return 0;
        }
        return (int) ((double) completedTasks / totalTasks * 100);
    }
}
