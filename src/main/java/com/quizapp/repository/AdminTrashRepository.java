package com.quizapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.quizapp.entity.AdminTrashEntity;

@Repository
public interface AdminTrashRepository extends JpaRepository<AdminTrashEntity, Long> {

}
