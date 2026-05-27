package com.example.tracker.model;

/**
 * ユーザー(User)を表すドメインクラス
 * 
 * <p>データベースの{@code USERS} テーブルに対応するPOJO。
 * ログイン画面認証に使用するユーザー名とパスワード(BCryptハッシュ)を保持する。</p>
 * 
 * <h3>対応テーブル</h3>
 * <pre>
 * CREATE TABLE USERS (
 *     id BIGINT AUTO_INCREMENT PRIMARY KEY,
 *     username VARCHAR(255) NOT NULL UNIQUE,
 *     password VARCHAR(255) NOT NULL,
 *     enabled BOOLEAN NOT NULL DEFAULT TRUE,
 *    role VARCHAR(20) NOT NULL DEFAULT 'GENERAL'
 * );
 * </pre>
 * 
 * @author tracker-team
 * @version 1.0
 * @since 1.0
 */

public class User {
    
    /** ユーザーID（主キー、自動採番） */
    private Long id;
   
    /** ユーザー名 */
    private String username;
   
    /** ハッシュ化されたユーザーパスワード */
    private String password;
    
    /** ユーザーの権限区分 ("GENERAL" または "ADMIN") */
    private String role;
    
    /**
     * ユーザーIDを返す
     * 
     * @return ユーザーID
     */
    public Long getId() {
        return id;
    }

    /**
     * ユーザーIDを設定する
     * 
     * @param id ユーザーID
     */
    public void setId(Long id) {
        this.id = id;
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
     * ユーザー名を設定する
     * 
     * @param username ユーザー名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * パスワードを返す
     * 
     * @return BCryptハッシュ化されたパスワード
     */
    public String getPassword() {
        return password;
    }

    /**
     * パスワードを設定する
     * 
     * @param password BCryptハッシュ化されたパスワード
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
    /**
     * 権限区分を取得する
     * 
     * @return 権限区分 ("GENERAL" または "ADMIN")
     */
    public String getRole() {
        return role;
    }
    
    /**
     * 権限区分を設定する
     * 
     * @param role 権限区分 ("GENERAL" または "ADMIN")
     */
    public void setRole(String role) {
        this.role = role;
    }
}
