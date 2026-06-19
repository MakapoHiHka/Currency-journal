package com.openSolutions.currencyJournal.controller;

import com.openSolutions.currencyJournal.domain.dto.request.RateSearchRequest;
import com.openSolutions.currencyJournal.domain.dto.request.RateUpdateRequest;
import com.openSolutions.currencyJournal.domain.dto.response.ApiResponse;
import com.openSolutions.currencyJournal.domain.dto.response.PageResponse;
import com.openSolutions.currencyJournal.domain.dto.response.RateDtoResponse;
import com.openSolutions.currencyJournal.service.implementation.RateServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/currency/rates")
@RequiredArgsConstructor
@Tag(name = "Currency Rates", description = "Операции с курсами валют")
public class CurrencyRateController {

    private final RateServiceImpl rateServiceImpl;

    @PostMapping
    @Operation(summary = "Получение журнала курса валют с пагинацией и фильтрами")
    public ResponseEntity<ApiResponse<PageResponse<RateDtoResponse>>> getRates(@Valid @RequestBody RateSearchRequest request) {

        log.debug("Запрос курсов: currencyId={}, page={}, size={}",
                request.getCurrencyId(), request.getPage(), request.getSize());

        return ResponseEntity.ok(
                ApiResponse.success(PageResponse.of(rateServiceImpl.getRates(request)))
        );
    }

    @GetMapping("/latest/{currencyId}")
    @Operation(summary = "Получение последнего курса валюты")
    public ResponseEntity<ApiResponse<RateDtoResponse>> getLatestRate(@PathVariable String currencyId) {
        log.debug("Запрос последнего курса для {}", currencyId);

        return rateServiceImpl.getLatestRate(currencyId)
                .map(rate -> ResponseEntity.ok(ApiResponse.success(rate)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping
    @Operation(summary = "Редактирование курса валюты")
    public ResponseEntity<ApiResponse<RateDtoResponse>> updateRate(@Valid @RequestBody RateUpdateRequest request) {

        log.info("Редактирование курса ID={}", request.getId());
        RateDtoResponse updatedRate = rateServiceImpl.updateRate(request);

        return ResponseEntity.ok(ApiResponse.success("Курс валюты успешно обновлен", updatedRate));
    }
}