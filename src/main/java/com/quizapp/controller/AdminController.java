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
	/**
	 * 管理者による新規クイズの保存処理。
	 * 入力に不備がある場合は元の入力画面にエラー付きで戻す。
	 * 全て正しく入力されている場合のみ保存し、確認モーダルの後にリダイレクト。
	 */
	@PostMapping("/admin/save")
	public String saveNewQuiz(
	        @RequestParam(required = false) String category,
	        @RequestParam(required = false) String questionText,
	        @RequestParam(required = false) String option_1,
	        @RequestParam(required = false) String option_2,
	        @RequestParam(required = false) String option_3,
	        @RequestParam(required = false) String option_4,
	        @RequestParam(required = false) String correctAnswer,
	        @RequestParam(required = false) String explanation,
	        HttpSession session,
	        Model model) {

	    // 管理者ログインチェック
	    AdminUserEntity user = (AdminUserEntity) session.getAttribute("adminUser");
	    if (user == null) {
	        return "redirect:/login";
	    }

	    // 入力チェック（どれか1つでも空なら hasError=true）
	    boolean hasError = false;

	    if (category == null || category.isEmpty()) {
	        model.addAttribute("categoryError", "必須項目です。入力してください。");
	        hasError = true;
	    }
	    if (questionText == null || questionText.isEmpty()) {
	        model.addAttribute("questionTextError", "必須項目です。入力してください。");
	        hasError = true;
	    }
	    if (option_1 == null || option_1.isEmpty()) {
	        model.addAttribute("option1Error", "必須項目です。入力してください。");
	        hasError = true;
	    }
	    if (option_2 == null || option_2.isEmpty()) {
	        model.addAttribute("option2Error", "必須項目です。入力してください。");
	        hasError = true;
	    }
	    if (option_3 == null || option_3.isEmpty()) {
	        model.addAttribute("option3Error", "必須項目です。入力してください。");
	        hasError = true;
	    }
	    if (option_4 == null || option_4.isEmpty()) {
	        model.addAttribute("option4Error", "必須項目です。入力してください。");
	        hasError = true;
	    }
	    if (correctAnswer == null || correctAnswer.isEmpty()) {
	        model.addAttribute("correctAnswerError", "必須項目です。入力してください。");
	        hasError = true;
	    }
	    if (explanation == null || explanation.isEmpty()) {
	        model.addAttribute("explanationError", "必須項目です。入力してください。");
	        hasError = true;
	    }

	    // エラーがある場合、入力内容も戻す
	    if (hasError) {
	        model.addAttribute("category", category);
	        model.addAttribute("questionText", questionText);
	        model.addAttribute("option_1", option_1);
	        model.addAttribute("option_2", option_2);
	        model.addAttribute("option_3", option_3);
	        model.addAttribute("option_4", option_4);
	        model.addAttribute("correctAnswer", correctAnswer);
	        model.addAttribute("explanation", explanation);
	        model.addAttribute("categoryList", categoryRepository.findAll());
	        return "admin/admin_new";
	    }

	    // カテゴリ情報取得
	    CategoryEntity categoryEntity = categoryRepository.findByName(category);
	    if (categoryEntity == null) {
	        model.addAttribute("error", "カテゴリが存在しません。先にカテゴリを登録してください。");
	        model.addAttribute("categoryList", categoryRepository.findAll());
	        return "admin/admin_new";
	    }

	    // 正常時の保存処理
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

	 // 成功フラグをビューに渡す（保存モーダル表示用）
	 model.addAttribute("saved", true);
	 model.addAttribute("categoryList", categoryRepository.findAll());
	 return "admin/admin_new";
	}
}