package com.example.tracker.model;

/**
 * Subject (科目) ドメインクラス
 * データベースのSUBJECTテーブルに対応するPOJO
 */
public class Subject {
    private Long id;
    private String name;
    
    // コンストラクタ
    public Subject() {
    }
    
    public Subject(Long id, String name) {
        this.id = id;
        this.name = name;
    }
    
    // Getter/Setter
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return "Subject{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
