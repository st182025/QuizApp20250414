package com.quizapp.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LogoutController {

    /**
     * ログアウト処理（セッション無効化）
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();  // セッションを破棄（ログアウト）
        return "redirect:/home"; // ログイン前のhomeにリダイレクト
    }
}
