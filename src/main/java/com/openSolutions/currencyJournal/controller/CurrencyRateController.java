package com.openSolutions.currencyJournal.controller;

import com.openSolutions.currencyJournal.dto.ApiResponse;
import com.openSolutions.currencyJournal.dto.PageResponse;
import com.openSolutions.currencyJournal.dto.RateDto;
import com.openSolutions.currencyJournal.dto.RateUpdateRequest;
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
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/currency/rates")
@RequiredArgsConstructor
@Tag(name = "Currency Rates", description = "Операции с курсами валют")
public class CurrencyRateController {

    private final CurrencyRateService currencyRateService;

    @GetMapping
    @Operation(summary = "Получение журнала курса валют с пагинацией и фильтрами")
    public ResponseEntity<ApiResponse<PageResponse<RateDto>>> getRates(
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

        // Используем фабричный метод для преобразования Page в наш DTO
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(ratesPage)));
    }

    @GetMapping("/today")
    @Operation(summary = "Получение курсов валют за сегодня")
    public ResponseEntity<ApiResponse<PageResponse<RateDto>>> getTodayRates(
            @Parameter(description = "Номер страницы")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Размер страницы")
            @RequestParam(defaultValue = "54") int size,

            // используем поле, которое есть в RateEntity
            @Parameter(description = "Поле для сортировки (rateDate, currencyId, value)")
            @RequestParam(defaultValue = "currencyId") String sortBy,

            @Parameter(description = "Направление сортировки")
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.debug("Запрос курсов за сегодня: page={}, size={}, sortBy={}", page, size, sortBy);

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<RateDto> ratesPage = currencyRateService.getTodayRates(pageable);

        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(ratesPage)));
    }

    @GetMapping("/latest/{currencyId}")
    @Operation(summary = "Получение последнего курса валюты")
    public ResponseEntity<ApiResponse<RateDto>> getLatestRate(@PathVariable String currencyId) {
        log.debug("Запрос последнего курса для {}", currencyId);

        return currencyRateService.getLatestRate(currencyId)
                .map(rate -> ResponseEntity.ok(ApiResponse.success(rate)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping
    @Operation(summary = "Редактирование курса валюты")
    public ResponseEntity<ApiResponse<RateDto>> updateRate(@Valid @RequestBody RateUpdateRequest request) {
        log.info("Редактирование курса ID={}", request.getId());

        // Сервис должен возвращать DTO. Если сейчас возвращается Entity, нужно применить MapStruct или ModelMapper.
        RateDto updatedRate = currencyRateService.updateRate(request);

        return ResponseEntity.ok(ApiResponse.success("Курс валюты успешно обновлен", updatedRate));
    }
}