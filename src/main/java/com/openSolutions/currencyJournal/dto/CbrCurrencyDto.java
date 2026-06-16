package com.openSolutions.currencyJournal.dto;

import lombok.*;

/**
 * DTO для представления валюты из XML ЦБ
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CbrCurrencyDto {

    private String id;

    private Integer numCode;

    private String charCode;

    private Long nominal;

    private String name;

    private String value;
}
