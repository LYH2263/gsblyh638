package com.example.adaptivelearning.service;

import com.example.adaptivelearning.dto.AnalyticsSummaryVO;
import com.example.adaptivelearning.dto.BehaviorReportRequest;
import com.example.adaptivelearning.dto.PathProgressVO;
import com.example.adaptivelearning.dto.RecommendationItemVO;
import com.example.adaptivelearning.dto.WeakestPathVO;
import com.example.adaptivelearning.exception.BusinessException;
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
import java.util.stream.Collectors;

@Service
public class LearningService {

    @Autowired
    private LearningPathRepository learningPathRepository;

    @Autowired
    private LearningBehaviorRepository learningBehaviorRepository;

    @Autowired
    private UserPathProgressRepository userPathProgressRepository;

    public List<RecommendationItemVO> getRecommendations(Long userId) {
        if (userId == null) {
            throw new BusinessException("userId 不能为空");
        }

        List<LearningPath> activePaths = learningPathRepository.findByActiveTrueOrderByIdAsc();
        List<UserPathProgress> progressList = userPathProgressRepository.findByUserId(userId);

        Map<Long, UserPathProgress> progressMap = new HashMap<>();
        for (UserPathProgress p : progressList) {
            progressMap.put(p.getPathId(), p);
        }

        List<RecommendationItemVO> result = new ArrayList<>();
        for (LearningPath path : activePaths) {
            UserPathProgress progress = progressMap.get(path.getId());
            if (progress != null && progress.getProgress() != null && progress.getProgress() >= 100) {
                continue;
            }

            RecommendationItemVO vo = new RecommendationItemVO();
            vo.setPathId(path.getId());
            vo.setPathCode(path.getPathCode());
            vo.setTitle(path.getTitle());
            vo.setSummary(path.getSummary());
            vo.setMasteryScore(progress != null ? progress.getMasteryScore() : 0);
            vo.setProgress(progress != null ? progress.getProgress() : 0);
            vo.setTotalStudyMinutes(progress != null ? progress.getTotalStudyMinutes() : 0);
            vo.setMatchHint(buildMatchHint(vo.getMasteryScore()));
            result.add(vo);
        }

        result.sort(Comparator
                .comparingInt(RecommendationItemVO::getMasteryScore)
                .thenComparingLong(RecommendationItemVO::getPathId));
        return result;
    }

    @Transactional
    public PathProgressVO reportBehavior(BehaviorReportRequest request) {
        validateReport(request);

        Long userId = request.getUserId();
        Long pathId = request.getPathId();
        Integer studyDuration = request.getStudyDuration();
        Integer masteryScore = request.getMasteryScore();

        LearningPath path = learningPathRepository.findById(pathId)
                .orElseThrow(() -> new BusinessException("学习路径不存在"));
        if (!Boolean.TRUE.equals(path.getActive())) {
            throw new BusinessException("学习路径未激活");
        }

        LocalDateTime now = LocalDateTime.now();

        LearningBehavior behavior = new LearningBehavior();
        behavior.setUserId(userId);
        behavior.setPathId(pathId);
        behavior.setStudyDuration(studyDuration);
        behavior.setMasteryScore(masteryScore);
        behavior.setCreatedAt(now);
        learningBehaviorRepository.save(behavior);

        UserPathProgress progress = userPathProgressRepository
                .findByUserIdAndPathId(userId, pathId)
                .orElseGet(() -> {
                    UserPathProgress p = new UserPathProgress();
                    p.setUserId(userId);
                    p.setPathId(pathId);
                    p.setMasteryScore(0);
                    p.setProgress(0);
                    p.setTotalStudyMinutes(0);
                    return p;
                });

        int totalMinutes = progress.getTotalStudyMinutes() + studyDuration;
        progress.setTotalStudyMinutes(totalMinutes);
        progress.setMasteryScore(masteryScore);
        progress.setProgress(calculateProgress(totalMinutes, path.getEstimatedMinutes()));
        progress.setUpdatedAt(now);
        userPathProgressRepository.save(progress);

        return toPathProgressVO(path, progress);
    }

    public PathProgressVO getPathProgress(Long userId, Long pathId) {
        if (userId == null) {
            throw new BusinessException("userId 不能为空");
        }
        if (pathId == null) {
            throw new BusinessException("pathId 不能为空");
        }
        LearningPath path = learningPathRepository.findById(pathId)
                .orElseThrow(() -> new BusinessException("学习路径不存在"));

        UserPathProgress progress = userPathProgressRepository
                .findByUserIdAndPathId(userId, pathId)
                .orElseGet(() -> {
                    UserPathProgress p = new UserPathProgress();
                    p.setUserId(userId);
                    p.setPathId(pathId);
                    p.setMasteryScore(0);
                    p.setProgress(0);
                    p.setTotalStudyMinutes(0);
                    p.setUpdatedAt(LocalDateTime.now());
                    return p;
                });

        return toPathProgressVO(path, progress);
    }

