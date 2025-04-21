package com.quizapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.quizapp.entity.QuizEntity;
import com.quizapp.repository.CategoryRepository;
import com.quizapp.repository.QuizRepository;

@Controller
@RequestMapping("/admin/edit")
public class AdminEditController {

	@Autowired
	private QuizRepository quizRepository;
	@Autowired
	private CategoryRepository categoryRepository;

	/**
	     * 編集画面を表示
	     * @param id 編集対象のクイズID
	     * @param model モデル
	     * @return 編集画面テンプレート
	     */
	@GetMapping("/{id}")
	public String showEditForm(@PathVariable Long id, Model model) {
		QuizEntity quiz = quizRepository.findById(id).orElse(null);
		if (quiz == null) {
			return "redirect:/admin/dashboard";
		}
		model.addAttribute("quiz", quiz);
		model.addAttribute("categories", categoryRepository.findAll());
		return "admin/admin_edit";
	}

	/**
	 * 編集内容を保存（更新処理）
	 * @param quiz フォームからの更新情報
	 * @param model モデル
	 * @return 更新後も同じ画面に再表示（更新完了モーダル表示）
	 */
	@PostMapping("")
	public String updateQuiz(@ModelAttribute QuizEntity quiz, Model model) {
		quizRepository.save(quiz);
		model.addAttribute("quiz", quiz);
		model.addAttribute("categories", categoryRepository.findAll());
		model.addAttribute("updated", true); // モーダル表示用フラグ
		return "admin/admin_edit";
	}

}
