package com.example.adaptivelearning.dto;

import java.util.List;

public class AnalyticsSummaryVO {

    private Integer totalStudyMinutes;
    private Integer completedPathCount;
    private Integer inProgressPathCount;
    private List<WeakestPathVO> weakestPaths;

    public Integer getTotalStudyMinutes() { return totalStudyMinutes; }
    public void setTotalStudyMinutes(Integer totalStudyMinutes) {
        this.totalStudyMinutes = totalStudyMinutes;
    }

    public Integer getCompletedPathCount() { return completedPathCount; }
    public void setCompletedPathCount(Integer completedPathCount) {
        this.completedPathCount = completedPathCount;
    }

    public Integer getInProgressPathCount() { return inProgressPathCount; }
    public void setInProgressPathCount(Integer inProgressPathCount) {
        this.inProgressPathCount = inProgressPathCount;
    }

    public List<WeakestPathVO> getWeakestPaths() { return weakestPaths; }
    public void setWeakestPaths(List<WeakestPathVO> weakestPaths) {
        this.weakestPaths = weakestPaths;
    }
}
