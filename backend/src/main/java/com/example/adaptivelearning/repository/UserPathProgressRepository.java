package com.example.adaptivelearning.repository;

import com.example.adaptivelearning.model.UserPathProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPathProgressRepository extends JpaRepository<UserPathProgress, Long> {

    Optional<UserPathProgress> findByUserIdAndPathId(Long userId, Long pathId);

    List<UserPathProgress> findByUserId(Long userId);
}
