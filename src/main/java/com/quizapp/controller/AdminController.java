package com.quizapp.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // ← 正しい Model を使用
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.quizapp.entity.AdminUserEntity;
import com.quizapp.repository.AdminUserRepository;

@Controller
public class AdminController {

    private final AdminUserRepository adminUserRepository;

    @Autowired
    public AdminController(AdminUserRepository adminUserRepository) {
        this.adminUserRepository = adminUserRepository;
    }

    // ログインフォーム表示
    @GetMapping("/admin/login")
    public String showLoginForm() {
        return "admin_login"; 
    }

    // ログイン処理
    @PostMapping("/admin/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        AdminUserEntity user = adminUserRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            session.setAttribute("adminUser", user); // セッションに保存
            return "redirect:/admin/dashboard"; // ダッシュボードへ
        } else {
            model.addAttribute("loginError", "ユーザー名またはパスワードが違います");
            return "admin_login";
        }
    }

    // ログアウト処理
    @GetMapping("/admin/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // セッションを破棄
        return "redirect:/home";
    }

    // 管理者ダッシュボード（仮）
    @GetMapping("/admin/dashboard")
    public String showAdminDashboard(HttpSession session, Model model) {
        AdminUserEntity user = (AdminUserEntity) session.getAttribute("adminUser");
        if (user == null) {
            return "redirect:/admin/login";
        }
        model.addAttribute("adminUser", user);
        return "admin_dashboard"; // ← これから作成する画面
    }
}
