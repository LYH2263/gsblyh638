package com.example.adaptivelearning.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;

/**
 * 用户在单条路径上的聚合进度。对应数据模型章 user_path_progress。
 * UNIQUE(user_id, path_id)。手写 getter/setter，禁止 Lombok（ALPHA）。
 */
@Entity
@Table(name = "user_path_progress",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "path_id"}))
public class UserPathProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "path_id", nullable = false)
    private Long pathId;

    /** 掌握度 0-100，上报时覆盖（GAMMA 8.3） */
    @Column(name = "mastery_score", nullable = false)
    private Integer masteryScore;

    /** 进度 0-100，>=100 为完成（GAMMA 8.1） */
    @Column(nullable = false)
    private Integer progress;

    /** 累计学习分钟，上报时累加（GAMMA 8.3） */
    @Column(name = "total_study_minutes", nullable = false)
    private Integer totalStudyMinutes;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getPathId() { return pathId; }
    public void setPathId(Long pathId) { this.pathId = pathId; }

    public Integer getMasteryScore() { return masteryScore; }
    public void setMasteryScore(Integer masteryScore) { this.masteryScore = masteryScore; }

    public Integer getProgress() { return progress; }
    public void setProgress(Integer progress) { this.progress = progress; }

    public Integer getTotalStudyMinutes() { return totalStudyMinutes; }
    public void setTotalStudyMinutes(Integer totalStudyMinutes) { this.totalStudyMinutes = totalStudyMinutes; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
