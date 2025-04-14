package com.quizapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.quizapp.entity.AdminUserEntity;

public interface AdminUserRepository extends JpaRepository<AdminUserEntity, Long> {
    AdminUserEntity findByUsername(String username);
}