    public AnalyticsSummaryVO getAnalyticsSummary(Long userId) {
        if (userId == null) {
            throw new BusinessException("userId 不能为空");
        }

        List<LearningBehavior> behaviors = learningBehaviorRepository.findByUserIdOrderByCreatedAtDesc(userId);
        int totalMinutes = 0;
        for (LearningBehavior b : behaviors) {
            if (b.getStudyDuration() != null) {
                totalMinutes += b.getStudyDuration();
            }
        }

        List<UserPathProgress> progressList = userPathProgressRepository.findByUserId(userId);
        int completedCount = 0;
        int inProgressCount = 0;
        List<UserPathProgress> unfinished = new ArrayList<>();
        for (UserPathProgress p : progressList) {
            int pg = p.getProgress() == null ? 0 : p.getProgress();
            if (pg >= 100) {
                completedCount++;
            } else {
                inProgressCount++;
                unfinished.add(p);
            }
        }

        unfinished.sort(Comparator
                .comparingInt((UserPathProgress p) -> p.getMasteryScore() == null ? 0 : p.getMasteryScore())
                .thenComparingLong(UserPathProgress::getPathId));

        List<Long> pathIds = unfinished.stream()
                .limit(3)
                .map(UserPathProgress::getPathId)
                .collect(Collectors.toList());
        Map<Long, String> titleMap = new HashMap<>();
        if (!pathIds.isEmpty()) {
            List<LearningPath> paths = learningPathRepository.findAllById(pathIds);
            for (LearningPath lp : paths) {
                titleMap.put(lp.getId(), lp.getTitle());
            }
        }

        List<WeakestPathVO> weakest = new ArrayList<>();
        for (UserPathProgress p : unfinished) {
            if (weakest.size() >= 3) {
                break;
            }
            WeakestPathVO vo = new WeakestPathVO();
            vo.setPathId(p.getPathId());
            vo.setTitle(titleMap.get(p.getPathId()));
            vo.setMasteryScore(p.getMasteryScore() == null ? 0 : p.getMasteryScore());
            weakest.add(vo);
        }

        AnalyticsSummaryVO summary = new AnalyticsSummaryVO();
        summary.setTotalStudyMinutes(totalMinutes);
        summary.setCompletedPathCount(completedCount);
        summary.setInProgressPathCount(inProgressCount);
        summary.setWeakestPaths(weakest);
        return summary;
    }

    private void validateReport(BehaviorReportRequest request) {
        if (request == null) {
            throw new BusinessException("请求体不能为空");
        }
        if (request.getUserId() == null) {
            throw new BusinessException("userId 不能为空");
        }
        if (request.getPathId() == null) {
            throw new BusinessException("pathId 不能为空");
        }
        Integer duration = request.getStudyDuration();
        if (duration == null) {
            throw new BusinessException("studyDuration 不能为空");
        }
        if (duration <= 0) {
            throw new BusinessException("studyDuration 必须为正整数（分钟）");
        }
        Integer mastery = request.getMasteryScore();
        if (mastery == null) {
            throw new BusinessException("masteryScore 不能为空");
        }
        if (mastery < 0 || mastery > 100) {
            throw new BusinessException("masteryScore 必须为 0-100 的整数");
        }
    }

    private int calculateProgress(int totalStudyMinutes, Integer estimatedMinutes) {
        if (estimatedMinutes == null || estimatedMinutes <= 0) {
            return 0;
        }
        long raw = (long) totalStudyMinutes * 100L / estimatedMinutes;
        if (raw > 100) {
            raw = 100;
        }
        return (int) raw;
    }

    private PathProgressVO toPathProgressVO(LearningPath path, UserPathProgress progress) {
        PathProgressVO vo = new PathProgressVO();
        vo.setPathId(path.getId());
        vo.setPathCode(path.getPathCode());
        vo.setTitle(path.getTitle());
        vo.setMasteryScore(progress.getMasteryScore());
        vo.setProgress(progress.getProgress());
        vo.setTotalStudyMinutes(progress.getTotalStudyMinutes());
        vo.setCompleted(progress.getProgress() != null && progress.getProgress() >= 100);
        return vo;
    }

    private String buildMatchHint(int masteryScore) {
        if (masteryScore < 30) {
            return "薄弱优先";
        }
        if (masteryScore < 70) {
            return "稳步提升";
        }
        return "巩固进阶";
    }
}
