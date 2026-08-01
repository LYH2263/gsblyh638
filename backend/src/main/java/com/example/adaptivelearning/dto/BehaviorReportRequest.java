package com.example.adaptivelearning.dto;

import jakarta.validation.constraints.NotNull;

public class BehaviorReportRequest {

    @NotNull(message = "userId 不能为空")
    private Long userId;

    @NotNull(message = "pathId 不能为空")
    private Long pathId;

    @NotNull(message = "studyDuration 不能为空")
    private Integer studyDuration;

    @NotNull(message = "masteryScore 不能为空")
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
