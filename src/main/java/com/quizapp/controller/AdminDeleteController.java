/* =====================
   AdminDeleteController（完全版）
   ===================== */
package com.quizapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.quizapp.entity.AdminTrashEntity;
import com.quizapp.entity.QuizEntity;
import com.quizapp.repository.AdminTrashRepository;
import com.quizapp.repository.QuizRepository;

@Controller
public class AdminDeleteController {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private AdminTrashRepository adminTrashRepository;

    // === 削除確認画面 ===
    @GetMapping("/admin/delete/{id}")
    public String showDeleteConfirm(@PathVariable Long id, Model model) {
        QuizEntity quiz = quizRepository.findById(id).orElse(null);
        if (quiz == null) {
            return "redirect:/admin/dashboard";
        }
        model.addAttribute("quiz", quiz);
        return "admin/admin_delete";
    }

    // === ゴミ箱へ移動（論理削除） ===
    @PostMapping("/admin/delete")
    public String moveToTrash(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        QuizEntity quiz = quizRepository.findById(id).orElse(null);
        if (quiz != null) {
            AdminTrashEntity trash = new AdminTrashEntity();
            trash.setCategory(quiz.getCategory());
            trash.setQuestionText(quiz.getQuestionText());
            trash.setOption_1(quiz.getOption_1());
            trash.setOption_2(quiz.getOption_2());
            trash.setOption_3(quiz.getOption_3());
            trash.setOption_4(quiz.getOption_4());
            trash.setCorrectAnswer(quiz.getCorrectAnswer());
            trash.setExplanation(quiz.getExplanation());
            adminTrashRepository.save(trash);
            quizRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "ゴミ箱へ移動しました！");
        }
        return "redirect:/admin/delete/complete";
    }

    // === ゴミ箱移動完了後：モーダル表示 ===
    @GetMapping("/admin/delete/complete")
    public String showDeleteSuccess(Model model) {
        return "admin/admin_delete"; // 同じテンプレートを使ってモーダル表示
    }
}
