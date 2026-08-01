package com.example.adaptivelearning.exception;

/**
 * 学习域业务异常，携带非 0 错误码（BETA 4.7 / GAMMA 8.5）。
 * 由 LearningExceptionHandler 转成统一包络返回。
 */
public class LearningApiException extends RuntimeException {
    private final int code;

    public LearningApiException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
