package com.openSolutions.currencyJournal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SyncResponse {
    private boolean success;
    private String message;
    private int currenciesProcessed;
    private long durationMs;
}