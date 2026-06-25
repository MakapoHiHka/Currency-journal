package com.openSolutions.currencyJournal.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO для справочника валют
 */
@Setter
@Getter
@NoArgsConstructor
public class RateDictDtoResponse {

    // Идентификатор
    @JsonProperty(index=0)
    private Long id;

    // Название валюты
    private String name;

    // Числовой код
    private Integer numCode;

    // Символьный код
    private String charCode;

    // Количество записей курса в журнале
    private Long ratesCount;
}