package com.example.adaptivelearning.service;

import com.example.adaptivelearning.dto.AnalyticsSummaryDTO;
import com.example.adaptivelearning.dto.BehaviorRequest;
import com.example.adaptivelearning.dto.ProgressDTO;
import com.example.adaptivelearning.dto.RecommendationDTO;
import com.example.adaptivelearning.dto.WeakPathDTO;
import com.example.adaptivelearning.exception.BusinessException;
import com.example.adaptivelearning.model.LearningBehavior;
import com.example.adaptivelearning.model.LearningPath;
import com.example.adaptivelearning.model.UserPathProgress;
import com.example.adaptivelearning.repository.LearningBehaviorRepository;
import com.example.adaptivelearning.repository.LearningPathRepository;
import com.example.adaptivelearning.repository.UserPathProgressRepository;
import com.example.adaptivelearning.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class LearningService {

    @Autowired
    private LearningPathRepository learningPathRepository;

    @Autowired
    private LearningBehaviorRepository learningBehaviorRepository;

    @Autowired
    private UserPathProgressRepository userPathProgressRepository;

    @Autowired
    private UserRepository userRepository;

    public List<RecommendationDTO> getRecommendations(Long userId) {
        if (userId == null) {
            throw new BusinessException(1001, "userId 不能为空");
        }
        if (!userRepository.existsById(userId)) {
            throw new BusinessException(1002, "用户不存在");
        }

        List<LearningPath> activePaths = learningPathRepository.findByActiveTrue();
        List<UserPathProgress> userProgressList = userPathProgressRepository.findByUserId(userId);

        Map<Long, UserPathProgress> progressMap = new HashMap<>();
        for (UserPathProgress p : userProgressList) {
            progressMap.put(p.getPathId(), p);
        }

        List<RecommendationDTO> recommendations = new ArrayList<>();
        for (LearningPath path : activePaths) {
            UserPathProgress progress = progressMap.get(path.getId());
            int currentProgress = progress != null ? progress.getProgress() : 0;

            if (currentProgress >= 100) {
                continue;
            }

            int masteryScore = progress != null ? progress.getMasteryScore() : 0;
            int totalMinutes = progress != null ? progress.getTotalStudyMinutes() : 0;

            RecommendationDTO dto = new RecommendationDTO();
            dto.setPathId(path.getId());
            dto.setPathCode(path.getPathCode());
            dto.setTitle(path.getTitle());
            dto.setSummary(path.getSummary());
            dto.setDifficulty(path.getDifficulty());
            dto.setEstimatedMinutes(path.getEstimatedMinutes());
            dto.setMasteryScore(masteryScore);
            dto.setProgress(currentProgress);
            dto.setTotalStudyMinutes(totalMinutes);
            dto.setMatchHint(buildMatchHint(masteryScore));
            recommendations.add(dto);
        }

        recommendations.sort(
                Comparator.comparingInt(RecommendationDTO::getMasteryScore)
                        .thenComparingLong(RecommendationDTO::getPathId)
        );

        return recommendations;
    }

    @Transactional
    public ProgressDTO reportBehavior(BehaviorRequest request) {
        Long userId = request.getUserId();
        Long pathId = request.getPathId();
        Integer studyDuration = request.getStudyDuration();
        Integer masteryScore = request.getMasteryScore();

        if (userId == null) {
            throw new BusinessException(1001, "userId 不能为空");
        }
        if (pathId == null) {
            throw new BusinessException(1001, "pathId 不能为空");
        }
        if (studyDuration == null || studyDuration < 1) {
            throw new BusinessException(1001, "studyDuration 必须为正整数（分钟）");
        }
        if (masteryScore == null || masteryScore < 0 || masteryScore > 100) {
            throw new BusinessException(1001, "masteryScore 必须在 0-100 之间");
        }

        if (!userRepository.existsById(userId)) {
            throw new BusinessException(1002, "用户不存在");
        }

        Optional<LearningPath> pathOpt = learningPathRepository.findById(pathId);
        if (pathOpt.isEmpty()) {
            throw new BusinessException(1003, "学习路径不存在");
        }
        LearningPath path = pathOpt.get();
        if (!Boolean.TRUE.equals(path.getActive())) {
            throw new BusinessException(1003, "学习路径不可用");
        }

        LearningBehavior behavior = new LearningBehavior();
        behavior.setUserId(userId);
        behavior.setPathId(pathId);
        behavior.setStudyDuration(studyDuration);
        behavior.setMasteryScore(masteryScore);
        learningBehaviorRepository.save(behavior);

        UserPathProgress progress = userPathProgressRepository
                .findByUserIdAndPathId(userId, pathId)
                .orElse(new UserPathProgress());

        if (progress.getId() == null) {
            progress.setUserId(userId);
            progress.setPathId(pathId);
            progress.setTotalStudyMinutes(studyDuration);
        } else {
            progress.setTotalStudyMinutes(progress.getTotalStudyMinutes() + studyDuration);
        }

        progress.setMasteryScore(masteryScore);

        int newProgress = 0;
        if (path.getEstimatedMinutes() != null && path.getEstimatedMinutes() > 0) {
            newProgress = Math.min(100,
                    (progress.getTotalStudyMinutes() * 100) / path.getEstimatedMinutes());
        }
        progress.setProgress(newProgress);

        userPathProgressRepository.save(progress);

        return toProgressDTO(path, progress);
    }

    public ProgressDTO getPathProgress(Long userId, Long pathId) {
        if (userId == null) {
            throw new BusinessException(1001, "userId 不能为空");
        }
        if (pathId == null) {
            throw new BusinessException(1001, "pathId 不能为空");
        }
        if (!userRepository.existsById(userId)) {
            throw new BusinessException(1002, "用户不存在");
        }

        Optional<LearningPath> pathOpt = learningPathRepository.findById(pathId);
        if (pathOpt.isEmpty()) {
            throw new BusinessException(1003, "学习路径不存在");
        }
        LearningPath path = pathOpt.get();

        UserPathProgress progress = userPathProgressRepository
                .findByUserIdAndPathId(userId, pathId)
                .orElse(null);

        if (progress == null) {
            progress = new UserPathProgress();
            progress.setUserId(userId);
            progress.setPathId(pathId);
            progress.setMasteryScore(0);
            progress.setProgress(0);
            progress.setTotalStudyMinutes(0);
        }

        return toProgressDTO(path, progress);
    }

    public AnalyticsSummaryDTO getAnalyticsSummary(Long userId) {
        if (userId == null) {
            throw new BusinessException(1001, "userId 不能为空");
        }
        if (!userRepository.existsById(userId)) {
            throw new BusinessException(1002, "用户不存在");
        }

        List<LearningBehavior> behaviors = learningBehaviorRepository.findByUserIdOrderByCreatedAtDesc(userId);
        int totalStudyMinutes = 0;
        for (LearningBehavior b : behaviors) {
            totalStudyMinutes += b.getStudyDuration();
        }

        List<UserPathProgress> allProgress = userPathProgressRepository.findByUserId(userId);

        int completedCount = 0;
        int inProgressCount = 0;
        List<UserPathProgress> incompleteProgress = new ArrayList<>();

        for (UserPathProgress p : allProgress) {
            if (p.getProgress() >= 100) {
                completedCount++;
            } else {
                inProgressCount++;
                incompleteProgress.add(p);
            }
        }

        incompleteProgress.sort(
                Comparator.comparingInt(UserPathProgress::getMasteryScore)
                        .thenComparingLong(UserPathProgress::getPathId)
        );

        List<WeakPathDTO> weakestPaths = new ArrayList<>();
        int limit = Math.min(3, incompleteProgress.size());
        for (int i = 0; i < limit; i++) {
            UserPathProgress p = incompleteProgress.get(i);
            LearningPath path = learningPathRepository.findById(p.getPathId()).orElse(null);
            if (path != null) {
                WeakPathDTO dto = new WeakPathDTO();
                dto.setPathId(p.getPathId());
                dto.setTitle(path.getTitle());
                dto.setMasteryScore(p.getMasteryScore());
                weakestPaths.add(dto);
            }
        }

        AnalyticsSummaryDTO summary = new AnalyticsSummaryDTO();
        summary.setTotalStudyMinutes(totalStudyMinutes);
        summary.setCompletedPathCount(completedCount);
        summary.setInProgressPathCount(inProgressCount);
        summary.setWeakestPaths(weakestPaths);
        return summary;
    }

    private ProgressDTO toProgressDTO(LearningPath path, UserPathProgress progress) {
        ProgressDTO dto = new ProgressDTO();
        dto.setPathId(path.getId());
        dto.setPathCode(path.getPathCode());
        dto.setTitle(path.getTitle());
        dto.setMasteryScore(progress.getMasteryScore());
        dto.setProgress(progress.getProgress());
        dto.setTotalStudyMinutes(progress.getTotalStudyMinutes());
        return dto;
    }

    private String buildMatchHint(int masteryScore) {
        if (masteryScore < 30) {
            return "薄弱优先";
        } else if (masteryScore < 60) {
            return "稳步提升";
        } else {
            return "巩固进阶";
        }
    }
}
