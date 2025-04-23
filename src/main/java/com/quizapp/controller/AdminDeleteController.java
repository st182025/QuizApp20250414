package com.quizapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.quizapp.entity.AdminTrashEntity;
import com.quizapp.entity.QuizEntity;
import com.quizapp.repository.AdminTrashRepository;
import com.quizapp.repository.QuizRepository;

/**
 * 管理者用：クイズ削除（ゴミ箱移動）処理を行うコントローラ
 */
@Controller
public class AdminDeleteController {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private AdminTrashRepository adminTrashRepository;

    /**
     * クイズ削除（ゴミ箱移動）前の確認画面を表示
     *
     * @param id 削除対象のクイズID
     * @param model モデル属性
     * @return 削除確認画面
     */
    @GetMapping("/admin/delete/{id}")
    public String showDeleteConfirm(@PathVariable Long id, Model model) {
        QuizEntity quiz = quizRepository.findById(id).orElse(null);
        if (quiz == null) {
            return "redirect:/admin/dashboard";
        }
        model.addAttribute("quiz", quiz);
        return "admin/admin_delete";
    }

    /**
     * クイズを論理削除し、ゴミ箱テーブル（quiz_trash）に移動する
     *
     * @param id クイズID
     * @param model モデル属性
     * @return 成功モーダル付きの同一ページ再描画
     */
    @PostMapping("/admin/delete")
    public String moveToTrash(@RequestParam Long id, Model model) {
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

            // 削除成功後に元のクイズ情報を再設定（モーダルの表示用）
            model.addAttribute("quiz", quiz);
            model.addAttribute("moved", true); // モーダル表示用フラグ
            return "admin/admin_delete";
        }
        return "redirect:/admin/dashboard";
    }

 }