package com.openSolutions.currencyJournal.service;

import com.openSolutions.currencyJournal.domain.dto.request.RateSearchRequest;
import com.openSolutions.currencyJournal.domain.dto.request.RateUpdateRequest;
import com.openSolutions.currencyJournal.domain.dto.response.RateDtoResponse;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface RateService {
    Page<RateDtoResponse> getRates(RateSearchRequest request);
    Optional<RateDtoResponse> getLatestRate(String currencyId);
    RateDtoResponse updateRate(RateUpdateRequest request);
}
