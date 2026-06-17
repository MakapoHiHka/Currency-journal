package com.openSolutions.currencyJournal.domain.dto.cbr;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO для представления ежедневных курсов валют из XML ЦБ
 */
@Data
public class CbrDailyRatesDto {

    private LocalDateTime updated;

    private List<CbrCurrencyDto> currencies;
}
