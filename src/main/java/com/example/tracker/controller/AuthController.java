package com.example.tracker.controller;

import com.example.tracker.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 認証関連画面を提供するコントローラー。
 * 
 * <p>
 * ログイン画面・ユーザー登録の表示と、ユーザー登録処理を担当する。
 * ユーザー名・パスワードの検証(認証処理)はSpring Securityが担当する。
 * </p>
 * 
 * @author tracker-team
 * @since 1.0
 * @see UserRepository
 * @see PasswordEncoder
 */
@Controller
public class AuthController {

    /** ユーザー名の最小文字数 */
    private static final int MIN_USERNAME_LENGTH = 2;

    /** パスワードの最小文字数 */
    private static final int MIN_PASSWORD_LENGTH = 4;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 認証関連コントローラーを生成する。
     * 
     * <p>
     * ユーザー登録処理に必要なリポジトリとパスワードエンコーダーを受け取る。
     * </p>
     * 
     * @param userRepository  ユーザー情報の参照・登録を行うリポジトリ
     * @param passwordEncoder パスワードをハッシュ化するエンコーダー
     */
    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * ログイン画面を表示する。
     * 
     * <p>
     * HTTP GET {@code /login} を受け取り、Thymeleafテンプレート
     * {@code login.html} を返す。
     * </p>
     * 
     * @return ビュー名
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * ユーザー登録画面を表示する。
     * 
     * @return ビュー名 {@code "register"}
     */
    @GetMapping("/register")
    public String registerForm() {
        return "register";
    }

    /**
     * ユーザー登録を処理する。
     * 
     * <p>
     * ユーザー名・パスワードが空、または下限値文字数 (ユーザー名{@value #MIN_USERNAME_LENGTH}文字・
     * パスワード{@value #MIN_PASSWORD_LENGTH}文字) 未満の場合、
     * およびユーザー名が既に使われている場合、
     * エラーメッセージを表示して登録画面に戻る。
     * </p>
     * 
     * @param username           フォームから送信されたユーザー名
     * @param password           フォームから送信されたパスワード (平文)
     * @param redirectAttributes フラッシュメッセージを渡すための属性
     * @return 登録成功時は {@code "/login"} へリダイレクト、失敗時は登録画面へリダイレクト
     */
    @PostMapping("/register")
    public String register(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            RedirectAttributes redirectAttributes) {

        // ユーザー名の空・長さチェック
        if (username == null || username.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "ユーザー名を入力してください。");
            return "redirect:/register";
        }
        if (username.length() < MIN_USERNAME_LENGTH) {
            redirectAttributes.addFlashAttribute("errorMessage", "ユーザー名は" + MIN_USERNAME_LENGTH + "文字以上で入力してください。");
            return "redirect:/register";
        }

        // パスワードの空・長さチェック
        if (password == null || password.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "パスワードを入力してください。");
            return "redirect:/register";
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            redirectAttributes.addFlashAttribute("errorMessage", "パスワードは" + MIN_PASSWORD_LENGTH + "文字以上で入力してください。");
            return "redirect:/register";
        }

        // ユーザー名の重複チェック
        if (userRepository.findByUsername(username).isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "そのユーザー名はすでに使われています。");
            return "redirect:/register";
        }

        // パスワードをハッシュ化して登録する
        String hashedPassword = passwordEncoder.encode(password);
        userRepository.insert(username, hashedPassword);

        return "redirect:/login";
    }
}
