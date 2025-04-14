package com.quizapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.quizapp.entity.AdminUserEntity;

public interface AdminUserRepository extends JpaRepository<AdminUserEntity, Integer> {

    // ログイン用：ユーザー名とパスワードの一致を探す
    AdminUserEntity findByUsernameAndPassword(String username, String password);
}
