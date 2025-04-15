package com.quizapp.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.quizapp.entity.QuizEntity;
import com.quizapp.entity.UserEntity;
import com.quizapp.repository.QuizRepository;

@Controller
public class UserController {

    @Autowired
    private QuizRepository quizRepository;

    /**
     * ユーザーのマイページ表示
     */
    @GetMapping("/user/mypage")
    public String showMypage(HttpSession session, Model model) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login"; // 未ログインならログインへ
        }

        // お気に入りクイズの取得
        String[] favoriteIds = user.getFavorites();
        List<QuizEntity> favoriteQuizzes = new ArrayList<>();

        if (favoriteIds != null && favoriteIds.length > 0) {
            List<Long> idList = Arrays.stream(favoriteIds)
                    .map(Long::parseLong)
                    .toList();

            favoriteQuizzes = quizRepository.findAllById(idList);
        }

        model.addAttribute("user", user);
        model.addAttribute("favoriteQuizzes", favoriteQuizzes);
        return "user/mypage";
    }
}
