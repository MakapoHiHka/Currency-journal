package com.openSolutions.currencyJournal.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * DTO для запроса журнала курса валют с фильтрами и пагинацией.
 * Используется в GET /api/currency/rates
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RateSearchRequest extends PageQueryRequest {

    /** ID валюты из ЦБ (R01235) */
    @Schema(
            description = "ID валюты из ЦБ (например R01535)",
            example = "R01535",
            defaultValue = ""
    )
    private String currencyId;

    /** ID страны */
    @Schema(
            description = "ID страны из справочника",
            example = "9",
            defaultValue = ""
    )
    private Long countryId;

    /** Начальная дата периода */
    @Schema(
            description = "Начальная дата периода (включительно)",
            example = "2026-06-01",
            defaultValue = ""
    )
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    /** Конечная дата периода */
    @Schema(
            description = "Конечная дата периода (включительно)",
            example = "2026-06-24",
            defaultValue = ""
    )
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;
}