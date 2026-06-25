package com.openSolutions.currencyJournal.service.interfaces;

import com.openSolutions.currencyJournal.domain.pojo.CbrDailyRatesDto;


public interface RateProcessorService {
    void processCurrencies(CbrDailyRatesDto currencies);
}
