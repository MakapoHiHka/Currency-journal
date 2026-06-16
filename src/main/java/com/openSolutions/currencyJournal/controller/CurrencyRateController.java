package com.openSolutions.currencyJournal.controller;

import com.openSolutions.currencyJournal.dto.CountryDto;
import com.openSolutions.currencyJournal.dto.RateDictDto;
import com.openSolutions.currencyJournal.dto.RateDto;
import com.openSolutions.currencyJournal.dto.RateUpdateRequest;
import jakarta.validation.Valid;
import com.openSolutions.currencyJournal.dto.*;
import com.openSolutions.currencyJournal.entity.RateEntity;
import com.openSolutions.currencyJournal.service.CurrencyRateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/currency")
@Tag(name = "Currency Rates API", description = "API для управления курсами валют ЦБ РФ")
public class CurrencyRateController {

    private static final Logger log = LoggerFactory.getLogger(CurrencyRateController.class);

    private final CurrencyRateService currencyRateService;

    public CurrencyRateController(CurrencyRateService currencyRateService) {
        this.currencyRateService = currencyRateService;
    }

    @PostMapping("/sync")
    @Operation(summary = "Ручная синхронизация курсов валют с ЦБ")
    public ResponseEntity<Map<String, Object>> synchronizeWithCbr() {
        log.info("POST /api/currency/sync");

        try {
            long startTime = System.currentTimeMillis();
            int count = currencyRateService.synchronizeWithCbr();
            long duration = System.currentTimeMillis() - startTime;

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Синхронизация успешно выполнена");
            response.put("currenciesProcessed", count);
            response.put("durationMs", duration);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Ошибка при синхронизации: {}", e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Ошибка синхронизации: " + e.getMessage());

            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/rates")
    @Operation(summary = "Получение журнала курса валют")
    public ResponseEntity<Map<String, Object>> getRates(
            @Parameter(description = "ID валюты")
            @RequestParam(required = false) String currencyId,

            @Parameter(description = "ID страны")
            @RequestParam(required = false) Long countryId,


            @Parameter(description = "Начальная дата")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,

            @Parameter(description = "Конечная дата")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,

            @Parameter(description = "Номер страницы")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Размер страницы")
            @RequestParam(defaultValue = "20") int size,

            @Parameter(description = "Поле для сортировки")
            @RequestParam(defaultValue = "rateDate") String sortBy,

            @Parameter(description = "Направление сортировки")
            @RequestParam(defaultValue = "desc") String sortDir) {

        log.debug("GET /api/currency/rates - currencyId={}, page={}, size={}", currencyId, page, size);

        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<RateDto> ratesPage = currencyRateService.getRates(
                    currencyId, countryId, startDate, endDate, pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("content", ratesPage.getContent());
            response.put("totalElements", ratesPage.getTotalElements());
            response.put("totalPages", ratesPage.getTotalPages());
            response.put("number", ratesPage.getNumber());
            response.put("size", ratesPage.getSize());
            response.put("numberOfElements", ratesPage.getNumberOfElements());
            response.put("first", ratesPage.isFirst());
            response.put("last", ratesPage.isLast());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Ошибка при получении курсов: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/rates/today")
    @Operation(summary = "Получение курсов валют за сегодня")
    public ResponseEntity<List<RateDto>> getTodayRates() {
        log.debug("GET /api/currency/rates/today");

        try {
            List<RateDto> rates = currencyRateService.getTodayRates();
            return ResponseEntity.ok(rates);
        } catch (Exception e) {
            log.error("Ошибка: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/rates/latest/{currencyId}")
    @Operation(summary = "Получение последнего курса валюты")
    public ResponseEntity<Map<String, Object>> getLatestRate(
            @Parameter(description = "ID валюты", required = true)
            @PathVariable String currencyId) {

        log.debug("GET /api/currency/rates/latest/{}", currencyId);

        try {
            return currencyRateService.getLatestRate(currencyId)
                    .map(rate -> {
                        Map<String, Object> response = new HashMap<>();
                        response.put("success", true);
                        response.put("data", rate);
                        return ResponseEntity.ok(response);
                    })
                    .orElseGet(() -> {
                        Map<String, Object> response = new HashMap<>();
                        response.put("success", false);
                        response.put("message", "Курс валюты не найден");
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            log.error("Ошибка: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/dict")
    @Operation(summary = "Получение справочника валют")
    public ResponseEntity<List<RateDictDto>> getRateDict() {
        log.debug("GET /api/currency/dict");

        try {
            List<RateDictDto> rateDict = currencyRateService.getRateDict();
            return ResponseEntity.ok(rateDict);
        } catch (Exception e) {
            log.error("Ошибка: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/countries")
    @Operation(summary = "Получение справочника стран")
    public ResponseEntity<List<CountryDto>> getCountries() {
        log.debug("GET /api/currency/countries");

        try {
            List<CountryDto> countries = currencyRateService.getCountries();
            return ResponseEntity.ok(countries);
        } catch (Exception e) {
            log.error("Ошибка: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/status")
    @Operation(summary = "Проверка работоспособности")
    public ResponseEntity<Map<String, Object>> statusCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Currency Journal API");
        response.put("timestamp", LocalDateTime.now());
        response.put("notes", "здесь будут данные об автоматической синхронизации");

        return ResponseEntity.ok(response);
    }
    /**

     * PUT /api/currency/rates
     * Тело запроса содержит id, nominal и value
     */
    @PutMapping("/rates")
    @Operation(summary = "Редактирование курса валюты")
    public ResponseEntity<Map<String, Object>> updateRate(@Valid @RequestBody RateUpdateRequest request) {

        log.info("PUT /api/currency/rates - Запрос на редактирование курса валюты ID={}", request.getId());

        try {
            RateEntity updatedRate = currencyRateService.updateRate(request);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Курс валюты успешно обновлен");
            response.put("data", updatedRate);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.error("Ошибка при редактировании курса валюты: {}", e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

        } catch (Exception e) {
            log.error("Неожиданная ошибка при редактировании курса валюты: {}", e.getMessage(), e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Внутренняя ошибка сервера");

            return ResponseEntity.internalServerError().body(response);
        }
    }
}