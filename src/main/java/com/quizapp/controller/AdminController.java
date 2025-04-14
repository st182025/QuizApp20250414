package com.quizapp.controller;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.quizapp.entity.AdminUserEntity;
import com.quizapp.entity.QuizEntity;
import com.quizapp.repository.AdminUserRepository;
import com.quizapp.service.QuizService;

/**
 * 管理者用のコントローラ。ログイン、ログアウト、ダッシュボード表示などを管理する。
 */
@Controller
public class AdminController {

    private final AdminUserRepository adminUserRepository;
    private final QuizService quizService;

    /**
     * コンストラクタインジェクション
     */
    @Autowired
    public AdminController(AdminUserRepository adminUserRepository, QuizService quizService) {
        this.adminUserRepository = adminUserRepository;
        this.quizService = quizService;
    }

    /**
     * 管理者ログインフォームを表示する
     * @return admin_login.html を返す
     */
    @GetMapping("/admin/login")
    public String showLoginForm() {
        return "admin_login"; 
    }

    /**
     * 管理者ログイン処理
     * 入力されたユーザー名・パスワードを検証し、成功すればセッションに管理者を保存する。
     *
     * @param username 入力ユーザー名
     * @param password 入力パスワード
     * @param session HttpSession（ログイン状態保持用）
     * @param model エラーメッセージ表示用モデル
     * @return ログイン成功→ダッシュボード、失敗→ログイン画面
     */
    @PostMapping("/admin/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        AdminUserEntity user = adminUserRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            session.setAttribute("adminUser", user); // ログイン成功時にセッション保存
            return "redirect:/admin/dashboard"; // 管理画面へ遷移
        } else {
            model.addAttribute("loginError", "ユーザー名またはパスワードが違います");
            return "admin_login";
        }
    }

    /**
     * ログアウト処理。セッションを破棄しホーム画面へ戻す。
     * @param session ログインセッション
     * @return /home にリダイレクト
     */
    @GetMapping("/admin/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/home";
    }

    /**
     * 管理者ダッシュボード表示
     * ログイン済みの管理者のみアクセス可能。クイズ一覧を表示する。
     *
     * @param session 管理者セッション確認用
     * @param model クイズリストとユーザー情報を渡す
     * @return admin_dashboard.html
     */
    @GetMapping("/admin/dashboard")
    public String showAdminDashboard(HttpSession session, Model model) {
        AdminUserEntity user = (AdminUserEntity) session.getAttribute("adminUser");
        if (user == null) {
            return "redirect:/admin/login"; // 未ログインならログイン画面へ
        }

        List<QuizEntity> quizList = quizService.getAllQuestions();
        model.addAttribute("quizList", quizList); // 全クイズを取得して渡す
        model.addAttribute("adminUser", user); // 管理者情報を渡す

        return "admin_dashboard";
    }
}
