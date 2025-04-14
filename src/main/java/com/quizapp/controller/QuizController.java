package com.quizapp.controller;

import java.util.Collections;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.quizapp.entity.QuizEntity;
import com.quizapp.service.QuizService;

@Controller
public class QuizController {

    private final QuizService quizService;

    // QuizServiceをインジェクト（依存性の注入）
    @Autowired
    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }
    //test3
    //test4
    //test5

    // ホーム画面表示（home.html）
    @GetMapping("/home")
    public String showHomePage() {
        return "home";
    }

    // クイズ開始（カテゴリと出題順に応じて最初の問題を表示）
    @GetMapping("/quiz/start")
    public String startQuiz(
            @RequestParam String category,                         // カテゴリ名
            @RequestParam(defaultValue = "normal") String mode,    // 出題順（normal or shuffle）
            Model model,
            HttpSession session) {

        // カテゴリに応じたクイズ一覧を取得
        List<QuizEntity> questions = category.equals("all")
                ? quizService.getAllQuestions()
                : quizService.getQuestionsByCategory(category);

        // シャッフル指定があればランダムに並び替え
        if (mode.equals("shuffle")) {
            Collections.shuffle(questions);
        }

        // クイズがない場合はエラーページへ
        if (questions.isEmpty()) {
            model.addAttribute("message", "このカテゴリにはまだ問題がありません。");
            return "error";
        }

        // セッションに状態を保存（クイズ一覧、カテゴリ、モード、正答数）
        session.setAttribute("questions", questions);
        session.setAttribute("category", category);
        session.setAttribute("mode", mode);
        session.setAttribute("correctCount", 0);  // 正答数を初期化

        // 最初の問題を表示
        QuizEntity firstQuestion = questions.get(0);
        model.addAttribute("question", firstQuestion);
        model.addAttribute("category", category);
        model.addAttribute("mode", mode);
        model.addAttribute("index", 0);            // インデックス（何問目か）
        model.addAttribute("questionNumber", 1);   // 表示用の「第○問」

        return "quiz";
    }

    // 解答送信時に呼び出される（正解・不正解の判定）
    @PostMapping("/quiz/answer")
    public String submitAnswer(
            @RequestParam Long id,                        // 問題ID
            @RequestParam String selectedOption,          // ユーザーが選んだ選択肢（option_1 など）
            @RequestParam String category,
            @RequestParam(defaultValue = "0") int index,  // 現在のインデックス
            @RequestParam String mode,
            Model model,
            HttpSession session) {

        // IDから問題を取得
        QuizEntity question = quizService.getQuestionById(id).orElse(null);

        // 正解判定（DBのcorrectAnswerと比較）
        boolean isCorrect = question.getCorrectAnswer().equals(selectedOption);

        // 正解なら正答数カウントを＋1
        if (isCorrect) {
            Integer correctCount = (Integer) session.getAttribute("correctCount");
            if (correctCount == null) correctCount = 0;
            session.setAttribute("correctCount", correctCount + 1);
        }

        // ビューに渡すデータ
        model.addAttribute("isCorrect", isCorrect);
        model.addAttribute("question", question);
        model.addAttribute("selectedOption", selectedOption);
        model.addAttribute("index", index);
        model.addAttribute("category", category);
        model.addAttribute("mode", mode);

        return "answer";
    }

    // 「次の問題へ」ボタン押下後、次の問題へ進む
    @GetMapping("/quiz/next")
    public String nextQuestion(
            @RequestParam int index,     // 今の問題のインデックス＋1が渡ってくる
            HttpSession session,
            Model model) {

        // セッションから情報取得
        List<QuizEntity> questions = (List<QuizEntity>) session.getAttribute("questions");
        String category = (String) session.getAttribute("category");
        String mode = (String) session.getAttribute("mode");

        // 最終問題を超えた場合は終了画面へ
        if (questions == null || index >= questions.size()) {
            int correctCount = session.getAttribute("correctCount") != null ? (int) session.getAttribute("correctCount") : 0;
            model.addAttribute("correctCount", correctCount);
            model.addAttribute("totalCount", questions != null ? questions.size() : 0);
            return "end";
        }

        // 次の問題を取得・表示
        QuizEntity nextQuestion = questions.get(index);
        model.addAttribute("question", nextQuestion);
        model.addAttribute("category", category);
        model.addAttribute("mode", mode);
        model.addAttribute("index", index);
        model.addAttribute("questionNumber", index + 1);

        return "quiz";
    }
}
