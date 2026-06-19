package com.openSolutions.currencyJournal.service.implementation;

import com.openSolutions.currencyJournal.domain.dto.cbr.CbrCurrencyDto;
import com.openSolutions.currencyJournal.domain.dto.cbr.CbrDailyRatesDto;
import com.openSolutions.currencyJournal.domain.dto.response.StatusResponse;
import com.openSolutions.currencyJournal.parser.CbrXmlParser;
import com.openSolutions.currencyJournal.service.interfaces.CbrSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CbrSyncServiceImpl implements CbrSyncService {

    private final CbrXmlParser xmlParser;
    private final RateProcessorServiceImpl rateProcessorServiceImpl;

    @Value("${cbr.api.url:https://www.cbr-xml-daily.ru/daily_utf8.xml}")
    private String cbrApiUrl;

    @Value("${sync.enabled:false}")
    private boolean isSyncEnabled;

    @Value("${sync.interval.seconds:-1}")
    private int SyncIntervalSeconds;

    /**
     * Ручная синхронизация курсов валют с ЦБ
     */
    @Transactional
    public int synchronizeWithCbr() {
        log.info("Запуск синхронизации курсов валют с ЦБ");
        long startTime = System.currentTimeMillis();

        try {
            CbrDailyRatesDto ratesDto = xmlParser.parseFromUrl(cbrApiUrl);
            LocalDateTime rateDate = ratesDto.getUpdated();
            List<CbrCurrencyDto> currencies = ratesDto.getCurrencies();

            if (currencies == null || currencies.isEmpty()) {
                log.warn("Список валют пуст");
                return 0;
            }

            log.info("Получено {} валют для обработки (дата курса: {})", currencies.size(), rateDate);

            int processedCount = 0;
            int errorCount = 0;

            for (CbrCurrencyDto currency : currencies) {
                try {
                    // Делегируем обработку RateProcessorService
                    rateProcessorServiceImpl.processCurrency(currency, rateDate);
                    processedCount++;
                } catch (Exception e) {
                    errorCount++;
                    log.error("Ошибка при обработке валюты {}: {}",
                            currency.getCharCode(), e.getMessage());
                }
            }

            long duration = System.currentTimeMillis() - startTime;
            log.info("Синхронизация завершена за {} мс. Добавлено: {}, Ошибок: {}",
                    duration, processedCount, errorCount);

            return processedCount;

        } catch (Exception e) {
            log.error("Критическая ошибка при синхронизации", e);
            throw new RuntimeException("Не удалось синхронизировать курсы валют", e);
        }
    }

    public StatusResponse getStatusInfo(){
        return new StatusResponse("UP", "Currency Journal API", LocalDateTime.now(), isSyncEnabled, SyncIntervalSeconds);
    }
}
