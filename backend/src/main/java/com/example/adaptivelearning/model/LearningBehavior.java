package com.example.adaptivelearning.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "learning_behaviors")
public class LearningBehavior {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "path_id", nullable = false)
    private Long pathId;

    @Column(name = "study_duration", nullable = false)
    private Integer studyDuration;

    @Column(name = "mastery_score", nullable = false)
    private Integer masteryScore;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getPathId() { return pathId; }
    public void setPathId(Long pathId) { this.pathId = pathId; }

    public Integer getStudyDuration() { return studyDuration; }
    public void setStudyDuration(Integer studyDuration) { this.studyDuration = studyDuration; }

    public Integer getMasteryScore() { return masteryScore; }
    public void setMasteryScore(Integer masteryScore) { this.masteryScore = masteryScore; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
