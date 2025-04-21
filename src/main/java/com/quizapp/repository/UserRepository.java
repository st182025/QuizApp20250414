package com.quizapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.quizapp.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long>{
UserEntity findByUsername(String username);
}
