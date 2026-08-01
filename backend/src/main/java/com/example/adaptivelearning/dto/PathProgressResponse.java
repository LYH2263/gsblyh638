package com.example.adaptivelearning.dto;

import java.time.LocalDateTime;

/**
 * 单路径进度 DTO（规格书 4.4/4.5）。
 */
public class PathProgressResponse {
    private Long userId;
    private Long pathId;
    private String pathCode;
    private String title;
    private Integer masteryScore;
    private Integer progress;
    private Integer totalStudyMinutes;
    private LocalDateTime updatedAt;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getPathId() { return pathId; }
    public void setPathId(Long pathId) { this.pathId = pathId; }

    public String getPathCode() { return pathCode; }
    public void setPathCode(String pathCode) { this.pathCode = pathCode; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Integer getMasteryScore() { return masteryScore; }
    public void setMasteryScore(Integer masteryScore) { this.masteryScore = masteryScore; }

    public Integer getProgress() { return progress; }
    public void setProgress(Integer progress) { this.progress = progress; }

    public Integer getTotalStudyMinutes() { return totalStudyMinutes; }
    public void setTotalStudyMinutes(Integer totalStudyMinutes) { this.totalStudyMinutes = totalStudyMinutes; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
