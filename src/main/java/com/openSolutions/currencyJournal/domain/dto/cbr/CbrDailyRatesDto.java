package com.openSolutions.currencyJournal.domain.dto.cbr;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * DTO для представления ежедневных курсов валют из XML ЦБ
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JacksonXmlRootElement(localName = "ValCurs")
public class CbrDailyRatesDto {

    /**
     * Дата обновления курсов (атрибут Date корневого элемента)
     * Формат: "dd.MM.yyyy"
     */
    @JacksonXmlProperty(isAttribute = true, localName = "Date")
    private String date;

    /**
     * Название (атрибут name корневого элемента)
     */
    @JacksonXmlProperty(isAttribute = true, localName = "name")
    private String name;

    /**
     * Список валют
     */
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "Valute")
    private List<CbrCurrencyDto> currencies;

    /**
     * Получить дату обновления как LocalDateTime
     * Возвращает начало дня указанной даты
     */
    public LocalDateTime getUpdated() {
        if (date == null || date.isEmpty()) {
            return LocalDateTime.now();
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            return LocalDate.parse(date, formatter).atStartOfDay();
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }
}