package com.example.adaptivelearning.dto;

public class PathProgressVO {

    private Long pathId;
    private String pathCode;
    private String title;
    private Integer masteryScore;
    private Integer progress;
    private Integer totalStudyMinutes;
    private Boolean completed;

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

    public Boolean getCompleted() { return completed; }
    public void setCompleted(Boolean completed) { this.completed = completed; }
}
