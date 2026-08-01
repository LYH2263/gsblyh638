package com.example.adaptivelearning.dto;

import java.time.LocalDateTime;

/**
 * ☆ behaviors/recent 列表项。禁止直接返回 Entity（BETA 4.4）。
 */
public class BehaviorResponse {
    private Long id;
    private Long pathId;
    private String pathCode;
    private String title;
    private Integer studyDuration;
    private Integer masteryScore;
    private LocalDateTime createdAt;

    public BehaviorResponse() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPathId() { return pathId; }
    public void setPathId(Long pathId) { this.pathId = pathId; }

    public String getPathCode() { return pathCode; }
    public void setPathCode(String pathCode) { this.pathCode = pathCode; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Integer getStudyDuration() { return studyDuration; }
    public void setStudyDuration(Integer studyDuration) { this.studyDuration = studyDuration; }

    public Integer getMasteryScore() { return masteryScore; }
    public void setMasteryScore(Integer masteryScore) { this.masteryScore = masteryScore; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
