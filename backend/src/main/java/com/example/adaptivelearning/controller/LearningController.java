package com.example.adaptivelearning.controller;

import com.example.adaptivelearning.dto.ApiResponse;
import com.example.adaptivelearning.dto.AnalyticsSummaryVO;
import com.example.adaptivelearning.dto.BehaviorReportRequest;
import com.example.adaptivelearning.dto.PathProgressVO;
import com.example.adaptivelearning.dto.RecommendationItemVO;
import com.example.adaptivelearning.service.LearningService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/learning")
@CrossOrigin(origins = "http://localhost:5173")
public class LearningController {

    private static final Logger log = LoggerFactory.getLogger(LearningController.class);

    @Autowired
    private LearningService learningService;

    @GetMapping("/paths/recommendations")
    public ApiResponse<List<RecommendationItemVO>> recommendations(@RequestParam("userId") Long userId) {
        log.info("Get recommendations for userId: {}", userId);
        List<RecommendationItemVO> data = learningService.getRecommendations(userId);
        return ApiResponse.ok(data);
    }

    @PostMapping("/behaviors")
    public ApiResponse<PathProgressVO> reportBehavior(@Valid @RequestBody BehaviorReportRequest request) {
        log.info("Report behavior userId={}, pathId={}, duration={}, mastery={}",
                request.getUserId(), request.getPathId(),
                request.getStudyDuration(), request.getMasteryScore());
        PathProgressVO data = learningService.reportBehavior(request);
        return ApiResponse.ok(data);
    }

    @GetMapping("/paths/{pathId}/progress")
    public ApiResponse<PathProgressVO> pathProgress(@PathVariable("pathId") Long pathId,
                                                    @RequestParam("userId") Long userId) {
        log.info("Get path progress userId={}, pathId={}", userId, pathId);
        PathProgressVO data = learningService.getPathProgress(userId, pathId);
        return ApiResponse.ok(data);
    }

    @GetMapping("/analytics/summary")
    public ApiResponse<AnalyticsSummaryVO> analyticsSummary(@RequestParam("userId") Long userId) {
        log.info("Get analytics summary for userId: {}", userId);
        AnalyticsSummaryVO data = learningService.getAnalyticsSummary(userId);
        return ApiResponse.ok(data);
    }
}
