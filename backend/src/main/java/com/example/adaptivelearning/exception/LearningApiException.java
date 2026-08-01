package com.example.adaptivelearning.exception;

/**
 * 学习域业务异常：携带非 0 业务码，统一走 code/message/data 包络。
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
