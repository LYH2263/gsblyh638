package com.example.adaptivelearning.repository;

import com.example.adaptivelearning.model.LearningBehavior;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LearningBehaviorRepository extends JpaRepository<LearningBehavior, Long> {

    List<LearningBehavior> findByUserIdOrderByCreatedAtDesc(Long userId);

    long countByUserId(Long userId);
}
