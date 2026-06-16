package com.openSolutions.currencyJournal.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO для справочника стран
 */
@Setter
@Getter
@NoArgsConstructor
public class CountryDto {

    // Идентификатор
    private Long id;

    // Название страны
    private String name;

    // Числовой код
    private Integer numCode;

    // Символьный код
    private String charCode;

    // Количество записей курса в журнале
    private Long ratesCount;
}