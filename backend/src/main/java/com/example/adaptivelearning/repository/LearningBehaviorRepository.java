package com.example.adaptivelearning.repository;

import com.example.adaptivelearning.model.LearningBehavior;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LearningBehaviorRepository extends JpaRepository<LearningBehavior, Long> {
    List<LearningBehavior> findByUserId(Long userId);
}
