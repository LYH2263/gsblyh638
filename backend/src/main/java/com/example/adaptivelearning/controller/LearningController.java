package com.example.adaptivelearning.controller;

import com.example.adaptivelearning.dto.AnalyticsSummaryResponse;
import com.example.adaptivelearning.dto.ApiResponse;
import com.example.adaptivelearning.dto.BehaviorReportRequest;
import com.example.adaptivelearning.dto.PathProgressResponse;
import com.example.adaptivelearning.dto.PathRecommendationResponse;
import com.example.adaptivelearning.service.LearningService;
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
@CrossOrigin(origins = "http://localhost:5173") // Allow Vue dev server
public class LearningController {

    private static final Logger log = LoggerFactory.getLogger(LearningController.class);

    @Autowired
    private LearningService learningService;

    /** ★ 推荐列表：薄弱优先升序，过滤已完成路径 */
    @GetMapping("/paths/recommendations")
    public ApiResponse<List<PathRecommendationResponse>> recommendations(@RequestParam Long userId) {
        log.info("Received recommendations request for userId: {}", userId);
        return ApiResponse.ok(learningService.getRecommendations(userId));
    }

    /** ★ 上报学习行为：写行为 + upsert 进度，返回进度 DTO */
    @PostMapping("/behaviors")
    public ApiResponse<PathProgressResponse> reportBehavior(@RequestBody BehaviorReportRequest request) {
        log.info("Received behavior report for userId: {}, pathId: {}", request.getUserId(), request.getPathId());
        return ApiResponse.ok(learningService.reportBehavior(request));
    }

    /** ★ 单路径进度查询 */
    @GetMapping("/paths/{pathId}/progress")
    public ApiResponse<PathProgressResponse> progress(@PathVariable Long pathId, @RequestParam Long userId) {
        log.info("Received progress request for userId: {}, pathId: {}", userId, pathId);
        return ApiResponse.ok(learningService.getProgress(pathId, userId));
    }

    /** ☆ 分析汇总：学习时长/完成数/学习中/最薄弱 Top3 */
    @GetMapping("/analytics/summary")
    public ApiResponse<AnalyticsSummaryResponse> analyticsSummary(@RequestParam Long userId) {
        log.info("Received analytics summary request for userId: {}", userId);
        return ApiResponse.ok(learningService.getAnalyticsSummary(userId));
    }
}
