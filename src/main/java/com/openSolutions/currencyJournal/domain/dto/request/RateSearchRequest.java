package com.openSolutions.currencyJournal.domain.dto.request;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * DTO для запроса журнала курса валют с фильтрами и пагинацией.
 * Используется в GET /api/currency/rates
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RateSearchRequest extends PageQueryRequest {

    /** ID валюты из ЦБ (например, R01235) */
    private String currencyId;

    /** ID страны */
    private Long countryId;

    /** Начальная дата периода */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDate;

    /** Конечная дата периода */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDate;
}