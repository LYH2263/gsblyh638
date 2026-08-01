package com.example.adaptivelearning.dto;

/**
 * 学习行为上报请求体。
 * 取值规则见规格书第八章：studyDuration 为分钟正整数（>=1），masteryScore 为 0-100 整数。
 */
public class BehaviorReportRequest {
    private Long userId;
    private Long pathId;
    private Integer studyDuration;
    private Integer masteryScore;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getPathId() { return pathId; }
    public void setPathId(Long pathId) { this.pathId = pathId; }

    public Integer getStudyDuration() { return studyDuration; }
    public void setStudyDuration(Integer studyDuration) { this.studyDuration = studyDuration; }

    public Integer getMasteryScore() { return masteryScore; }
    public void setMasteryScore(Integer masteryScore) { this.masteryScore = masteryScore; }
}
