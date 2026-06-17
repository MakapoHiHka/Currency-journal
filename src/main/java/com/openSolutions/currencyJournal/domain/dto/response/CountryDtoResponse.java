package com.openSolutions.currencyJournal.domain.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CountryDtoResponse {
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
