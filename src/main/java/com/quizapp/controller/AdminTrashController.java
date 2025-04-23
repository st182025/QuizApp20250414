package com.quizapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.quizapp.entity.AdminTrashEntity;
import com.quizapp.entity.QuizEntity;
import com.quizapp.repository.AdminTrashRepository;
import com.quizapp.repository.QuizRepository;

/**
 * ゴミ箱（論理削除されたクイズ）管理用コントローラ
 */
@Controller
public class AdminTrashController {

    @Autowired
    private AdminTrashRepository adminTrashRepository;

    @Autowired
    private QuizRepository quizRepository;

    /**
     * ゴミ箱一覧画面の表示
     * @param model 表示用Model
     * @return ゴミ箱一覧テンプレート
     */
    @GetMapping("/admin/trash")
    public String showTrashList(Model model) {
        List<AdminTrashEntity> trashList = adminTrashRepository.findAll();
        model.addAttribute("trashList", trashList);
        return "admin/admin_trash";
    }

    /**
     * ゴミ箱からクイズを復元する（POST）
     * @param id 復元対象のクイズID
     * @param redirectAttributes Flash属性によるメッセージ表示用
     * @return ゴミ箱一覧へリダイレクト
     */
    @PostMapping("/admin/trash/restore")
    public String restoreFromTrash(@RequestParam Long id, RedirectAttributes redirectAttributes) {
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

            redirectAttributes.addFlashAttribute("message", "復元しました！");
        } else {
            redirectAttributes.addFlashAttribute("message", "復元に失敗しました。");
        }
        return "redirect:/admin/trash";
    }

    /**
     * ゴミ箱のクイズを完全削除する（POST）
     * @param id 完全削除対象のクイズID
     * @param redirectAttributes Flash属性によるメッセージ表示用
     * @return ゴミ箱一覧へリダイレクト
     */
    @PostMapping("/admin/trash/permanent-delete")
    public String deletePermanently(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        if (adminTrashRepository.existsById(id)) {
            adminTrashRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "完全に削除しました！");
        } else {
            redirectAttributes.addFlashAttribute("message", "削除に失敗しました。");
        }
        return "redirect:/admin/trash";
    }
}
