package com.example.adaptivelearning.exception;

import com.example.adaptivelearning.controller.LearningController;
import com.example.adaptivelearning.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * 学习域异常统一走 code/message/data 包络（规格书第四章 BETA）。
 * 仅作用于 LearningController，不影响既有用户域接口。
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(assignableTypes = LearningController.class)
public class LearningExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(LearningExceptionHandler.class);

    @ExceptionHandler(LearningApiException.class)
    public ApiResponse<Void> handleLearningApiException(LearningApiException ex) {
        log.warn("Learning domain rejected request: code={}, message={}", ex.getCode(), ex.getMessage());
        return ApiResponse.error(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiResponse<Void> handleNotReadable(HttpMessageNotReadableException ex) {
        return ApiResponse.error(400, "请求体格式非法：studyDuration/masteryScore 必须为整数，userId/pathId 必须为数值");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ApiResponse<Void> handleMissingParam(MissingServletRequestParameterException ex) {
        return ApiResponse.error(400, "缺少必填参数: " + ex.getParameterName());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ApiResponse<Void> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return ApiResponse.error(400, "参数类型非法: " + ex.getName());
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleException(Exception ex) {
        log.error("Learning domain unexpected error", ex);
        return ApiResponse.error(500, "学习域服务异常: " + ex.getMessage());
    }
}
