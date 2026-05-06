package com.tom.tradereview.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 全局异常出口。
 *
 * <p>前端 http 拦截器主要读取 message/detail 字段展示错误提示，所以这里把后端抛出的业务异常
 * 统一包装成稳定的 JSON 结构，避免每个 Controller 重复写 try/catch。</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Service 层常用 ResponseStatusException 表达“数据不存在”“参数非法”等 HTTP 语义。
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(ResponseStatusException exception) {
        HttpStatus status = HttpStatus.valueOf(exception.getStatusCode().value());
        String message = exception.getReason() == null ? status.getReasonPhrase() : exception.getReason();
        return ResponseEntity.status(status).body(errorBody(status, message));
    }

    /**
     * 兜底处理参数类异常，统一返回 400，方便前端按同一种结构读取错误信息。
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException exception) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = exception.getMessage() == null ? status.getReasonPhrase() : exception.getMessage();
        return ResponseEntity.status(status).body(errorBody(status, message));
    }

    /**
     * 兼容前端不同位置读取 message/detail 的习惯；detail 与 message 保持一致。
     */
    private Map<String, Object> errorBody(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("detail", message);
        return body;
    }
}
