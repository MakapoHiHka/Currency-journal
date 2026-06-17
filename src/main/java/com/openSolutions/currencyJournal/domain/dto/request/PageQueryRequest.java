package com.openSolutions.currencyJournal.domain.dto.request;

import jakarta.validation.constraints.Min;
import lombok.*;

/**
 * Базовый DTO для запросов с пагинацией.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PageQueryRequest {

    @Min(value = 0, message = "Номер страницы не может быть отрицательным")
    private int page = 0;

    @Min(value = 1, message = "Размер страницы должен быть больше 0")
    private int size = 20;

    private String sortBy = "rateDate";

    private String sortDir = "desc";
}