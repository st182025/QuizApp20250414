package com.quizapp.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.quizapp.entity.AdminUserEntity;
import com.quizapp.entity.UserEntity;
import com.quizapp.repository.AdminUserRepository;
import com.quizapp.repository.UserRepository;

/**
 * ログイン機能を管理する共通コントローラ。
 * 管理者・ユーザーのログイン処理およびログイン画面表示を担当。
 */
@Controller
public class LoginController {

    @Autowired
    private AdminUserRepository adminRepo;

    @Autowired
    private UserRepository userRepo;

    /**
     * ログイン処理（共通）
     * ログイン種別（role）に応じて管理者または一般ユーザーを判定し、セッションに保存。
     *
     * @param username 入力されたユーザー名
     * @param password 入力されたパスワード
     * @param role 管理者またはユーザーの区別
     * @param session ログイン情報を保持するセッション
     * @param model エラーメッセージなどを格納
     * @return ログイン成功時はダッシュボード等へ、失敗時はログイン画面に戻る
     */
    @PostMapping("/login")
    public String handleLogin(
        @RequestParam String username,
        @RequestParam String password,
        @RequestParam String role,
        HttpSession session,
        Model model
    ) {
        if ("admin".equals(role)) {
            AdminUserEntity admin = adminRepo.findByUsername(username);
            if (admin != null && admin.getPassword().equals(password)) {
                session.setAttribute("adminUser", admin);
                return "redirect:/admin/dashboard";
            }
        } else if ("user".equals(role)) {
            UserEntity user = userRepo.findByUsername(username);
            if (user != null && user.getPassword().equals(password)) {
                session.setAttribute("user", user);
                return "redirect:/home"; // 仮想パス
            }
        }

        model.addAttribute("loginError", "ユーザー名またはパスワードが正しくありません");
        return "login";
    }

    /**
     * ログイン画面の表示。
     *
     * @return login.html を返す
     */
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }
}
