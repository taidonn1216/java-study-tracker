package com.example.tracker.repository;

import com.example.tracker.model.Subject;
import com.example.tracker.model.SubjectSummary;
import java.util.List;
import java.util.Optional;

/**
 * Subject Repository インターフェース
 * 科目データへのアクセスを抽象化
 */
public interface SubjectRepository {
    
    /**
     * すべての科目を取得する
     */
    List<Subject> findAll();
    
    /**
     * すべての科目をタスク統計付きで取得する
     */
    List<SubjectSummary> findAllWithTaskStats();
    
    /**
     * IDで科目を検索する
     */
    Optional<Subject> findById(Long id);
    
    /**
     * 新しい科目を登録する
     */
    void insert(String name);
    
    /**
     * 科目を削除する
     */
    void deleteById(Long id);
}
