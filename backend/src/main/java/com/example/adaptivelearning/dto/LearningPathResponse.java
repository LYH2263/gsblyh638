package com.example.adaptivelearning.dto;

/**
 * ☆ paths 路径主数据 VO。禁止直接返回 Entity（BETA 4.4）。
 */
public class LearningPathResponse {
    private Long pathId;
    private String pathCode;
    private String title;
    private String summary;
    private Integer difficulty;
    private Integer estimatedMinutes;

    public LearningPathResponse() {
    }

    public Long getPathId() { return pathId; }
    public void setPathId(Long pathId) { this.pathId = pathId; }

    public String getPathCode() { return pathCode; }
    public void setPathCode(String pathCode) { this.pathCode = pathCode; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public Integer getDifficulty() { return difficulty; }
    public void setDifficulty(Integer difficulty) { this.difficulty = difficulty; }

    public Integer getEstimatedMinutes() { return estimatedMinutes; }
    public void setEstimatedMinutes(Integer estimatedMinutes) { this.estimatedMinutes = estimatedMinutes; }
}
