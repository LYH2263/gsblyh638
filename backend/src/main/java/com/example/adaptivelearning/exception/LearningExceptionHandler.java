package com.example.adaptivelearning.exception;

import com.example.adaptivelearning.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 学习域异常处理：把 LearningApiException 转成统一包络（BETA 4.1/4.7）。
 * 仅拦截学习域自定义异常，不影响既有用户注册/登录（ALPHA 兼容）。
 */
@RestControllerAdvice(basePackages = "com.example.adaptivelearning.controller")
public class LearningExceptionHandler {

    @ExceptionHandler(LearningApiException.class)
    public ResponseEntity<ApiResponse<Object>> handleLearningApiException(LearningApiException ex) {
        // 非法输入必须失败（非 0 code），不得部分写入（GAMMA 8.5）。
        return ResponseEntity.ok(ApiResponse.error(ex.getCode(), ex.getMessage()));
    }
}
