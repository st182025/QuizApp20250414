package com.quizapp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.quizapp.entity.UserScoreEntity;
import com.quizapp.repository.UserScoreRepository;

@Service
public class UserScoreService {

    @Autowired
    private UserScoreRepository userScoreRepository;

    // 最新スコア3件取得
    public List<UserScoreEntity> loadRecentScores(Long userId) {
        return userScoreRepository.findTop3ByUser_IdOrderByPlayedAtDesc(userId);
    }


    // スコア保存
    public void saveScore(UserScoreEntity score) {
        userScoreRepository.save(score);
    }
}
