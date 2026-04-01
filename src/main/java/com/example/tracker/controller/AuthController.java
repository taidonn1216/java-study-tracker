package com.example.tracker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 認証関連画面を提供するコントローラー。
 * 
 * <p>このクラスはログイン画面の表示のみを担当し、
 * ユーザー名・パスワードの検証(認証処理)はSpring Securityが担当する。</p> 
 */
@Controller
public class AuthController {
    /**
     * ログイン画面を表示する。
     * 
     * <p>HTTP GET {@code /login} を受け取り、Thymeleefテンプレート
     * {@code login.html} を返す。</p> 
     * 
     * @return ビュー名
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }
        
}
