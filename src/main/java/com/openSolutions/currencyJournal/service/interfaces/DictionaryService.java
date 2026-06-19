package com.openSolutions.currencyJournal.service.interfaces;

import com.openSolutions.currencyJournal.domain.dto.response.CountryDtoResponse;
import com.openSolutions.currencyJournal.domain.dto.response.RateDictDtoResponse;

import java.util.List;

public interface DictionaryService {
    List<RateDictDtoResponse> getRateDict();
    List<CountryDtoResponse> getCountries();
}
