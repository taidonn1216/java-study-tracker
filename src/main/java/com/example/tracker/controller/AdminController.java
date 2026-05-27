package com.example.tracker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.security.core.Authentication;

import com.example.tracker.model.User;
import com.example.tracker.repository.UserRepository;

/**
 * 管理者向けの機能を提供するコントローラー。
 * 
 * <p>
 * ユーザー一覧の表示やロール変更などの管理機能を扱う。
 * </p>
 * 
 * @author tracker-team
 * @version 1.0
 * @since 1.0
 * @see UserRepository
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;

    /**
     * コンストラクタ。
     * 
     * @param userRepository ユーザー情報を取得するリポジトリ
     */
    public AdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 管理者トップページを表示する。
     * 全ユーザーの一覧を取得し、画面を渡す。
     * 
     * @param model 画面にデータを渡すためのオブジェクト
     * @return 管理者のトップページのテンプレート名
     */
    @GetMapping
    public String adminTop(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin/index";
    }

    /**
     * 指定されたユーザーの権限を変更する。
     * ログイン中のユーザー自身の権限は変更できない (自己ロックアウト防止)
     * 
     * @param id                 権限を変更する対象のユーザーのID
     * @param role               変更後の権限 ("ADMIN" または "GENERAL")
     * @param authentication     現在ログイン中のユーザー情報
     * @param redirectAttributes リダイレクト先にエラーメッセージを渡すための属性
     * @return 管理画面にリダイレクト
     */
    @PostMapping("/users/{id}/role")
    public String updateRole(@PathVariable Long id, @RequestParam String role, Authentication authentication,
            RedirectAttributes redirectAttributes) {
        User targetUser;
        try {
            targetUser = userRepository.findById(id);
        } catch (RuntimeException e) {
            return "redirect:/admin";
        }

        String currentUsername = authentication.getName();

        if (targetUser.getUsername().equals(currentUsername)) {
            redirectAttributes.addFlashAttribute("errorMessage", "自分自身の権限は変更できません。");
            return "redirect:/admin";
        }

        if (!role.equals("ADMIN") && !role.equals("GENERAL")) {
            redirectAttributes.addFlashAttribute("errorMessage", "不正なロールです。");
            return "redirect:/admin";
        }

        userRepository.updateRole(id, role);
        return "redirect:/admin";
    }

}
