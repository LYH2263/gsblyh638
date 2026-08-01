package com.example.adaptivelearning.dto;

import java.util.List;

public class AnalyticsSummaryDTO {

    private Integer totalStudyMinutes;
    private Integer completedPathCount;
    private Integer inProgressPathCount;
    private List<WeakPathDTO> weakestPaths;

    public Integer getTotalStudyMinutes() { return totalStudyMinutes; }
    public void setTotalStudyMinutes(Integer totalStudyMinutes) { this.totalStudyMinutes = totalStudyMinutes; }

    public Integer getCompletedPathCount() { return completedPathCount; }
    public void setCompletedPathCount(Integer completedPathCount) { this.completedPathCount = completedPathCount; }

    public Integer getInProgressPathCount() { return inProgressPathCount; }
    public void setInProgressPathCount(Integer inProgressPathCount) { this.inProgressPathCount = inProgressPathCount; }

    public List<WeakPathDTO> getWeakestPaths() { return weakestPaths; }
    public void setWeakestPaths(List<WeakPathDTO> weakestPaths) { this.weakestPaths = weakestPaths; }
}
