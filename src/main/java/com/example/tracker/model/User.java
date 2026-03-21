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
 *     id       BIGINT AUTO_INCREMENT PRIMARY KEY,
 *     username VARCHAR(255) NOT NULL,
 *.    password VARCHAR(255) NOT NULL
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
   
    /**　ユーザー名 */
    private String username;
   
    /** ハッシュ化されたユーザーパスワード */
    private String password;
    
    /**
     * ユーザーIDを返す
     * 
     * @return　ユーザーID
     */
    public Long getId() {
        return id;
    }

    /**
     * ユーザーIDを設定する
     * 
     * @param ユーザーID
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
     * @param ユーザー名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * パスワードを返す
     * 
     * @return
     */
    public String getPassword() {
        return password;
    }

    /**
     * パスワードを設定する
     * 
     * @param
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
