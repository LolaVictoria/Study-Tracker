package com.lolavictoria.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lolavictoria.entity.Question;

import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    boolean existsByLink(String link);

    Optional<Question> findByLink(String link);
}