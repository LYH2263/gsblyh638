package com.example.adaptivelearning.service;

import com.example.adaptivelearning.dto.AnalyticsSummaryResponse;
import com.example.adaptivelearning.dto.BehaviorReportRequest;
import com.example.adaptivelearning.dto.PathProgressResponse;
import com.example.adaptivelearning.dto.PathRecommendationResponse;
import com.example.adaptivelearning.exception.LearningApiException;
import com.example.adaptivelearning.model.LearningBehavior;
import com.example.adaptivelearning.model.LearningPath;
import com.example.adaptivelearning.model.UserPathProgress;
import com.example.adaptivelearning.repository.LearningBehaviorRepository;
import com.example.adaptivelearning.repository.LearningPathRepository;
import com.example.adaptivelearning.repository.UserPathProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LearningService {

    @Autowired
    private LearningPathRepository learningPathRepository;

    @Autowired
    private LearningBehaviorRepository learningBehaviorRepository;

    @Autowired
    private UserPathProgressRepository userPathProgressRepository;

    /**
     * 推荐规则（规格书第八章 8.2）：
     * 仅 active=true；progress>=100 的路径不得出现；
     * 按 masteryScore 升序（薄弱优先，无记录视作 0），次级排序 pathId 升序。
     */
    public List<PathRecommendationResponse> getRecommendations(Long userId) {
        if (userId == null) {
            throw new LearningApiException(400, "userId 不能为空");
        }
        List<LearningPath> activePaths = learningPathRepository.findByActiveTrue();
        Map<Long, UserPathProgress> progressMap = new HashMap<>();
        for (UserPathProgress record : userPathProgressRepository.findByUserId(userId)) {
            progressMap.put(record.getPathId(), record);
        }

        List<PathRecommendationResponse> recommendations = new ArrayList<>();
        for (LearningPath path : activePaths) {
            UserPathProgress record = progressMap.get(path.getId());
            int mastery = record != null && record.getMasteryScore() != null ? record.getMasteryScore() : 0;
            int progress = record != null && record.getProgress() != null ? record.getProgress() : 0;
            int totalMinutes = record != null && record.getTotalStudyMinutes() != null ? record.getTotalStudyMinutes() : 0;
            if (progress >= 100) {
                continue;
            }

            PathRecommendationResponse dto = new PathRecommendationResponse();
            dto.setPathId(path.getId());
            dto.setPathCode(path.getPathCode());
            dto.setTitle(path.getTitle());
            dto.setSummary(path.getSummary());
            dto.setMasteryScore(mastery);
            dto.setProgress(progress);
            dto.setTotalStudyMinutes(totalMinutes);
            dto.setMatchHint(mastery < 60 ? "掌握度偏低，建议优先学习" : "掌握情况良好，可继续巩固提升");
            recommendations.add(dto);
        }

        recommendations.sort(Comparator
                .comparingInt(PathRecommendationResponse::getMasteryScore)
                .thenComparing(PathRecommendationResponse::getPathId));
        return recommendations;
    }

    /**
     * 上报语义（规格书第八章 8.3/8.5）：
     * 插入 learning_behaviors；total_study_minutes 累加；masteryScore 覆盖；
     * 更新 progress（= 累计学习分钟 / 预估分钟，封顶 100）；
     * 非法输入整体失败（事务回滚，不得部分写入）。
     */
    @Transactional
    public PathProgressResponse reportBehavior(BehaviorReportRequest request) {
        if (request == null) {
            throw new LearningApiException(400, "请求体不能为空");
        }
        Long userId = request.getUserId();
        Long pathId = request.getPathId();
        Integer studyDuration = request.getStudyDuration();
        Integer masteryScore = request.getMasteryScore();

        if (userId == null) {
            throw new LearningApiException(400, "userId 不能为空");
        }
        if (pathId == null) {
            throw new LearningApiException(400, "pathId 不能为空");
        }
        if (studyDuration == null || studyDuration < 1) {
            throw new LearningApiException(400, "studyDuration 必须为分钟正整数（>=1），0/负数/小数非法");
        }
        if (masteryScore == null || masteryScore < 0 || masteryScore > 100) {
            throw new LearningApiException(400, "masteryScore 必须为 0-100 的整数");
        }

        LearningPath path = learningPathRepository.findById(pathId)
                .orElseThrow(() -> new LearningApiException(404, "学习路径不存在: pathId=" + pathId));

        LearningBehavior behavior = new LearningBehavior();
        behavior.setUserId(userId);
        behavior.setPathId(pathId);
        behavior.setStudyDuration(studyDuration);
        behavior.setMasteryScore(masteryScore);
        behavior.setCreatedAt(LocalDateTime.now());
        learningBehaviorRepository.save(behavior);

        UserPathProgress progress = userPathProgressRepository
                .findByUserIdAndPathId(userId, pathId)
                .orElseGet(() -> {
                    UserPathProgress created = new UserPathProgress();
                    created.setUserId(userId);
                    created.setPathId(pathId);
                    created.setMasteryScore(0);
                    created.setProgress(0);
                    created.setTotalStudyMinutes(0);
                    return created;
                });

        int totalMinutes = (progress.getTotalStudyMinutes() != null ? progress.getTotalStudyMinutes() : 0) + studyDuration;
        int estimated = path.getEstimatedMinutes() != null && path.getEstimatedMinutes() > 0
                ? path.getEstimatedMinutes() : 1;

        progress.setTotalStudyMinutes(totalMinutes);
        progress.setMasteryScore(masteryScore);
        progress.setProgress(Math.min(100, totalMinutes * 100 / estimated));
        progress.setUpdatedAt(LocalDateTime.now());
        userPathProgressRepository.save(progress);

        return toProgressResponse(progress, path);
    }

    /**
     * 单路径进度查询：无记录时按初始值（mastery/progress/total 均为 0）返回。
     */
    public PathProgressResponse getProgress(Long pathId, Long userId) {
        if (userId == null) {
            throw new LearningApiException(400, "userId 不能为空");
        }
        LearningPath path = learningPathRepository.findById(pathId)
                .orElseThrow(() -> new LearningApiException(404, "学习路径不存在: pathId=" + pathId));

        UserPathProgress progress = userPathProgressRepository
                .findByUserIdAndPathId(userId, pathId)
                .orElse(null);
        if (progress == null) {
            PathProgressResponse empty = new PathProgressResponse();
            empty.setUserId(userId);
            empty.setPathId(path.getId());
            empty.setPathCode(path.getPathCode());
            empty.setTitle(path.getTitle());
            empty.setMasteryScore(0);
            empty.setProgress(0);
            empty.setTotalStudyMinutes(0);
            empty.setUpdatedAt(null);
            return empty;
        }
        return toProgressResponse(progress, path);
    }

    /**
     * 分析汇总（规格书第八章 8.4）：
     * totalStudyMinutes = 行为时长求和；completedPathCount = progress>=100；
     * inProgressPathCount = 有进度未完成；weakestPaths = 未完成中 mastery 最低 Top3
     *（与 8.2 一致：无记录视作 mastery 0，progress>=100 视为完成）。
     */
    public AnalyticsSummaryResponse getAnalyticsSummary(Long userId) {
        if (userId == null) {
            throw new LearningApiException(400, "userId 不能为空");
        }

        int totalStudyMinutes = 0;
        for (LearningBehavior behavior : learningBehaviorRepository.findByUserId(userId)) {
            if (behavior.getStudyDuration() != null) {
                totalStudyMinutes += behavior.getStudyDuration();
            }
        }

        List<UserPathProgress> records = userPathProgressRepository.findByUserId(userId);
        Map<Long, UserPathProgress> progressMap = new HashMap<>();
        for (UserPathProgress record : records) {
            progressMap.put(record.getPathId(), record);
        }

        int completedPathCount = 0;
        int inProgressPathCount = 0;
        for (UserPathProgress record : records) {
            int progress = record.getProgress() != null ? record.getProgress() : 0;
            if (progress >= 100) {
                completedPathCount++;
            } else {
                inProgressPathCount++;
            }
        }

        List<AnalyticsSummaryResponse.WeakestPathItem> weakest = new ArrayList<>();
        for (LearningPath path : learningPathRepository.findByActiveTrue()) {
            UserPathProgress record = progressMap.get(path.getId());
            int progress = record != null && record.getProgress() != null ? record.getProgress() : 0;
            if (progress >= 100) {
                continue;
            }
            int mastery = record != null && record.getMasteryScore() != null ? record.getMasteryScore() : 0;
            weakest.add(new AnalyticsSummaryResponse.WeakestPathItem(path.getId(), path.getTitle(), mastery));
        }
        weakest.sort(Comparator
                .comparingInt(AnalyticsSummaryResponse.WeakestPathItem::getMasteryScore)
                .thenComparing(AnalyticsSummaryResponse.WeakestPathItem::getPathId));
        List<AnalyticsSummaryResponse.WeakestPathItem> weakestTop3 =
                weakest.size() > 3 ? weakest.subList(0, 3) : weakest;

        AnalyticsSummaryResponse summary = new AnalyticsSummaryResponse();
        summary.setTotalStudyMinutes(totalStudyMinutes);
        summary.setCompletedPathCount(completedPathCount);
        summary.setInProgressPathCount(inProgressPathCount);
        summary.setWeakestPaths(weakestTop3);
        return summary;
    }

    private PathProgressResponse toProgressResponse(UserPathProgress progress, LearningPath path) {
        PathProgressResponse dto = new PathProgressResponse();
        dto.setUserId(progress.getUserId());
        dto.setPathId(progress.getPathId());
        dto.setPathCode(path.getPathCode());
        dto.setTitle(path.getTitle());
        dto.setMasteryScore(progress.getMasteryScore());
        dto.setProgress(progress.getProgress());
        dto.setTotalStudyMinutes(progress.getTotalStudyMinutes());
        dto.setUpdatedAt(progress.getUpdatedAt());
        return dto;
    }
}
