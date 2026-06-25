package com.openSolutions.currencyJournal.service;

import com.openSolutions.currencyJournal.domain.dto.response.StatusResponse;

public interface CbrSyncService {
    long synchronizeWithCbr();
    StatusResponse getStatusInfo();
}
