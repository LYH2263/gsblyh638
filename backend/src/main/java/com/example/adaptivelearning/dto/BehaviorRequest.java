package com.example.adaptivelearning.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class BehaviorRequest {

    @NotNull(message = "userId 不能为空")
    private Long userId;

    @NotNull(message = "pathId 不能为空")
    private Long pathId;

    @NotNull(message = "studyDuration 不能为空")
    @Min(value = 1, message = "studyDuration 必须为正整数（分钟）")
    private Integer studyDuration;

    @NotNull(message = "masteryScore 不能为空")
    @Min(value = 0, message = "masteryScore 必须在 0-100 之间")
    @Max(value = 100, message = "masteryScore 必须在 0-100 之间")
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
