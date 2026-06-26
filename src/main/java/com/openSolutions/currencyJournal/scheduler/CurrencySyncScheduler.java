package com.openSolutions.currencyJournal.scheduler;

import com.openSolutions.currencyJournal.domain.dto.response.CbrDailyRatesDtoResponse;
import com.openSolutions.currencyJournal.service.CbrSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
// Планировщик создастся только если sync.enabled = true (или если свойства нет, так как matchIfMissing = true)
@ConditionalOnProperty(name = "sync.enabled", havingValue = "true", matchIfMissing = true)
public class CurrencySyncScheduler {

    private final CbrSyncService cbrSyncService;

    /**
     * Автоматическая синхронизация курсов валют.
     */
    @Scheduled(cron = "${sync.interval-cron}")
    public void syncWithCbr() {
        log.info("Запуск автоматической синхронизации с ЦБ");

        try {
            CbrDailyRatesDtoResponse body = cbrSyncService.synchronizeWithCbr();

            log.info("Автоматическая синхронизация завершена успешно. Получено валют: {}", body.getCurrencies().size());
        } catch (Exception e) {
            // Ловим исключение, чтобы падение одной синхронизации не останавливало весь планировщик
            log.error("Ошибка при автоматической синхронизации: {}", e.getMessage(), e);
        }
    }
}