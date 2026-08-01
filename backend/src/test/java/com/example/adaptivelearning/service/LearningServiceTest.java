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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
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

    private LearningPath buildPath(Long id, String code, int estimatedMinutes) {
        LearningPath path = new LearningPath();
        path.setId(id);
        path.setPathCode(code);
        path.setTitle("实战课单元 " + id);
        path.setSummary("summary-" + code);
        path.setDifficulty(3);
        path.setEstimatedMinutes(estimatedMinutes);
        path.setActive(true);
        return path;
    }

    private UserPathProgress buildProgress(Long userId, Long pathId, int mastery, int progress, int totalMinutes) {
        UserPathProgress record = new UserPathProgress();
        record.setUserId(userId);
        record.setPathId(pathId);
        record.setMasteryScore(mastery);
        record.setProgress(progress);
        record.setTotalStudyMinutes(totalMinutes);
        record.setUpdatedAt(LocalDateTime.now());
        return record;
    }

    @Test
    void testRecommendations_SortAscFilterCompletedAndMissingAsZero() {
        List<LearningPath> paths = Arrays.asList(
                buildPath(1L, "PATH-001", 30),
                buildPath(2L, "PATH-002", 30),
                buildPath(3L, "PATH-003", 30));
        when(learningPathRepository.findByActiveTrue()).thenReturn(paths);
        when(userPathProgressRepository.findByUserId(7L)).thenReturn(Arrays.asList(
                buildProgress(7L, 1L, 50, 50, 15),
                buildProgress(7L, 2L, 90, 100, 40)));

        List<PathRecommendationResponse> result = learningService.getRecommendations(7L);

        // path2 完成被过滤；path3 无记录 mastery 视作 0 排最前（薄弱优先）
        assertEquals(2, result.size());
        assertEquals(3L, result.get(0).getPathId());
        assertEquals(0, result.get(0).getMasteryScore());
        assertEquals(1L, result.get(1).getPathId());
        assertEquals(50, result.get(1).getMasteryScore());
    }

    @Test
    void testRecommendations_NullUserIdRejected() {
        LearningApiException ex = assertThrows(LearningApiException.class,
                () -> learningService.getRecommendations(null));
        assertNotEquals(0, ex.getCode());
    }

    @Test
    void testReportBehavior_AccumulatesMinutesOverwritesMasteryUpdatesProgress() {
        LearningPath path = buildPath(1L, "PATH-001", 40);
        when(learningPathRepository.findById(1L)).thenReturn(Optional.of(path));
        when(userPathProgressRepository.findByUserIdAndPathId(7L, 1L))
                .thenReturn(Optional.of(buildProgress(7L, 1L, 30, 25, 10)));
        when(learningBehaviorRepository.save(any(LearningBehavior.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(userPathProgressRepository.save(any(UserPathProgress.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BehaviorReportRequest request = new BehaviorReportRequest();
        request.setUserId(7L);
        request.setPathId(1L);
        request.setStudyDuration(20);
        request.setMasteryScore(66);

        PathProgressResponse dto = learningService.reportBehavior(request);

        assertEquals(30, dto.getTotalStudyMinutes()); // 10 + 20 累加
        assertEquals(66, dto.getMasteryScore());      // 覆盖
        assertEquals(75, dto.getProgress());          // 30/40 = 75%
        verify(learningBehaviorRepository).save(any(LearningBehavior.class));
    }

    @Test
    void testReportBehavior_ProgressCappedAt100() {
        LearningPath path = buildPath(1L, "PATH-001", 30);
        when(learningPathRepository.findById(1L)).thenReturn(Optional.of(path));
        when(userPathProgressRepository.findByUserIdAndPathId(7L, 1L))
                .thenReturn(Optional.empty());
        when(userPathProgressRepository.save(any(UserPathProgress.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BehaviorReportRequest request = new BehaviorReportRequest();
        request.setUserId(7L);
        request.setPathId(1L);
        request.setStudyDuration(60);
        request.setMasteryScore(100);

        PathProgressResponse dto = learningService.reportBehavior(request);

        assertEquals(100, dto.getProgress());
        assertEquals(60, dto.getTotalStudyMinutes());
    }

    @Test
    void testReportBehavior_InvalidDurationRejectedWithoutPartialWrite() {
        BehaviorReportRequest request = new BehaviorReportRequest();
        request.setUserId(7L);
        request.setPathId(1L);
        request.setStudyDuration(0);
        request.setMasteryScore(50);

        LearningApiException ex = assertThrows(LearningApiException.class,
                () -> learningService.reportBehavior(request));
        assertNotEquals(0, ex.getCode());
        verify(learningBehaviorRepository, never()).save(any());
        verify(userPathProgressRepository, never()).save(any());
    }

    @Test
    void testReportBehavior_InvalidMasteryRejectedWithoutPartialWrite() {
        BehaviorReportRequest request = new BehaviorReportRequest();
        request.setUserId(7L);
        request.setPathId(1L);
        request.setStudyDuration(10);
        request.setMasteryScore(101);

        LearningApiException ex = assertThrows(LearningApiException.class,
                () -> learningService.reportBehavior(request));
        assertNotEquals(0, ex.getCode());
        verify(learningBehaviorRepository, never()).save(any());
        verify(userPathProgressRepository, never()).save(any());
    }

    @Test
    void testReportBehavior_PathNotFound() {
        when(learningPathRepository.findById(99L)).thenReturn(Optional.empty());

        BehaviorReportRequest request = new BehaviorReportRequest();
        request.setUserId(7L);
        request.setPathId(99L);
        request.setStudyDuration(10);
        request.setMasteryScore(50);

        LearningApiException ex = assertThrows(LearningApiException.class,
                () -> learningService.reportBehavior(request));
        assertEquals(404, ex.getCode());
        verify(learningBehaviorRepository, never()).save(any());
    }

    @Test
    void testGetProgress_NoRecordReturnsZeros() {
        LearningPath path = buildPath(2L, "PATH-002", 32);
        when(learningPathRepository.findById(2L)).thenReturn(Optional.of(path));
        when(userPathProgressRepository.findByUserIdAndPathId(7L, 2L))
                .thenReturn(Optional.empty());

        PathProgressResponse dto = learningService.getProgress(2L, 7L);

        assertEquals(0, dto.getMasteryScore());
        assertEquals(0, dto.getProgress());
        assertEquals(0, dto.getTotalStudyMinutes());
        assertEquals("PATH-002", dto.getPathCode());
    }

    @Test
    void testAnalyticsSummary_AggregatesAndWeakestTop3() {
        LearningBehavior b1 = new LearningBehavior();
        b1.setUserId(7L);
        b1.setPathId(1L);
        b1.setStudyDuration(20);
        b1.setMasteryScore(40);
        LearningBehavior b2 = new LearningBehavior();
        b2.setUserId(7L);
        b2.setPathId(2L);
        b2.setStudyDuration(25);
        b2.setMasteryScore(90);
        when(learningBehaviorRepository.findByUserId(7L)).thenReturn(Arrays.asList(b1, b2));
        when(userPathProgressRepository.findByUserId(7L)).thenReturn(Arrays.asList(
                buildProgress(7L, 1L, 40, 60, 20),
                buildProgress(7L, 2L, 90, 100, 25)));
        when(learningPathRepository.findByActiveTrue()).thenReturn(Arrays.asList(
                buildPath(1L, "PATH-001", 40),
                buildPath(2L, "PATH-002", 30),
                buildPath(3L, "PATH-003", 30),
                buildPath(4L, "PATH-004", 30),
                buildPath(5L, "PATH-005", 30)));

        AnalyticsSummaryResponse summary = learningService.getAnalyticsSummary(7L);

        assertEquals(45, summary.getTotalStudyMinutes()); // 20+25 行为时长求和
        assertEquals(1, summary.getCompletedPathCount()); // path2 progress=100
        assertEquals(1, summary.getInProgressPathCount()); // path1 有进度未完成
        // 未完成中 mastery 最低 Top3：path3/4/5 无记录视作 0（按 pathId 升序），path1=40 排最后
        assertEquals(3, summary.getWeakestPaths().size());
        assertEquals(3L, summary.getWeakestPaths().get(0).getPathId());
        assertEquals(0, summary.getWeakestPaths().get(0).getMasteryScore());
        assertEquals(4L, summary.getWeakestPaths().get(1).getPathId());
        assertEquals(5L, summary.getWeakestPaths().get(2).getPathId());
        assertTrue(summary.getWeakestPaths().stream().noneMatch(item -> item.getPathId() == 2L));
    }

    @Test
    void testAnalyticsSummary_NullUserIdRejected() {
        LearningApiException ex = assertThrows(LearningApiException.class,
                () -> learningService.getAnalyticsSummary(null));
        assertNotEquals(0, ex.getCode());
    }
}
