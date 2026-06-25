package com.openSolutions.currencyJournal.scheduler;

import com.openSolutions.currencyJournal.service.interfaces.CbrSyncService;
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
            long startTime = System.currentTimeMillis();
            long duration = cbrSyncService.synchronizeWithCbr();
            //long duration = System.currentTimeMillis() - startTime;

            log.info("Автоматическая синхронизация завершена успешно. Время: {} мс", duration);
        } catch (Exception e) {
            // Ловим исключение, чтобы падение одной синхронизации не останавливало весь планировщик
            log.error("Ошибка при автоматической синхронизации: {}", e.getMessage(), e);
        }
    }
}