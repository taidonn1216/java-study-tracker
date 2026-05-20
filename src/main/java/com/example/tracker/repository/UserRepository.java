package com.example.tracker.repository;

import com.example.tracker.model.User;
import java.util.Optional;
import java.util.List;

/**
 * ユーザー情報のデータアクセスインターフェイス。
 * 
 * <p>{@code USERS} テーブルに対するCRUD操作を定義する。</p>
 * 
 * @author tracker-team
 * @version 1.0
 * @since 1.0
 */

public interface UserRepository {
     
    /**
     * ユーザー名でユーザーを検索する。
     * 
     * @param username 検索するユーザー名
     * @return 該当するユーザー (存在しない場合は空の {@link Optional})
     */
    Optional<User> findByUsername(String username);
    
    /**
     * ユーザーを新規に登録する。
     * 
     * @param username ユーザー名
     * @param password ハッシュ化済みパスワード
     */
    void insert(String username, String password);

    /**
     * 全ユーザーを取得する
     * 
     * @return ユーザーの一覧
     */
    List<User> findAll();

    /**
     * 指定してユーザーの役割を更新する。
     * 
     * @param id   更新対象のユーザー
     * @param role 新しい役割
     */
    void updateRole(Long id, String role);
    
    /**
     * 指定されたユーザーを取得する。
     * 
     * @param id 取得するユーザーのID
     * @return 該当するユーザー情報
     */
    User findById(Long id);
}
