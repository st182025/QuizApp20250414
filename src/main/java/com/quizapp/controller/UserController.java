package com.quizapp.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.quizapp.entity.QuizEntity;
import com.quizapp.entity.UserEntity;
import com.quizapp.repository.QuizRepository;
import com.quizapp.repository.UserRepository;

@Controller
public class UserController {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * ユーザーのマイページ表示
     */
    @GetMapping("/user/mypage")
    public String showMypage(HttpSession session, Model model) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

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

    /**
     * 登録フォーム表示
     */
    @GetMapping("/register")
    public String showRegisterForm() {
        return "user/register";
    }

    /**
     * Ajax用：ユーザー名重複チェック
     */
    @GetMapping("/check-username")
    @ResponseBody
    public String checkUsername(@RequestParam String username) {
        return userRepository.findByUsername(username) != null ? "true" : "false";
    }

    /**
     * 登録処理：空欄・重複チェック
     */
    @PostMapping("/register")
    public String registerUser(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String profile,
            Model model) {

        boolean hasError = false;

        if (username == null || username.isBlank()) {
            model.addAttribute("usernameError", "必須項目です。入力してください。");
            hasError = true;
        }
        if (password == null || password.isBlank()) {
            model.addAttribute("passwordError", "必須項目です。入力してください。");
            hasError = true;
        }

        if (!hasError && userRepository.findByUsername(username) != null) {
            model.addAttribute("usernameError", "そのユーザー名は使用されています。");
            hasError = true;
        }

        if (hasError) {
            model.addAttribute("username", username);
            model.addAttribute("email", email);
            model.addAttribute("profile", profile);
            return "user/register";
        }

        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setProfile(profile);
        userRepository.save(user);

        model.addAttribute("saved", true);
        return "user/register";
    }
}
