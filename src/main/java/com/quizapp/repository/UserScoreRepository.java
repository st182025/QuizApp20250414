package com.quizapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.quizapp.entity.UserScoreEntity;

@Repository
public interface UserScoreRepository extends JpaRepository<UserScoreEntity, Long> {

    // 特定ユーザーの最新3件のスコア履歴を取得
    List<UserScoreEntity> findTop3ByUser_IdOrderByPlayedAtDesc(Long userId);
}
