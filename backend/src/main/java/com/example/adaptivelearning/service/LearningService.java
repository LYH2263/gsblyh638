package com.example.adaptivelearning.service;

import com.example.adaptivelearning.dto.AnalyticsSummaryResponse;
import com.example.adaptivelearning.dto.BehaviorReportRequest;
import com.example.adaptivelearning.dto.BehaviorResponse;
import com.example.adaptivelearning.dto.LearningPathResponse;
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
import java.util.Optional;

/**
 * 学习域业务服务。集中承载第八章（GAMMA）推荐与计分规则。
 * Controller 只调用本层，Repository 只在本层注入（BETA 4.3）。
 */
@Service
public class LearningService {

    private static final int PROGRESS_COMPLETE = 100;

    @Autowired
    private LearningPathRepository learningPathRepository;

    @Autowired
    private LearningBehaviorRepository learningBehaviorRepository;

    @Autowired
    private UserPathProgressRepository userPathProgressRepository;

    /**
     * ★ 推荐列表（GAMMA 8.2）：仅 active=true；progress>=100 不出现；
     * 按 masteryScore 升序（无记录视作 0），次级按 pathId 升序。
     */
    public List<PathRecommendationResponse> recommend(Long userId) {
        if (userId == null) {
            throw new LearningApiException(1001, "userId 不能为空");
        }
        List<LearningPath> activePaths = learningPathRepository.findByActiveTrue();
        Map<Long, UserPathProgress> progressByPath = new HashMap<>();
        for (UserPathProgress p : userPathProgressRepository.findByUserId(userId)) {
            progressByPath.put(p.getPathId(), p);
        }

        List<PathRecommendationResponse> result = new ArrayList<>();
        for (LearningPath path : activePaths) {
            UserPathProgress progress = progressByPath.get(path.getId());
            int mastery = progress != null ? progress.getMasteryScore() : 0;
            int pct = progress != null ? progress.getProgress() : 0;
            int minutes = progress != null ? progress.getTotalStudyMinutes() : 0;

            // 完成的路径不得出现（GAMMA 8.2.2）
            if (pct >= PROGRESS_COMPLETE) {
                continue;
            }

            PathRecommendationResponse dto = new PathRecommendationResponse();
            dto.setPathId(path.getId());
            dto.setPathCode(path.getPathCode());
            dto.setTitle(path.getTitle());
            dto.setSummary(path.getSummary());
            dto.setMasteryScore(mastery);
            dto.setProgress(pct);
            dto.setTotalStudyMinutes(minutes);
            dto.setMatchHint(mastery == 0 ? "尚未开始，建议优先攻克" : "薄弱优先，建议巩固");
            result.add(dto);
        }

        // 薄弱优先：mastery 升序，次级 pathId 升序（GAMMA 8.2.3/8.2.4）
        result.sort(Comparator
                .comparingInt(PathRecommendationResponse::getMasteryScore)
                .thenComparing(PathRecommendationResponse::getPathId));
        return result;
    }

