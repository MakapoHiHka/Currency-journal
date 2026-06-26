package com.openSolutions.currencyJournal.controller;

import com.openSolutions.currencyJournal.domain.dto.response.ApiResponse;
import com.openSolutions.currencyJournal.domain.dto.response.RateDictDtoResponse;
import com.openSolutions.currencyJournal.service.RateDictService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/rate-dict")
@RequiredArgsConstructor
@Tag(name = "RateDict", description = "Справочники валют")
public class RateDictController {

    private final RateDictService rateDictService;

    @GetMapping("/dict")
    @Operation(summary = "Получение справочника валют")
    public ResponseEntity<ApiResponse<List<RateDictDtoResponse>>> getRateDict() {
        log.debug("Запрос справочника валют");
        return ResponseEntity.ok(ApiResponse.success(rateDictService.getRateDict()));
    }

}
