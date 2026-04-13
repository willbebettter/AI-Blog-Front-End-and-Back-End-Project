package com.ai.backend.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Object> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("参数异常: {}", e.getMessage());
        return Result.error(400, e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Object> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常: {}", e.getMessage(), e);
        return Result.error(500, "服务器内部错误: " + e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Object> handleException(Exception e) {
        log.error("未知异常: {}", e.getMessage(), e);
        return Result.error(500, "服务器内部错误");
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Object> handleDataIntegrityViolation(org.springframework.dao.DataIntegrityViolationException e) {
        log.warn("数据完整性异常: {}", e.getMessage());
        String message = "数据操作失败";
        if (e.getMessage() != null && e.getMessage().contains("Data too long")) {
            message = "数据过长，请检查输入内容";
        } else if (e.getMessage() != null && e.getMessage().contains("Duplicate entry")) {
            message = "数据已存在，请勿重复提交";
        }
        return Result.error(400, message);
    }

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Object> handleValidationException(org.springframework.web.bind.MethodArgumentNotValidException e) {
        log.warn("参数校验失败: {}", e.getMessage());
        return Result.error(400, "参数校验失败: " + e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("未知错误"));
    }
}
