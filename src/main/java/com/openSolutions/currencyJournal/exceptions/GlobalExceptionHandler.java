package com.openSolutions.currencyJournal.exceptions;

import com.openSolutions.currencyJournal.domain.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Обработка ошибок валидации (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.warn("Ошибка валидации данных: {}", ex.getMessage());

        // Собираем все ошибки в удобную структуру: { "fieldName": "error message" }
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        // Формируем ответ
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Ошибка валидации входных данных");
        response.put("errors", errors); // Детальный список ошибок по полям

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // 400 статус
    }

    // неверное поле сортировки
    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidDataAccess(InvalidDataAccessApiUsageException ex) {
        log.warn("Ошибка доступа к данным: {}", ex.getMessage());
        return new ResponseEntity<>(
                ApiResponse.error("Недопустимое поле для сортировки или фильтрации"),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(BadCbrResponseException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCbrResponseException(BadCbrResponseException ex) {
        // Логируем на уровне DEBUG, чтобы не засорять консоль
        log.debug("Ошибка при получении данных с ресурса: {}", ex.getMessage());
        return new ResponseEntity<>(ApiResponse.error("Ошибка при получении данных с ресурса ЦБ"), HttpStatus.SERVICE_UNAVAILABLE);
    }

    //например, нет интернета при парсинге
    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceAccessException(ResourceAccessException ex) {
        log.debug("Нет доступа к ресурсу: {}", ex.getMessage());
        return new ResponseEntity<>(ApiResponse.error("Нет доступа к ресурсу"), HttpStatus.SERVICE_UNAVAILABLE);
    }

    // Обработка бизнес-исключений
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException ex) {
        log.error("Бизнес-ошибка: {}", ex.getMessage());
        // Возвращаем 400 Bad Request
        return new ResponseEntity<>(ApiResponse.error(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }
    // Обработчик для отсутствующих статических ресурсов (favicon.ico и т.д.)
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoResourceFound(NoResourceFoundException ex) {
        // Логируем на уровне DEBUG, чтобы не засорять консоль
        log.debug("Запрошен несуществующий статический ресурс: {}", ex.getResourcePath());
        return new ResponseEntity<>(ApiResponse.error("Ресурс не найден"), HttpStatus.NOT_FOUND);
    }

    // Обработка всех остальных непредвиденных ошибок
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGlobalException(Exception ex) {
        log.error("Неожиданная ошибка: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(ApiResponse.error("Внутренняя ошибка сервера"), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}