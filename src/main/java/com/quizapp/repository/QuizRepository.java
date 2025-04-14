package com.quizapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.quizapp.entity.QuizEntity;

public interface QuizRepository extends JpaRepository<QuizEntity, Long> {

    List<QuizEntity> findByCategory_Id(Integer categoryId);

    QuizEntity findFirstByCategory_IdOrderByIdAsc(Integer categoryId);
}
