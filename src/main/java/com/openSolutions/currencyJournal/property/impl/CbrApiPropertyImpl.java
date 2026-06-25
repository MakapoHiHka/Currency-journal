package com.openSolutions.currencyJournal.property.impl;

import com.openSolutions.currencyJournal.property.CbrApiProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ConfigurationProperties(prefix = "cbr.api")
@Component
public class CbrApiPropertyImpl implements CbrApiProperty {
    private String url;
}
