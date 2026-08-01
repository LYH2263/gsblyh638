package com.example.adaptivelearning.dto;

public class WeakestPathVO {

    private Long pathId;
    private String title;
    private Integer masteryScore;

    public Long getPathId() { return pathId; }
    public void setPathId(Long pathId) { this.pathId = pathId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Integer getMasteryScore() { return masteryScore; }
    public void setMasteryScore(Integer masteryScore) { this.masteryScore = masteryScore; }
}
