package com.openSolutions.currencyJournal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusResponse {
    private String status;
    private String service;
    private LocalDateTime timestamp;
    private String notes;
}