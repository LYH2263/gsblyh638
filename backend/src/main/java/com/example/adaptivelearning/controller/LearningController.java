package com.example.adaptivelearning.controller;

import com.example.adaptivelearning.dto.AnalyticsSummaryResponse;
import com.example.adaptivelearning.dto.ApiResponse;
import com.example.adaptivelearning.dto.BehaviorReportRequest;
import com.example.adaptivelearning.dto.BehaviorResponse;
import com.example.adaptivelearning.dto.LearningPathResponse;
import com.example.adaptivelearning.dto.PathProgressResponse;
import com.example.adaptivelearning.dto.PathRecommendationResponse;
import com.example.adaptivelearning.service.LearningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 学习域接口（BETA 4.1）：统一前缀 /api/learning，统一包络 {code,message,data}。
 * Controller 只注入 Service，不直接注入 Repository（BETA 4.3）。
 */
@RestController
@RequestMapping("/api/learning")
@CrossOrigin(origins = "http://localhost:5173") // 与既有 UserController 对齐
public class LearningController {

    @Autowired
    private LearningService learningService;

    /** ★ 推荐列表 */
    @GetMapping("/paths/recommendations")
    public ApiResponse<List<PathRecommendationResponse>> recommendations(@RequestParam Long userId) {
        return ApiResponse.ok(learningService.recommend(userId));
    }

    /** ★ 上报行为 */
    @PostMapping("/behaviors")
    public ApiResponse<PathProgressResponse> reportBehavior(@RequestBody BehaviorReportRequest request) {
        return ApiResponse.ok(learningService.reportBehavior(request));
    }

    /** ★ 路径进度 */
    @GetMapping("/paths/{pathId}/progress")
    public ApiResponse<PathProgressResponse> progress(@PathVariable Long pathId,
                                                      @RequestParam Long userId) {
        return ApiResponse.ok(learningService.getProgress(userId, pathId));
    }

    /** ☆ 分析汇总 */
    @GetMapping("/analytics/summary")
    public ApiResponse<AnalyticsSummaryResponse> analyticsSummary(@RequestParam Long userId) {
        return ApiResponse.ok(learningService.analyticsSummary(userId));
    }

    /** ☆ 最近动态 */
    @GetMapping("/behaviors/recent")
    public ApiResponse<List<BehaviorResponse>> recentBehaviors(@RequestParam Long userId,
                                                               @RequestParam(defaultValue = "10") int limit) {
        return ApiResponse.ok(learningService.recentBehaviors(userId, limit));
    }

    /** ☆ 路径主数据 */
    @GetMapping("/paths")
    public ApiResponse<List<LearningPathResponse>> paths() {
        return ApiResponse.ok(learningService.listPaths());
    }
}
