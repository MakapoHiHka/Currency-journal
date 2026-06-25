package com.openSolutions.currencyJournal.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SyncResponse {
    private boolean success;
    private String message;
    private long durationMs;
}