package openSolutions.currencyJournal.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO для журнала курса валют
 * Содержит только необходимые данные без циклических ссылок
 */
@Setter
@Getter
@NoArgsConstructor
public class RateDto {

    // Идентификатор записи
    private Long id;

    // ID валюты из ЦБ (например, R01235)
    private String currencyId;

    // ID страны
    private Long countryId;

    // Название страны
    private String countryName;

    // ID справочника валют
    private Long rateDictId;

    // Название валюты
    private String rateDictName;

    // Символьный код валюты (например, USD)
    private String charCode;

    // Числовой код валюты
    private Integer numCode;

    // Номинальное значение
    private Long nominal;

    // Значение курса
    private BigDecimal value;

    // Дата и время курса
    private LocalDateTime rateDate;

    // Дата создания записи
    private LocalDateTime created;

    // Дата обновления записи
    private LocalDateTime updated;
}