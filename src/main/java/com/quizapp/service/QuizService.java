package com.quizapp.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.quizapp.entity.QuizEntity;
import com.quizapp.repository.CategoryRepository;
import com.quizapp.repository.QuizRepository;

@Service
public class QuizService {

    private final QuizRepository quizRepository;         // クイズ用リポジトリ
    private final CategoryRepository categoryRepository; // カテゴリ用リポジトリ

    // コンストラクタインジェクション
    public QuizService(QuizRepository quizRepository, CategoryRepository categoryRepository) {
        this.quizRepository = quizRepository;
        this.categoryRepository = categoryRepository;
    }

    /**
     * すべてのクイズを取得（オールジャンル用）
     */
    public List<QuizEntity> getAllQuestions() {
        return quizRepository.findAll();
    }

    /**
     * IDから特定のクイズを取得
     */
    public Optional<QuizEntity> getQuestionById(Long id) {
        return quizRepository.findById(id);
    }

    /**
     * クイズを保存（新規登録や編集用）
     */
    public QuizEntity saveQuestion(QuizEntity quizEntity) {
        return quizRepository.save(quizEntity);
    }

    /**
     * クイズをID指定で削除
     */
    public void deleteQuestion(Long id) {
        quizRepository.deleteById(id);
    }

    /**
     * カテゴリ名（文字列）からカテゴリIDを取得し、該当するクイズ一覧を返す
     */
    public List<QuizEntity> getQuestionsByCategory(String categoryName) {
        Integer categoryId = categoryRepository.findIdByName(categoryName);
        if (categoryId == null) return Collections.emptyList(); // 該当なしの場合は空リスト
        return quizRepository.findByCategory_Id(categoryId);
    }

    /**
     * カテゴリIDに属する最初のクイズ（ID昇順）を1件取得
     */
    public QuizEntity getFirstQuestionByCategoryId(Integer categoryId) {
        return quizRepository.findFirstByCategory_IdOrderByIdAsc(categoryId);
    }
}
