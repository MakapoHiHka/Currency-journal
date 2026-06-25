package com.openSolutions.currencyJournal.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ConfigurationProperties(prefix = "sync")
@Component
public class SyncPropertyImpl implements SyncProperty{

    private boolean enabled;
    private String intervalCron;
}