    /**
     * ★ 上报行为（GAMMA 8.1/8.3/8.5）：校验非法输入 -> 失败且不写入；
     * total_study_minutes 累加、masteryScore 覆盖、更新 progress、插入 behaviors。
     */
    @Transactional
    public PathProgressResponse reportBehavior(BehaviorReportRequest request) {
        if (request == null) {
            throw new LearningApiException(1000, "请求体不能为空");
        }
        Long userId = request.getUserId();
        Long pathId = request.getPathId();
        Integer studyDuration = request.getStudyDuration();
        Integer masteryScore = request.getMasteryScore();

        if (userId == null) {
            throw new LearningApiException(1001, "userId 不能为空");
        }
        if (pathId == null) {
            throw new LearningApiException(1002, "pathId 不能为空");
        }
        // studyDuration 必须为正整数（≥1），0/负/小数在整型入参下等价于非正数（GAMMA 8.1.1）
        if (studyDuration == null || studyDuration < 1) {
            throw new LearningApiException(1003, "studyDuration 必须为正整数（分钟，≥1）");
        }
        // masteryScore 必须为 0-100 整数（GAMMA 8.1.2）
        if (masteryScore == null || masteryScore < 0 || masteryScore > 100) {
            throw new LearningApiException(1004, "masteryScore 必须为 0-100 的整数");
        }

        LearningPath path = learningPathRepository.findById(pathId)
                .orElseThrow(() -> new LearningApiException(1005, "学习路径不存在"));

        // 追加不可变行为流水（GAMMA 8.3）
        LearningBehavior behavior = new LearningBehavior();
        behavior.setUserId(userId);
        behavior.setPathId(pathId);
        behavior.setStudyDuration(studyDuration);
        behavior.setMasteryScore(masteryScore);
        behavior.setCreatedAt(LocalDateTime.now());
        learningBehaviorRepository.save(behavior);

        // upsert 进度：分钟累加、mastery 覆盖、progress 重算（GAMMA 8.3）
        UserPathProgress progress = userPathProgressRepository
                .findByUserIdAndPathId(userId, pathId)
                .orElseGet(() -> {
                    UserPathProgress p = new UserPathProgress();
                    p.setUserId(userId);
                    p.setPathId(pathId);
                    p.setTotalStudyMinutes(0);
                    return p;
                });
        progress.setTotalStudyMinutes(progress.getTotalStudyMinutes() + studyDuration);
        progress.setMasteryScore(masteryScore);
        progress.setProgress(computeProgress(progress.getTotalStudyMinutes(), path.getEstimatedMinutes()));
        progress.setUpdatedAt(LocalDateTime.now());
        userPathProgressRepository.save(progress);

        return toProgressResponse(progress, path);
    }

    /**
     * ★ 单路径进度（BETA 4.5）。无记录时返回零值进度。
     */
    public PathProgressResponse getProgress(Long userId, Long pathId) {
        if (userId == null) {
            throw new LearningApiException(1001, "userId 不能为空");
        }
        LearningPath path = learningPathRepository.findById(pathId)
                .orElseThrow(() -> new LearningApiException(1005, "学习路径不存在"));

        Optional<UserPathProgress> progressOpt =
                userPathProgressRepository.findByUserIdAndPathId(userId, pathId);
        if (progressOpt.isPresent()) {
            return toProgressResponse(progressOpt.get(), path);
        }
        PathProgressResponse dto = new PathProgressResponse();
        dto.setPathId(path.getId());
        dto.setPathCode(path.getPathCode());
        dto.setTitle(path.getTitle());
        dto.setMasteryScore(0);
        dto.setProgress(0);
        dto.setTotalStudyMinutes(0);
        return dto;
    }

    /**
     * ☆ 分析汇总（GAMMA 8.4）。
     */
    public AnalyticsSummaryResponse analyticsSummary(Long userId) {
        if (userId == null) {
            throw new LearningApiException(1001, "userId 不能为空");
        }
        int totalMinutes = 0;
        for (LearningBehavior b : learningBehaviorRepository.findByUserIdOrderByCreatedAtDesc(userId)) {
            totalMinutes += b.getStudyDuration();
        }

        List<UserPathProgress> progresses = userPathProgressRepository.findByUserId(userId);
        Map<Long, LearningPath> pathById = new HashMap<>();
        for (LearningPath p : learningPathRepository.findAll()) {
            pathById.put(p.getId(), p);
        }

        int completed = 0;
        int inProgress = 0;
        List<UserPathProgress> unfinished = new ArrayList<>();
        for (UserPathProgress p : progresses) {
            if (p.getProgress() >= PROGRESS_COMPLETE) {
                completed++;
            } else {
                inProgress++;
                unfinished.add(p);
            }
        }

        // 未完成中 mastery 最低 Top3
        unfinished.sort(Comparator
                .comparingInt(UserPathProgress::getMasteryScore)
                .thenComparing(UserPathProgress::getPathId));
        List<AnalyticsSummaryResponse.WeakestPathItem> weakest = new ArrayList<>();
        for (int i = 0; i < unfinished.size() && i < 3; i++) {
            UserPathProgress p = unfinished.get(i);
            LearningPath path = pathById.get(p.getPathId());
            weakest.add(new AnalyticsSummaryResponse.WeakestPathItem(
                    p.getPathId(),
                    path != null ? path.getTitle() : null,
                    p.getMasteryScore()));
        }

        AnalyticsSummaryResponse summary = new AnalyticsSummaryResponse();
        summary.setTotalStudyMinutes(totalMinutes);
        summary.setCompletedPathCount(completed);
        summary.setInProgressPathCount(inProgress);
        summary.setWeakestPaths(weakest);
        return summary;
    }

