package com.example.adaptivelearning.service;

import com.example.adaptivelearning.dto.BehaviorReportRequest;
import com.example.adaptivelearning.dto.PathProgressResponse;
import com.example.adaptivelearning.dto.PathRecommendationResponse;
import com.example.adaptivelearning.exception.LearningApiException;
import com.example.adaptivelearning.model.LearningPath;
import com.example.adaptivelearning.model.UserPathProgress;
import com.example.adaptivelearning.repository.LearningBehaviorRepository;
import com.example.adaptivelearning.repository.LearningPathRepository;
import com.example.adaptivelearning.repository.UserPathProgressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LearningServiceTest {

    @Mock
    private LearningPathRepository learningPathRepository;

    @Mock
    private LearningBehaviorRepository learningBehaviorRepository;

    @Mock
    private UserPathProgressRepository userPathProgressRepository;

    @InjectMocks
    private LearningService learningService;

    private LearningPath path1;
    private LearningPath path2;
    private LearningPath path3;

    @BeforeEach
    void setUp() {
        path1 = newPath(1L, "PATH-001", 31);
        path2 = newPath(2L, "PATH-002", 32);
        path3 = newPath(3L, "PATH-003", 33);
    }

    private LearningPath newPath(Long id, String code, int estimated) {
        LearningPath p = new LearningPath();
        p.setId(id);
        p.setPathCode(code);
        p.setTitle("实战课 " + code);
        p.setSummary("summary");
        p.setDifficulty(2);
        p.setEstimatedMinutes(estimated);
        p.setActive(true);
        return p;
    }

    private UserPathProgress newProgress(Long pathId, int mastery, int progress, int minutes) {
        UserPathProgress p = new UserPathProgress();
        p.setUserId(1L);
        p.setPathId(pathId);
        p.setMasteryScore(mastery);
        p.setProgress(progress);
        p.setTotalStudyMinutes(minutes);
        return p;
    }

    @Test
    void testRecommend_SortsByMasteryAscAndFiltersCompleted() {
        when(learningPathRepository.findByActiveTrue())
                .thenReturn(Arrays.asList(path1, path2, path3));
        // path1 mastery 50; path2 completed (progress>=100) 应过滤; path3 无记录视作 0
        when(userPathProgressRepository.findByUserId(1L)).thenReturn(Arrays.asList(
                newProgress(1L, 50, 40, 20),
                newProgress(2L, 90, 100, 40)
        ));

        List<PathRecommendationResponse> result = learningService.recommend(1L);

        assertEquals(2, result.size(), "已完成路径必须被过滤");
        assertEquals(3L, result.get(0).getPathId(), "mastery 0 的 path3 排首位");
        assertEquals(0, result.get(0).getMasteryScore());
        assertEquals(1L, result.get(1).getPathId());
        assertEquals(50, result.get(1).getMasteryScore());
    }

    @Test
    void testRecommend_NullUserId_Fails() {
        assertThrows(LearningApiException.class, () -> learningService.recommend(null));
    }

    @Test
    void testReport_RejectsNonPositiveDuration_NoWrite() {
        BehaviorReportRequest req = new BehaviorReportRequest();
        req.setUserId(1L);
        req.setPathId(1L);
        req.setStudyDuration(0);
        req.setMasteryScore(50);

        assertThrows(LearningApiException.class, () -> learningService.reportBehavior(req));
        verify(learningBehaviorRepository, never()).save(any());
        verify(userPathProgressRepository, never()).save(any());
    }

    @Test
    void testReport_RejectsMasteryOutOfRange_NoWrite() {
        BehaviorReportRequest req = new BehaviorReportRequest();
        req.setUserId(1L);
        req.setPathId(1L);
        req.setStudyDuration(10);
        req.setMasteryScore(101);

        assertThrows(LearningApiException.class, () -> learningService.reportBehavior(req));
        verify(learningBehaviorRepository, never()).save(any());
        verify(userPathProgressRepository, never()).save(any());
    }

    @Test
    void testReport_AccumulatesMinutesOverwritesMasteryAndUpdatesProgress() {
        BehaviorReportRequest req = new BehaviorReportRequest();
        req.setUserId(1L);
        req.setPathId(1L);
        req.setStudyDuration(20);
        req.setMasteryScore(70);

        when(learningPathRepository.findById(1L)).thenReturn(Optional.of(path1));
        when(userPathProgressRepository.findByUserIdAndPathId(1L, 1L))
                .thenReturn(Optional.of(newProgress(1L, 30, 30, 11)));
        when(userPathProgressRepository.save(any(UserPathProgress.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        PathProgressResponse resp = learningService.reportBehavior(req);

        // 累加：11 + 20 = 31 分钟；path1 预估 31 -> progress 100
        assertEquals(31, resp.getTotalStudyMinutes());
        assertEquals(70, resp.getMasteryScore(), "mastery 应被覆盖");
        assertEquals(100, resp.getProgress());
        verify(learningBehaviorRepository).save(any());
        verify(userPathProgressRepository).save(any());
    }

    @Test
    void testReport_PathNotFound_Fails() {
        BehaviorReportRequest req = new BehaviorReportRequest();
        req.setUserId(1L);
        req.setPathId(99L);
        req.setStudyDuration(10);
        req.setMasteryScore(50);

        when(learningPathRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(LearningApiException.class, () -> learningService.reportBehavior(req));
        verify(learningBehaviorRepository, never()).save(any());
    }

    @Test
    void testGetProgress_NoRecord_ReturnsZeroValues() {
        when(learningPathRepository.findById(1L)).thenReturn(Optional.of(path1));
        when(userPathProgressRepository.findByUserIdAndPathId(1L, 1L))
                .thenReturn(Optional.empty());

        PathProgressResponse resp = learningService.getProgress(1L, 1L);

        assertEquals(0, resp.getProgress());
        assertEquals(0, resp.getMasteryScore());
        assertEquals(0, resp.getTotalStudyMinutes());
    }

    @Test
    void testAnalyticsSummary_ComputesCountsAndWeakestTop3() {
        when(learningBehaviorRepository.findByUserIdOrderByCreatedAtDesc(1L))
                .thenReturn(Collections.emptyList());
        when(userPathProgressRepository.findByUserId(1L)).thenReturn(Arrays.asList(
                newProgress(1L, 80, 100, 40), // completed
                newProgress(2L, 20, 30, 10),  // in progress, weakest
                newProgress(3L, 60, 50, 20)   // in progress
        ));
        when(learningPathRepository.findAll()).thenReturn(Arrays.asList(path1, path2, path3));

        var summary = learningService.analyticsSummary(1L);

        assertEquals(1, summary.getCompletedPathCount());
        assertEquals(2, summary.getInProgressPathCount());
        assertEquals(2, summary.getWeakestPaths().size());
        assertEquals(2L, summary.getWeakestPaths().get(0).getPathId(), "mastery 最低者排首");
    }
}
