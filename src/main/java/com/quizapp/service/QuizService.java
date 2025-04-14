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

    private final QuizRepository quizRepository;
    private final CategoryRepository categoryRepository;

    public QuizService(QuizRepository quizRepository, CategoryRepository categoryRepository) {
        this.quizRepository = quizRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<QuizEntity> getAllQuestions() {
        return quizRepository.findAll();
    }

    public Optional<QuizEntity> getQuestionById(Long id) {
        return quizRepository.findById(id);
    }

    public QuizEntity saveQuestion(QuizEntity quizEntity) {
        return quizRepository.save(quizEntity);
    }

    public void deleteQuestion(Long id) {
        quizRepository.deleteById(id);
    }

    public List<QuizEntity> getQuestionsByCategory(String categoryName) {
        Integer categoryId = categoryRepository.findIdByName(categoryName);
        if (categoryId == null) return Collections.emptyList();
        return quizRepository.findByCategory_Id(categoryId);
    }

    public QuizEntity getFirstQuestionByCategoryId(Integer categoryId) {
        return quizRepository.findFirstByCategory_IdOrderByIdAsc(categoryId);
    }
}
