package com.example.adaptivelearning.dto;

/**
 * 推荐列表条目 DTO（规格书 4.3）。
 */
public class PathRecommendationResponse {
    private Long pathId;
    private String pathCode;
    private String title;
    private String summary;
    private Integer masteryScore;
    private Integer progress;
    private Integer totalStudyMinutes;
    private String matchHint;

    public Long getPathId() { return pathId; }
    public void setPathId(Long pathId) { this.pathId = pathId; }

    public String getPathCode() { return pathCode; }
    public void setPathCode(String pathCode) { this.pathCode = pathCode; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public Integer getMasteryScore() { return masteryScore; }
    public void setMasteryScore(Integer masteryScore) { this.masteryScore = masteryScore; }

    public Integer getProgress() { return progress; }
    public void setProgress(Integer progress) { this.progress = progress; }

    public Integer getTotalStudyMinutes() { return totalStudyMinutes; }
    public void setTotalStudyMinutes(Integer totalStudyMinutes) { this.totalStudyMinutes = totalStudyMinutes; }

    public String getMatchHint() { return matchHint; }
    public void setMatchHint(String matchHint) { this.matchHint = matchHint; }
}
