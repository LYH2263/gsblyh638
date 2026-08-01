package com.example.adaptivelearning.repository;

import com.example.adaptivelearning.model.LearningPath;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LearningPathRepository extends JpaRepository<LearningPath, Long> {

    List<LearningPath> findByActiveTrueOrderByIdAsc();

    Optional<LearningPath> findByPathCode(String pathCode);
}
