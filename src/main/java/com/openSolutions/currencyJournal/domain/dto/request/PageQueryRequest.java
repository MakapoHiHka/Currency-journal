package com.openSolutions.currencyJournal.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(
            description = "Номер страницы (начинается с 0)",
            example = "0",
            defaultValue = "0",
            minimum = "0"
    )
    private int page;

    @Min(value = 1, message = "Размер страницы должен быть больше 0")
    @Schema(
            description = "Количество записей на странице",
            example = "20",
            defaultValue = "20",
            minimum = "1"
    )
    private int size;

    @Schema(
            description = "Поле для сортировки",
            example = "rateDate",
            defaultValue = "created",
            allowableValues = {"id", "currencyId", "countryId", "rateDictId", "nominal", "value", "rateDate", "created", "updated", "rateDict.charCode", "rateDict.name", "country.name"}
    )
    private String sortBy = "created";

    @Schema(
            description = "Направление сортировки",
            example = "desc",
            defaultValue = "desc",
            allowableValues = {"asc", "desc"}
    )
    private String sortDir = "desc";
}