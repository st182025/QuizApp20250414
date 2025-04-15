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
import com.quizapp.entity.CategoryEntity;
import com.quizapp.entity.QuizEntity;
import com.quizapp.repository.CategoryRepository;
import com.quizapp.service.QuizService;

/**
 * 管理者機能（ダッシュボード表示、新規問題作成）を担当するコントローラ。
 */
@Controller
public class AdminController {

	private final QuizService quizService;
	private final CategoryRepository categoryRepository;

	@Autowired
	public AdminController(QuizService quizService, CategoryRepository categoryRepository) {
		this.quizService = quizService;
		this.categoryRepository = categoryRepository;
	}

	/**
	 * 管理者ダッシュボード表示
	 */
	@GetMapping("/admin/dashboard")
	public String showAdminDashboard(HttpSession session, Model model) {
	    AdminUserEntity user = (AdminUserEntity) session.getAttribute("adminUser");
	    if (user == null) {
	        return "redirect:/login";
	    }

	    List<QuizEntity> quizList = quizService.getAllQuestions();
	    model.addAttribute("quizList", quizList);
	    model.addAttribute("adminUser", user);

	    // ✅ 修正ここだけ
	    return "admin/admin_dashboard";
	}


	/**
	 * 新規クイズ作成フォームの表示
	 */
	@GetMapping("/admin/new")
	public String showNewQuizForm(HttpSession session, Model model) {
		AdminUserEntity user = (AdminUserEntity) session.getAttribute("adminUser");
		if (user == null) {
			return "redirect:/login";
		}
		List<CategoryEntity> categoryList = categoryRepository.findAll();
		model.addAttribute("categoryList", categoryList);
		model.addAttribute("adminUser", user);
		return "admin/admin_new";
	}

	/**
	 * 新規クイズの保存処理
	 */
	@PostMapping("/admin/save")
	public String saveNewQuiz(
			@RequestParam String category,
			@RequestParam String questionText,
			@RequestParam String option_1,
			@RequestParam String option_2,
			@RequestParam String option_3,
			@RequestParam String option_4,
			@RequestParam String correctAnswer,
			@RequestParam String explanation,
			HttpSession session,
			Model model) {

		AdminUserEntity user = (AdminUserEntity) session.getAttribute("adminUser");
		if (user == null) {
			return "redirect:/login";
		}

		CategoryEntity categoryEntity = categoryRepository.findByName(category);
		if (categoryEntity == null) {
			model.addAttribute("error", "カテゴリが存在しません。先にカテゴリを登録してください。");
			model.addAttribute("categoryList", categoryRepository.findAll());
			return "admin/admin_new"; 
		}

		QuizEntity quiz = new QuizEntity();
		quiz.setCategory(categoryEntity);
		quiz.setQuestionText(questionText);
		quiz.setOption_1(option_1);
		quiz.setOption_2(option_2);
		quiz.setOption_3(option_3);
		quiz.setOption_4(option_4);
		quiz.setCorrectAnswer(correctAnswer);
		quiz.setExplanation(explanation);

		quizService.saveQuestion(quiz);

		return "redirect:/admin/dashboard";
	}
}
