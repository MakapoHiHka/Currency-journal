package com.openSolutions.currencyJournal.domain.pojo;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.*;

/**
 * DTO для представления валюты из XML ЦБ
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CbrCurrencyDto {

    @JacksonXmlProperty(isAttribute = true, localName = "ID")
    private String id;

    @JacksonXmlProperty(localName = "NumCode")
    private Integer numCode;

    @JacksonXmlProperty(localName = "CharCode")
    private String charCode;

    @JacksonXmlProperty(localName = "Nominal")
    private Long nominal;

    @JacksonXmlProperty(localName = "Name")
    private String name;

    @JacksonXmlProperty(localName = "Value")
    private String value;

}
