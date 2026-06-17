package com.openSolutions.currencyJournal.controller;

import com.openSolutions.currencyJournal.service.CurrencyRateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/currency")
@RequiredArgsConstructor
@Tag(name = "System", description = "Системные операции и синхронизация")
public class CurrencySyncController {

    private final CurrencyRateService currencyRateService;

    @PostMapping("/sync")
    @Operation(summary = "Ручная синхронизация курсов валют с ЦБ")
    public ResponseEntity<Map<String, Object>> synchronizeWithCbr() {
        log.info("Запуск ручной синхронизации с ЦБ");

        long startTime = System.currentTimeMillis();
        int count = currencyRateService.synchronizeWithCbr();
        long duration = System.currentTimeMillis() - startTime;

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Синхронизация успешно выполнена");
        response.put("currenciesProcessed", count);
        response.put("durationMs", duration);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/status")
    @Operation(summary = "Проверка работоспособности")
    public ResponseEntity<Map<String, Object>> statusCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("notes", "здесь будет информация об автоматической синхронизации");
        response.put("status", "UP");
        response.put("service", "Currency Journal API");
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.ok(response);
    }
}