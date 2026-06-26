package com.openSolutions.currencyJournal.service;

import com.openSolutions.currencyJournal.domain.dto.response.CbrDailyRatesDtoResponse;


public interface RateProcessorService {
    void processCurrencies(CbrDailyRatesDtoResponse currencies);
}
