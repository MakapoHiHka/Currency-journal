package com.openSolutions.currencyJournal.controller;

import com.openSolutions.currencyJournal.domain.dto.response.StatusResponse;
import com.openSolutions.currencyJournal.domain.dto.response.SyncResponse;
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

@Slf4j
@RestController
@RequestMapping("/api/currency")
@RequiredArgsConstructor
@Tag(name = "System", description = "Системные операции и синхронизация")
public class CurrencySyncController {

    private final CurrencyRateService currencyRateService;

    @PostMapping("/sync")
    @Operation(summary = "Ручная синхронизация курсов валют с ЦБ")
    public ResponseEntity<SyncResponse> synchronizeWithCbr() {
        log.info("Запуск ручной синхронизации с ЦБ");

        long startTime = System.currentTimeMillis();
        int count = currencyRateService.synchronizeWithCbr();
        long duration = System.currentTimeMillis() - startTime;

        SyncResponse response = new SyncResponse(true, "Синхронизация успешно выполнена", count, duration);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status")
    @Operation(summary = "Проверка работоспособности")
    public ResponseEntity<StatusResponse> statusCheck() {
        StatusResponse response = new StatusResponse("UP", "Currency Journal API", LocalDateTime.now(), "Тут будет информация об автоматической синхронизации");
        return ResponseEntity.ok(response);
    }
}