package com.openSolutions.currencyJournal.service;

import com.openSolutions.currencyJournal.domain.pojo.CbrDailyRatesDto;


public interface RateProcessorService {
    void processCurrencies(CbrDailyRatesDto currencies);
}
