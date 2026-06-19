package com.openSolutions.currencyJournal.domain.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StatusResponse {
    private String status;
    private String service;
    private LocalDateTime timestamp;
    private boolean isAutoSyncEnabled;
    private String autoSyncIntervalCron;
}