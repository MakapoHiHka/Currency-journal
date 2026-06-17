package com.openSolutions.currencyJournal.controller;

import com.openSolutions.currencyJournal.dto.RateDto;
import com.openSolutions.currencyJournal.dto.RateUpdateRequest;
import com.openSolutions.currencyJournal.entity.RateEntity;
import com.openSolutions.currencyJournal.service.CurrencyRateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/currency/rates")
@RequiredArgsConstructor
@Tag(name = "Currency Rates", description = "Операции с курсами валют")
public class CurrencyRateController {

    private final CurrencyRateService currencyRateService;

    @GetMapping
    @Operation(summary = "Получение журнала курса валют с пагинацией и фильтрами")
    public ResponseEntity<Map<String, Object>> getRates(
            @Parameter(description = "ID валюты") @RequestParam(required = false) String currencyId,
            @Parameter(description = "ID страны") @RequestParam(required = false) Long countryId,
            @Parameter(description = "Начальная дата") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Конечная дата") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "rateDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        log.debug("Запрос курсов: currencyId={}, page={}, size={}", currencyId, page, size);

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<RateDto> ratesPage = currencyRateService.getRates(currencyId, countryId, startDate, endDate, pageable);

        // Примечание: возврат Map - это антипаттерн, см. раздел оптимизации ниже
        Map<String, Object> response = new HashMap<>();
        response.put("content", ratesPage.getContent());
        response.put("totalElements", ratesPage.getTotalElements());
        response.put("totalPages", ratesPage.getTotalPages());
        response.put("number", ratesPage.getNumber());
        response.put("size", ratesPage.getSize());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/today")
    @Operation(summary = "Получение курсов валют за сегодня")
    public ResponseEntity<List<RateDto>> getTodayRates() {
        log.debug("Запрос курсов за сегодня");
        return ResponseEntity.ok(currencyRateService.getTodayRates());
    }

    @GetMapping("/latest/{currencyId}")
    @Operation(summary = "Получение последнего курса валюты")
    public ResponseEntity<Map<String, Object>> getLatestRate(@PathVariable String currencyId) {
        log.debug("Запрос последнего курса для {}", currencyId);

        return currencyRateService.getLatestRate(currencyId)
                .map(rate -> ResponseEntity.ok(Map.of("success", true, "data", rate)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping
    @Operation(summary = "Редактирование курса валюты")
    public ResponseEntity<Map<String, Object>> updateRate(@Valid @RequestBody RateUpdateRequest request) {
        log.info("Редактирование курса ID={}", request.getId());
        RateEntity updatedRate = currencyRateService.updateRate(request);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Курс валюты успешно обновлен",
                "data", updatedRate
        ));
    }
}