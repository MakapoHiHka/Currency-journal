package com.openSolutions.currencyJournal.service;

import com.openSolutions.currencyJournal.domain.dto.response.CountryDtoResponse;

import java.util.List;

public interface CountryService {
    List<CountryDtoResponse> getCountries();
}
