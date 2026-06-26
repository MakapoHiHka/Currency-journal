package com.openSolutions.currencyJournal.controller;

import com.openSolutions.currencyJournal.domain.dto.response.ApiResponse;
import com.openSolutions.currencyJournal.domain.dto.response.CountryDtoResponse;
import com.openSolutions.currencyJournal.service.CountryService;
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
@RequestMapping("/api/country")
@RequiredArgsConstructor
@Tag(name = "Country", description = "Справочник стран")
public class CountryController {

    private final CountryService countryService;

    @GetMapping("/countries")
    @Operation(summary = "Получение справочника стран")
    public ResponseEntity<ApiResponse<List<CountryDtoResponse>>> getCountries() {
        log.debug("Запрос справочника стран");
        return ResponseEntity.ok(ApiResponse.success(countryService.getCountries()));
    }
}
