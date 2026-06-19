package com.openSolutions.currencyJournal.scheduler;

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
     * fixedRateString использует SpEL (#{...}), чтобы умножить секунды из properties на 1000 (миллисекунды).
     * По умолчанию: 3600 секунд (1 час)
     */
    @Scheduled(fixedRateString = "#{${sync.interval.seconds:3600} * 1000}")
    public void syncWithCbr() {
        log.info("Запуск автоматической синхронизации с ЦБ");

        try {
            long startTime = System.currentTimeMillis();
            int count = cbrSyncService.synchronizeWithCbr();
            long duration = System.currentTimeMillis() - startTime;

            log.info("Автоматическая синхронизация завершена успешно. Обновлено курсов: {}, время: {} мс", count, duration);
        } catch (Exception e) {
            // Ловим исключение, чтобы падение одной синхронизации не останавливало весь планировщик
            log.error("Ошибка при автоматической синхронизации: {}", e.getMessage(), e);
        }
    }
}