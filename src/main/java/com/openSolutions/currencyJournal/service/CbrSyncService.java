package com.openSolutions.currencyJournal.service;

import com.openSolutions.currencyJournal.domain.dto.response.StatusResponse;
import com.openSolutions.currencyJournal.domain.dto.response.CbrDailyRatesDtoResponse;

public interface CbrSyncService {
    CbrDailyRatesDtoResponse synchronizeWithCbr();
    StatusResponse getStatusInfo();
}
