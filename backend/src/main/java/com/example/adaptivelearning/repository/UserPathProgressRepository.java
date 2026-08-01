package com.example.adaptivelearning.repository;

import com.example.adaptivelearning.model.UserPathProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserPathProgressRepository extends JpaRepository<UserPathProgress, Long> {
    List<UserPathProgress> findByUserId(Long userId);
    Optional<UserPathProgress> findByUserIdAndPathId(Long userId, Long pathId);
}
