package com.example.adaptivelearning.exception;

import com.example.adaptivelearning.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthorizedException(UnauthorizedException ex) {
        return new ResponseEntity<>(ApiResponse.error(ex.getCode(), ex.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException ex) {
        return new ResponseEntity<>(ApiResponse.error(ex.getCode(), ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String message = "参数校验失败";
        FieldError fieldError = ex.getBindingResult().getFieldError();
        if (fieldError != null) {
            message = fieldError.getDefaultMessage();
        }
        return new ResponseEntity<>(ApiResponse.error(1001, message), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingParam(MissingServletRequestParameterException ex) {
        return new ResponseEntity<>(ApiResponse.error(1001, "缺少必填参数: " + ex.getParameterName()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return new ResponseEntity<>(ApiResponse.error(1001, "参数类型错误: " + ex.getName()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception ex) {
        return new ResponseEntity<>(ApiResponse.error(1500, "发生未知错误: " + ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
