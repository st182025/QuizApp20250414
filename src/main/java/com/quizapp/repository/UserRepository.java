package com.quizapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.quizapp.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity,Long>{
UserEntity findByUsername(String username);
}
