package com.openSolutions.currencyJournal.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Обработка бизнес-исключений (например, если курс не найден)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex, WebRequest request) {
        log.error("Бизнес-ошибка: {}", ex.getMessage());

        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("message", ex.getMessage());
        body.put("timestamp", LocalDateTime.now());

        // Если это ошибка "не найдено", можно вернуть 404, иначе 400
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // Обработка всех остальных непредвиденных ошибок
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception ex, WebRequest request) {
        log.error("Неожиданная ошибка: {}", ex.getMessage(), ex);

        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("message", "Внутренняя ошибка сервера");
        body.put("timestamp", LocalDateTime.now());

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}