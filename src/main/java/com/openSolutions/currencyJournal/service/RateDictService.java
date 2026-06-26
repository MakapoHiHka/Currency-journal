package com.openSolutions.currencyJournal.service;

import com.openSolutions.currencyJournal.domain.dto.response.RateDictDtoResponse;

import java.util.List;

public interface RateDictService {
    List<RateDictDtoResponse> getRateDict();
}
