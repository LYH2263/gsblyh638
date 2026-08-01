package com.example.adaptivelearning.dto;

import java.util.List;

/**
 * 分析汇总 DTO（规格书 4.6 / 第八章 8.4）。
 */
public class AnalyticsSummaryResponse {
    private Integer totalStudyMinutes;
    private Integer completedPathCount;
    private Integer inProgressPathCount;
    private List<WeakestPathItem> weakestPaths;

    public Integer getTotalStudyMinutes() { return totalStudyMinutes; }
    public void setTotalStudyMinutes(Integer totalStudyMinutes) { this.totalStudyMinutes = totalStudyMinutes; }

    public Integer getCompletedPathCount() { return completedPathCount; }
    public void setCompletedPathCount(Integer completedPathCount) { this.completedPathCount = completedPathCount; }

    public Integer getInProgressPathCount() { return inProgressPathCount; }
    public void setInProgressPathCount(Integer inProgressPathCount) { this.inProgressPathCount = inProgressPathCount; }

    public List<WeakestPathItem> getWeakestPaths() { return weakestPaths; }
    public void setWeakestPaths(List<WeakestPathItem> weakestPaths) { this.weakestPaths = weakestPaths; }

    /**
     * 最薄弱路径条目：pathId/title/masteryScore。
     */
    public static class WeakestPathItem {
        private Long pathId;
        private String title;
        private Integer masteryScore;

        public WeakestPathItem() {
        }

        public WeakestPathItem(Long pathId, String title, Integer masteryScore) {
            this.pathId = pathId;
            this.title = title;
            this.masteryScore = masteryScore;
        }

        public Long getPathId() { return pathId; }
        public void setPathId(Long pathId) { this.pathId = pathId; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public Integer getMasteryScore() { return masteryScore; }
        public void setMasteryScore(Integer masteryScore) { this.masteryScore = masteryScore; }
    }
}
