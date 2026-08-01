package com.example.adaptivelearning.controller;

import com.example.adaptivelearning.dto.AnalyticsSummaryDTO;
import com.example.adaptivelearning.dto.ApiResponse;
import com.example.adaptivelearning.dto.BehaviorRequest;
import com.example.adaptivelearning.dto.ProgressDTO;
import com.example.adaptivelearning.dto.RecommendationDTO;
import com.example.adaptivelearning.service.LearningService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/learning")
@CrossOrigin(origins = "http://localhost:5173")
public class LearningController {

    private static final Logger log = LoggerFactory.getLogger(LearningController.class);

    @Autowired
    private LearningService learningService;

    @GetMapping("/paths/recommendations")
    public ResponseEntity<ApiResponse<List<RecommendationDTO>>> getRecommendations(
            @RequestParam Long userId) {
        log.info("Getting recommendations for userId: {}", userId);
        List<RecommendationDTO> recommendations = learningService.getRecommendations(userId);
        return ResponseEntity.ok(ApiResponse.success(recommendations));
    }

    @PostMapping("/behaviors")
    public ResponseEntity<ApiResponse<ProgressDTO>> reportBehavior(
            @Valid @RequestBody BehaviorRequest request) {
        log.info("Reporting behavior for userId: {}, pathId: {}", request.getUserId(), request.getPathId());
        ProgressDTO progress = learningService.reportBehavior(request);
        return ResponseEntity.ok(ApiResponse.success(progress));
    }

    @GetMapping("/paths/{pathId}/progress")
    public ResponseEntity<ApiResponse<ProgressDTO>> getPathProgress(
            @PathVariable Long pathId,
            @RequestParam Long userId) {
        log.info("Getting progress for userId: {}, pathId: {}", userId, pathId);
        ProgressDTO progress = learningService.getPathProgress(userId, pathId);
        return ResponseEntity.ok(ApiResponse.success(progress));
    }

    @GetMapping("/analytics/summary")
    public ResponseEntity<ApiResponse<AnalyticsSummaryDTO>> getAnalyticsSummary(
            @RequestParam Long userId) {
        log.info("Getting analytics summary for userId: {}", userId);
        AnalyticsSummaryDTO summary = learningService.getAnalyticsSummary(userId);
        return ResponseEntity.ok(ApiResponse.success(summary));
    }
}
