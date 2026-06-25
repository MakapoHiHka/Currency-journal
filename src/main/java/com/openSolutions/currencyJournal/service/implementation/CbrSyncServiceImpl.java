package com.openSolutions.currencyJournal.service.implementation;

import com.openSolutions.currencyJournal.domain.pojo.CbrDailyRatesDto;
import com.openSolutions.currencyJournal.domain.dto.response.StatusResponse;
import com.openSolutions.currencyJournal.exceptions.BadCbrResponseException;
import com.openSolutions.currencyJournal.property.CbrApiProperty;
import com.openSolutions.currencyJournal.service.CbrSyncService;
import com.openSolutions.currencyJournal.service.RateProcessorService;
import com.openSolutions.currencyJournal.property.SyncProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class CbrSyncServiceImpl implements CbrSyncService {

    private final RateProcessorService rateProcessorService;
    private final SyncProperty syncProperty;
    private final CbrApiProperty cbrApiProperty;
    private final RestTemplate restTemplate;

    /**
     * Ручная синхронизация курсов валют с ЦБ
     */
    public long synchronizeWithCbr() {
        long startTime = System.currentTimeMillis();
        ResponseEntity<CbrDailyRatesDto> response = restTemplate.getForEntity(cbrApiProperty.getUrl(), CbrDailyRatesDto.class);
        //проверить ответ на успешность
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new BadCbrResponseException("Не удалось получить данные с ЦБ. Status: " + response.getStatusCode());
        }
        rateProcessorService.processCurrencies(response.getBody());
        return System.currentTimeMillis() - startTime;
    }


    public StatusResponse getStatusInfo(){
        return new StatusResponse("UP", "Currency Journal API", LocalDateTime.now(), syncProperty.isEnabled(), syncProperty.getIntervalCron());
    }
}