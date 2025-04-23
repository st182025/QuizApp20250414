package com.quizapp.controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.quizapp.entity.QuizEntity;
import com.quizapp.entity.UserEntity;
import com.quizapp.repository.UserRepository;
import com.quizapp.service.QuizService;

/**
 * クイズ出題・回答・お気に入り追加などを担当するコントローラ
 */
@Controller
public class QuizController {

    private final QuizService quizService;

    @Autowired
    private UserRepository userRepository;

    /**
     * コンストラクタ：QuizServiceをDI
     * @param quizService クイズ操作用サービス
     */
    @Autowired
    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    /**
     * ホーム画面表示（カテゴリ・出題順選択）
     * @return home.html
     */
    @GetMapping("/home")
    public String showHomePage() {
        return "home";
    }

    /**
     * クイズの開始（カテゴリとモードに応じた問題取得と初期表示）
     * @param category カテゴリ名 or all
     * @param mode 出題順（normal or shuffle）
     * @param model 表示用モデル
     * @param session セッションにクイズ状態を保持
     * @return quiz.html（第1問）
     */
    @GetMapping("/quiz/start")
    public String startQuiz(
            @RequestParam String category,
            @RequestParam(defaultValue = "normal") String mode,
            Model model,
            HttpSession session) {

        List<QuizEntity> questions = category.equals("all")
                ? quizService.getAllQuestions()
                : quizService.getQuestionsByCategory(category);

        if (mode.equals("shuffle")) {
            Collections.shuffle(questions);
        }

        if (questions.isEmpty()) {
            model.addAttribute("message", "このカテゴリにはまだ問題がありません。");
            return "error";
        }

        session.setAttribute("questions", questions);
        session.setAttribute("category", category);
        session.setAttribute("mode", mode);
        session.setAttribute("correctCount", 0);

        model.addAttribute("question", questions.get(0));
        model.addAttribute("category", category);
        model.addAttribute("mode", mode);
        model.addAttribute("index", 0);
        model.addAttribute("questionNumber", 1);

        return "quiz";
    }

    /**
     * クイズの解答処理（正誤判定、結果画面表示）
     * @param id クイズID
     * @param selectedOption ユーザーが選択した選択肢（option_1など）
     * @param category カテゴリ名
     * @param index クイズのインデックス
     * @param mode 出題モード
     * @param model 結果表示用モデル
     * @param session 正答数の更新用セッション
     * @return answer.html（結果表示）
     */
    @PostMapping("/quiz/answer")
    public String submitAnswer(
            @RequestParam Long id,
            @RequestParam String selectedOption,
            @RequestParam String category,
            @RequestParam(defaultValue = "0") int index,
            @RequestParam String mode,
            Model model,
            HttpSession session) {

        QuizEntity question = quizService.getQuestionById(id).orElse(null);
        boolean isCorrect = question != null && question.getCorrectAnswer().equals(selectedOption);

        if (isCorrect) {
            Integer correctCount = (Integer) session.getAttribute("correctCount");
            if (correctCount == null) correctCount = 0;
            session.setAttribute("correctCount", correctCount + 1);
        }

        model.addAttribute("isCorrect", isCorrect);
        model.addAttribute("question", question);
        model.addAttribute("selectedOption", selectedOption);
        model.addAttribute("index", index);
        model.addAttribute("category", category);
        model.addAttribute("mode", mode);

        return "answer";
    }

    /**
     * 「次の問題へ」押下時、次のクイズを表示
     * @param index 現在のクイズのインデックス
     * @param session クイズ一覧取得用セッション
     * @param model 表示用モデル
     * @return quiz.html or end.html
     */
    @GetMapping("/quiz/next")
    public String nextQuestion(
            @RequestParam int index,
            HttpSession session,
            Model model) {

        List<QuizEntity> questions = (List<QuizEntity>) session.getAttribute("questions");
        String category = (String) session.getAttribute("category");
        String mode = (String) session.getAttribute("mode");

        if (questions == null || index >= questions.size()) {
            int correctCount = session.getAttribute("correctCount") != null ? (int) session.getAttribute("correctCount") : 0;
            model.addAttribute("correctCount", correctCount);
            model.addAttribute("totalCount", questions != null ? questions.size() : 0);
            return "end";
        }

        QuizEntity nextQuestion = questions.get(index);
        model.addAttribute("question", nextQuestion);
        model.addAttribute("category", category);
        model.addAttribute("mode", mode);
        model.addAttribute("index", index);
        model.addAttribute("questionNumber", index + 1);

        return "quiz";
    }

    /**
     * お気に入り追加処理（ログイン必須）
     * @param quizId 対象のクイズID
     * @param index 現在の問題インデックス
     * @param category カテゴリ名
     * @param mode 出題順モード
     * @param session ユーザー情報用セッション
     * @param model 再描画用モデル
     * @param redirectAttributes リダイレクト用メッセージ
     * @return answer.html（再描画）
     */
    @PostMapping("/quiz/favorite/add")
    public String addFavorite(
            @RequestParam Long quizId,
            @RequestParam int index,
            @RequestParam String category,
            @RequestParam String mode,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        UserEntity user = (UserEntity) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        String[] currentFavorites = user.getFavorites();
        Set<String> favoriteSet = new HashSet<>();
        if (currentFavorites != null) {
            favoriteSet.addAll(Arrays.asList(currentFavorites));
        }
        favoriteSet.add(String.valueOf(quizId));
        user.setFavorites(favoriteSet.toArray(new String[0]));

        userRepository.save(user);

        QuizEntity question = quizService.getQuestionById(quizId).orElse(null);
        model.addAttribute("question", question);
        model.addAttribute("index", index);
        model.addAttribute("category", category);
        model.addAttribute("mode", mode);
        model.addAttribute("selectedOption", "");
        model.addAttribute("isCorrect", null);
        model.addAttribute("successMessage", "お気に入りに追加しました！");

        return "answer";
    }
}
