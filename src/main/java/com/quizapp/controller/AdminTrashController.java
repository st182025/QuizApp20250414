package com.quizapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.quizapp.entity.AdminTrashEntity;
import com.quizapp.entity.QuizEntity;
import com.quizapp.repository.AdminTrashRepository;
import com.quizapp.repository.QuizRepository;

@Controller
public class AdminTrashController {

    @Autowired
    private AdminTrashRepository adminTrashRepository;

    @Autowired
    private QuizRepository quizRepository;

    // ========== 【ゴミ箱一覧ページ】 ==========
    @GetMapping("/admin/trash")
    public String showTrashList(Model model, @RequestParam(name = "message", required = false) String message) {
        List<AdminTrashEntity> trashList = adminTrashRepository.findAll();
        model.addAttribute("trashList", trashList);
        if (message != null && !message.isEmpty()) {
            model.addAttribute("message", message);
        }
        return "admin/admin_trash";
    }

    // ========== 【復元処理】 ==========
    @GetMapping("/admin/trash/restore/{id}")
    public String restoreFromTrash(@PathVariable Long id) {
        AdminTrashEntity trash = adminTrashRepository.findById(id).orElse(null);
        if (trash != null) {
            QuizEntity quiz = new QuizEntity();
            quiz.setCategory(trash.getCategory());
            quiz.setQuestionText(trash.getQuestionText());
            quiz.setOption_1(trash.getOption_1());
            quiz.setOption_2(trash.getOption_2());
            quiz.setOption_3(trash.getOption_3());
            quiz.setOption_4(trash.getOption_4());
            quiz.setCorrectAnswer(trash.getCorrectAnswer());
            quiz.setExplanation(trash.getExplanation());

            quizRepository.save(quiz);              // 復元先テーブルに追加
            adminTrashRepository.deleteById(id);    // ゴミ箱から削除
            return "redirect:/admin/trash?message=復元しました！";
        }
        return "redirect:/admin/trash?message=復元に失敗しました。";
    }

    // ========== 【完全削除処理】 ==========
    @GetMapping("/admin/trash/permanent-delete/{id}")
    public String deletePermanently(@PathVariable Long id) {
        if (adminTrashRepository.existsById(id)) {
            adminTrashRepository.deleteById(id);
            return "redirect:/admin/trash?message=完全に削除しました。";
        }
        return "redirect:/admin/trash?message=削除に失敗しました。";
    }
}