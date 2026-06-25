package com.openSolutions.currencyJournal.service.interfaces;

import com.openSolutions.currencyJournal.domain.dto.response.StatusResponse;

public interface CbrSyncService {
    long synchronizeWithCbr();
    StatusResponse getStatusInfo();
}
