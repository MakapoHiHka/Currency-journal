package com.openSolutions.currencyJournal.service.interfaces;

import com.openSolutions.currencyJournal.domain.dto.cbr.CbrCurrencyDto;
import com.openSolutions.currencyJournal.domain.entity.CountryEntity;
import com.openSolutions.currencyJournal.domain.entity.RateDictEntity;

import java.time.LocalDateTime;

public interface RateProcessorService {
    void processCurrency(CbrCurrencyDto currency, LocalDateTime rateDate);
}
