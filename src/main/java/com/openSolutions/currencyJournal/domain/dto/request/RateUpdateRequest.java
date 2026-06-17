package com.openSolutions.currencyJournal.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO для запроса редактирования курса валюты
 */
@Getter
@Setter
@AllArgsConstructor
public class RateUpdateRequest {

    @NotNull(message = "Номинал не может быть null")
    private Long id;

    @NotNull(message = "Номинал не может быть null")
    @Positive(message = "Номинал должен быть положительным числом")
    private Long nominal;

    @NotNull(message = "Значение курса не может быть null")
    @Positive(message = "Значение курса должно быть положительным числом")
    private BigDecimal value;

}
