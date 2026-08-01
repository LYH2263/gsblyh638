package com.example.adaptivelearning.repository;

import com.example.adaptivelearning.model.LearningPath;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LearningPathRepository extends JpaRepository<LearningPath, Long> {
    List<LearningPath> findByActiveTrue();
    Optional<LearningPath> findByPathCode(String pathCode);
}
