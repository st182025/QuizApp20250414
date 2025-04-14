package com.quizapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.quizapp.entity.CategoryEntity;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Integer> {
	CategoryEntity findByName(String name);
	// カテゴリ名（name）からカテゴリID（id）を取得するJPQLクエリ
	// CategoryEntity を c という別名（エイリアス）で扱い、c.name で名前検索
    @Query("SELECT c.id FROM CategoryEntity c WHERE c.name = :name")
    Integer findIdByName(String name);
}
