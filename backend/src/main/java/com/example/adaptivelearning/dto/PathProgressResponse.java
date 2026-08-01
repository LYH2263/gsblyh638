package com.example.adaptivelearning.dto;

/**
 * ★ progress 单路径进度 DTO（BETA 4.5），亦为 behaviors 的返回体。
 * 禁止直接返回 JPA Entity（BETA 4.4）。
 */
public class PathProgressResponse {
    private Long pathId;
    private String pathCode;
    private String title;
    private Integer masteryScore;
    private Integer progress;
    private Integer totalStudyMinutes;

    public PathProgressResponse() {
    }

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
}
