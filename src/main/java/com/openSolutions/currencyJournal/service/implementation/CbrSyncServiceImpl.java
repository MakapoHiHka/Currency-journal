package com.openSolutions.currencyJournal.service.implementation;

import com.openSolutions.currencyJournal.domain.pojo.CbrDailyRatesDto;
import com.openSolutions.currencyJournal.domain.dto.response.StatusResponse;
import com.openSolutions.currencyJournal.parser.CbrXmlParser;
import com.openSolutions.currencyJournal.service.interfaces.CbrSyncService;
import com.openSolutions.currencyJournal.service.interfaces.RateProcessorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class CbrSyncServiceImpl implements CbrSyncService {

    private final CbrXmlParser xmlParser;
    private final RateProcessorService rateProcessorService;

    @Value("${cbr.api.url:https://www.cbr-xml-daily.ru/daily_utf8.xml}")
    private String cbrApiUrl;

    @Value("${sync.enabled:false}")
    private boolean isSyncEnabled;

    @Value("${sync.interval.cron:-1}")
    private String SyncIntervalCron;

    /**
     * Ручная синхронизация курсов валют с ЦБ
     */
    public long synchronizeWithCbr() {
        long startTime = System.currentTimeMillis();
        CbrDailyRatesDto ratesDto = xmlParser.parseFromUrl(cbrApiUrl);
        rateProcessorService.processCurrencies(ratesDto);
        return System.currentTimeMillis() - startTime;
    }


    public StatusResponse getStatusInfo(){
        return new StatusResponse("UP", "Currency Journal API", LocalDateTime.now(), isSyncEnabled, SyncIntervalCron);
    }
}