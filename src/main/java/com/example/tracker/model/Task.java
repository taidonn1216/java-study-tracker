package com.example.tracker.model;

/**
 * Task (タスク) ドメインクラス
 * データベースのTASKテーブルに対応するPOJO
 */
public class Task {
    private Long id;
    private Long subjectId;
    private String title;
    private Boolean completed;
    
    // コンストラクタ
    public Task() {
    }
    
    public Task(Long id, Long subjectId, String title, Boolean completed) {
        this.id = id;
        this.subjectId = subjectId;
        this.title = title;
        this.completed = completed;
    }
    
    // Getter/Setter
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getSubjectId() {
        return subjectId;
    }
    
    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public Boolean getCompleted() {
        return completed;
    }
    
    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }
    
    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", subjectId=" + subjectId +
                ", title='" + title + '\'' +
                ", completed=" + completed +
                '}';
    }
}
