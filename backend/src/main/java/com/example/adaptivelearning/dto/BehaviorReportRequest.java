package com.example.adaptivelearning.dto;

/**
 * ★ behaviors 上报请求体（BETA 4.4）。语义见第八章（GAMMA）。
 * 校验放在 Service 层以便统一走包络（BETA 4.7 / GAMMA 8.5）。
 */
public class BehaviorReportRequest {
    private Long userId;
    private Long pathId;
    private Integer studyDuration;
    private Integer masteryScore;

    public BehaviorReportRequest() {
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getPathId() { return pathId; }
    public void setPathId(Long pathId) { this.pathId = pathId; }

    public Integer getStudyDuration() { return studyDuration; }
    public void setStudyDuration(Integer studyDuration) { this.studyDuration = studyDuration; }

    public Integer getMasteryScore() { return masteryScore; }
    public void setMasteryScore(Integer masteryScore) { this.masteryScore = masteryScore; }
}