    /**
     * ☆ 最近学习动态。
     */
    public List<BehaviorResponse> recentBehaviors(Long userId, int limit) {
        if (userId == null) {
            throw new LearningApiException(1001, "userId 不能为空");
        }
        Map<Long, LearningPath> pathById = new HashMap<>();
        for (LearningPath p : learningPathRepository.findAll()) {
            pathById.put(p.getId(), p);
        }
        List<LearningBehavior> behaviors = learningBehaviorRepository.findByUserIdOrderByCreatedAtDesc(userId);
        List<BehaviorResponse> result = new ArrayList<>();
        for (int i = 0; i < behaviors.size() && i < limit; i++) {
            LearningBehavior b = behaviors.get(i);
            LearningPath path = pathById.get(b.getPathId());
            BehaviorResponse dto = new BehaviorResponse();
            dto.setId(b.getId());
            dto.setPathId(b.getPathId());
            dto.setPathCode(path != null ? path.getPathCode() : null);
            dto.setTitle(path != null ? path.getTitle() : null);
            dto.setStudyDuration(b.getStudyDuration());
            dto.setMasteryScore(b.getMasteryScore());
            dto.setCreatedAt(b.getCreatedAt());
            result.add(dto);
        }
        return result;
    }

    /**
     * ☆ 路径主数据（仅 active）。
     */
    public List<LearningPathResponse> listPaths() {
        List<LearningPathResponse> result = new ArrayList<>();
        List<LearningPath> paths = learningPathRepository.findByActiveTrue();
        paths.sort(Comparator.comparing(LearningPath::getId));
        for (LearningPath path : paths) {
            LearningPathResponse dto = new LearningPathResponse();
            dto.setPathId(path.getId());
            dto.setPathCode(path.getPathCode());
            dto.setTitle(path.getTitle());
            dto.setSummary(path.getSummary());
            dto.setDifficulty(path.getDifficulty());
            dto.setEstimatedMinutes(path.getEstimatedMinutes());
            result.add(dto);
        }
        return result;
    }

    /**
     * 由累计分钟与预估分钟推算进度百分比，封顶 100（progress>=100 判定完成，GAMMA 8.1.3）。
     */
    private int computeProgress(int totalStudyMinutes, Integer estimatedMinutes) {
        if (estimatedMinutes == null || estimatedMinutes <= 0) {
            return Math.min(PROGRESS_COMPLETE, totalStudyMinutes);
        }
        long pct = Math.round(totalStudyMinutes * 100.0 / estimatedMinutes);
        if (pct > PROGRESS_COMPLETE) {
            return PROGRESS_COMPLETE;
        }
        return (int) pct;
    }

    private PathProgressResponse toProgressResponse(UserPathProgress progress, LearningPath path) {
        PathProgressResponse dto = new PathProgressResponse();
        dto.setPathId(progress.getPathId());
        dto.setPathCode(path.getPathCode());
        dto.setTitle(path.getTitle());
        dto.setMasteryScore(progress.getMasteryScore());
        dto.setProgress(progress.getProgress());
        dto.setTotalStudyMinutes(progress.getTotalStudyMinutes());
        return dto;
    }
}
