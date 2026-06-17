package com.openSolutions.currencyJournal.service;

import com.openSolutions.currencyJournal.dto.*;
import com.openSolutions.currencyJournal.entity.CountryEntity;
import com.openSolutions.currencyJournal.entity.RateDictEntity;
import com.openSolutions.currencyJournal.entity.RateEntity;
import com.openSolutions.currencyJournal.repository.CountryRepository;
import com.openSolutions.currencyJournal.repository.RateDictRepository;
import com.openSolutions.currencyJournal.repository.RateRepository;
import lombok.RequiredArgsConstructor;
import com.openSolutions.currencyJournal.dto.*;
import com.openSolutions.currencyJournal.entity.*;
import com.openSolutions.currencyJournal.mapper.RateMapper;
import com.openSolutions.currencyJournal.parser.CbrXmlParser;
import com.openSolutions.currencyJournal.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CurrencyRateService {

    private static final Logger log = LoggerFactory.getLogger(CurrencyRateService.class);

    private final CbrXmlParser xmlParser;
    private final RateDictRepository rateDictRepository;
    private final CountryRepository countryRepository;
    private final RateRepository rateRepository;
    private final RateMapper rateMapper;

    @Value("${cbr.api.url:https://www.cbr-xml-daily.ru/daily_utf8.xml}")
    private String cbrApiUrl;

    /**
     * Ручная синхронизация курсов валют с ЦБ
     */
    @Transactional
    public int synchronizeWithCbr() {
        log.info("Запуск ручной синхронизации курсов валют с ЦБ");
        long startTime = System.currentTimeMillis();

        try {
            // 1. Загружаем XML
            CbrDailyRatesDto ratesDto = xmlParser.parseFromUrl(cbrApiUrl);
            LocalDateTime updateDate = ratesDto.getUpdated();
            List<CbrCurrencyDto> currencies = ratesDto.getCurrencies();
            log.info("Получено {} валют для обработки (дата: {})", currencies.size(), updateDate);

            // 2. Загружаем справочники ОДИН РАЗ
            Map<Integer, RateDictEntity> rateDictByNumCode = loadRateDictByNumCode();
            Map<Integer, CountryEntity> countryByNumCode = loadCountryByNumCode();

            // 3. Собираем новые записи для пакетного сохранения
            List<RateDictEntity> newRateDicts = new ArrayList<>();
            List<CountryEntity> newCountries = new ArrayList<>();

            // 4. Загружаем существующие курсы за дату обновления (один запрос)
            Set<String> existingCurrencyIds = loadExistingCurrencyIds(updateDate);

            // 5. Первый проход: создаём новые справочники (без RateEntity)
            for (CbrCurrencyDto currency : currencies) {
                // Найти или создать справочник валюты
                if (!rateDictByNumCode.containsKey(currency.getNumCode())) {
                    RateDictEntity rateDict = createRateDictEntity(currency);
                    newRateDicts.add(rateDict);
                    // Временно добавляем в Map (без ID, но потом заменим)
                    rateDictByNumCode.put(currency.getNumCode(), rateDict);
                }

                // Найти страну
                if (!countryByNumCode.containsKey(currency.getNumCode())) {
                    CountryEntity country = createCountryEntity(currency);
                    newCountries.add(country);
                    countryByNumCode.put(currency.getNumCode(), country);
                }
            }

            // 6. сохраняем новые справочники (чтобы получить ID)
            if (!newRateDicts.isEmpty()) {
                rateDictRepository.saveAll(newRateDicts);
                log.info("Создано новых записей в справочнике валют: {}", newRateDicts.size());
            }
            if (!newCountries.isEmpty()) {
                countryRepository.saveAll(newCountries);
                log.info("Создано новых записей в справочнике стран: {}", newCountries.size());
            }

            // 7. Второй проход: создаём записи курсов (теперь у справочников есть ID)
            List<RateEntity> newRates = new ArrayList<>();
            int processedCount = 0;
            int errorCount = 0;

            for (CbrCurrencyDto currency : currencies) {
                try {
                    RateDictEntity rateDict = rateDictByNumCode.get(currency.getNumCode());
                    CountryEntity country = countryByNumCode.get(currency.getNumCode());

                    if (rateDict == null || country == null) {
                        log.error("Не найдены справочники для валюты: {}", currency.getCharCode());
                        errorCount++;
                        continue;
                    }

                    // Создать запись курса, если её ещё нет
                    if (!existingCurrencyIds.contains(currency.getId())) {
                        RateEntity rate = createRateEntity(currency, rateDict, country, updateDate);
                        newRates.add(rate);
                        existingCurrencyIds.add(currency.getId());
                    }

                    processedCount++;
                } catch (Exception e) {
                    errorCount++;
                    log.error("Ошибка при обработке валюты {}: {}", currency.getCharCode(), e.getMessage());
                }
            }

            // 8. Сохраняем курсы (теперь foreign key корректны)
            if (!newRates.isEmpty()) {
                rateRepository.saveAll(newRates);
                log.info("Создано новых записей в журнале курсов: {}", newRates.size());
            }

            long duration = System.currentTimeMillis() - startTime;
            log.info("Синхронизация завершена за {} мс. Успешно: {}, Ошибок: {}",
                    duration, processedCount, errorCount);
            return processedCount;

        } catch (Exception e) {
            log.error("Критическая ошибка при синхронизации", e);
            throw new RuntimeException("Не удалось синхронизировать курсы валют", e);
        }
    }

    /**
     * Загрузить все справочники валют в Map по числовому коду (1 запрос)
     */
    private Map<Integer, RateDictEntity> loadRateDictByNumCode() {
        return rateDictRepository.findAll().stream()
                .collect(Collectors.toMap(RateDictEntity::getNumCode, Function.identity()));
    }

    /**
     * Загрузить все справочники стран в Map по числовому коду (1 запрос)
     */
    private Map<Integer, CountryEntity> loadCountryByNumCode() {
        return countryRepository.findAll().stream()
                .collect(Collectors.toMap(CountryEntity::getNumCode, Function.identity()));
    }

    /**
     * Загрузить существующие currencyId за указанную дату (1 запрос)
     */
    private Set<String> loadExistingCurrencyIds(LocalDateTime rateDate) {
        LocalDateTime start = rateDate.toLocalDate().atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        List<RateEntity> rates = rateRepository.findByCurrencyIdAndPeriod(null, start, end);
        return rates.stream()
                .map(RateEntity::getCurrencyId)
                .collect(Collectors.toSet());
    }

    /**
     * Создать сущность справочника валюты (без сохранения в БД)
     */
    private RateDictEntity createRateDictEntity(CbrCurrencyDto currency) {
        RateDictEntity rateDict = new RateDictEntity();
        rateDict.setName(currency.getName());
        rateDict.setNumCode(currency.getNumCode());
        rateDict.setCharCode(currency.getCharCode());
        return rateDict;
    }

    /**
     * Создать сущность страны (без сохранения в БД)
     */
    private CountryEntity createCountryEntity(CbrCurrencyDto currency) {
        CountryEntity country = new CountryEntity();
        country.setName(currency.getName());
        country.setNumCode(currency.getNumCode());
        country.setCharCode(currency.getCharCode());
        return country;
    }

    /**
     * Создать сущность курса валюты (без сохранения в БД)
     */
    private RateEntity createRateEntity(CbrCurrencyDto currency, RateDictEntity rateDict,
                                        CountryEntity country, LocalDateTime rateDate) {
        RateEntity rate = new RateEntity();
        rate.setCurrencyId(currency.getId());

        // Устанавливаем И ID, И связь
        rate.setCountryId(country.getId());
        rate.setCountry(country);
        rate.setRateDictId(rateDict.getId());
        rate.setRateDict(rateDict);

        rate.setRateDate(rateDate);
        rate.setNominal(currency.getNominal());

        try {
            String valueStr = currency.getValue().replace(",", ".");
            rate.setValue(new BigDecimal(valueStr));
        } catch (Exception e) {
            log.error("Ошибка парсинга значения курса {}: {}", currency.getCharCode(), currency.getValue());
            rate.setValue(BigDecimal.ZERO);
        }

        return rate;
    }

    /**
     *  Чтение журнала курса валют с фильтрацией
     */
    @Transactional(readOnly = true)
    public Page<RateDto> getRates(String currencyId, Long countryId,
                                  LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        log.debug("Запрос журнала курсов: currencyId={}, countryId={}, startDate={}, endDate={}",
                currencyId, countryId, startDate, endDate);

        boolean hasCurrency = currencyId != null && !currencyId.isEmpty();
        boolean hasCountry = countryId != null;
        boolean hasStart = startDate != null;
        boolean hasEnd = endDate != null;

        Page<RateEntity> ratesPage;

        // Если есть и currencyId, и countryId
        if (hasCurrency && hasCountry) {
            if (hasStart && hasEnd) {
                ratesPage = rateRepository.findByCountryIdAndCurrencyIdAndRateDateBetween(
                        countryId, currencyId, startDate, endDate, pageable);
            } else if (hasStart) {
                ratesPage = rateRepository.findByCountryIdAndCurrencyIdAndRateDateAfter(
                        countryId, currencyId, startDate, pageable);
            } else if (hasEnd) {
                ratesPage = rateRepository.findByCountryIdAndCurrencyIdAndRateDateBefore(
                        countryId, currencyId, endDate, pageable);
            } else {
                ratesPage = rateRepository.findByCountryIdAndCurrencyId(countryId, currencyId, pageable);
            }
        }
        // Только countryId
        else if (hasCountry) {
            if (hasStart && hasEnd) {
                ratesPage = rateRepository.findByCountryIdAndRateDateBetween(
                        countryId, startDate, endDate, pageable);
            } else if (hasStart) {
                ratesPage = rateRepository.findByCountryIdAndRateDateAfter(
                        countryId, startDate, pageable);
            } else if (hasEnd) {
                ratesPage = rateRepository.findByCountryIdAndRateDateBefore(
                        countryId, endDate, pageable);
            } else {
                ratesPage = rateRepository.findByCountryId(countryId, pageable);
            }
        }
        // Только currencyId (без countryId)
        else if (hasCurrency) {
            if (hasStart && hasEnd) {
                ratesPage = rateRepository.findByCurrencyIdAndRateDateBetween(
                        currencyId, startDate, endDate, pageable);
            } else if (hasStart) {
                ratesPage = rateRepository.findByCurrencyIdAndRateDateAfter(
                        currencyId, startDate, pageable);
            } else if (hasEnd) {
                ratesPage = rateRepository.findByCurrencyIdAndRateDateBefore(
                        currencyId, endDate, pageable);
            } else {
                ratesPage = rateRepository.findByCurrencyId(currencyId, pageable);
            }
        }
        // Без фильтров по валюте и стране
        else {
            if (hasStart && hasEnd) {
                ratesPage = rateRepository.findByRateDateBetween(startDate, endDate, pageable);
            } else if (hasStart) {
                ratesPage = rateRepository.findByRateDateAfter(startDate, pageable);
            } else if (hasEnd) {
                ratesPage = rateRepository.findByRateDateBefore(endDate, pageable);
            } else {
                ratesPage = rateRepository.findAll(pageable);
            }
        }

        return ratesPage.map(rateMapper::toRateDto);
    }

    /**
     * Чтение справочника валют
     */
    @Transactional(readOnly = true)
    public List<RateDictDto> getRateDict() {
        log.debug("Запрос справочника валют");
        return rateDictRepository.findAllByOrderByNameAsc().stream()
                .map(rateMapper::toRateDictDto)
                .collect(Collectors.toList());
    }

    /**
     * Чтение справочника стран
     */
    @Transactional(readOnly = true)
    public List<CountryDto> getCountries() {
        log.debug("Запрос справочника стран");
        return countryRepository.findAllByOrderByNameAsc().stream()
                .map(rateMapper::toCountryDto)
                .collect(Collectors.toList());
    }

    /**
     * Получить последний курс валюты
     */
    @Transactional(readOnly = true)
    public Optional<RateDto> getLatestRate(String currencyId) {
        log.debug("Запрос последнего курса для валюты: {}", currencyId);
        return rateRepository.findTopByCurrencyIdOrderByRateDateDesc(currencyId)
                .map(rateMapper::toRateDto);
    }

    /**
     * Получить курсы за сегодня
     */
    @Transactional(readOnly = true)
    public Page<RateDto> getTodayRates(Pageable pageable) {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        Page<RateEntity> entityPage = rateRepository.findByRateDateBetween(startOfDay, endOfDay, pageable);

        return entityPage.map(rateMapper::toRateDto);
    }

    @Transactional
    public RateDto updateRate(RateUpdateRequest request) {
        log.info("Запрос на редактирование курса валюты с ID: {}", request.getId());

        // Найти существующую запись по id из DTO
        RateEntity existingRate = rateRepository.findById(request.getId())
                .orElseThrow(() -> {
                    log.error("Запись курса валюты с ID {} не найдена", request.getId());
                    return new RuntimeException("Запись курса валюты с ID " + request.getId() + " не найдена");
                });

        // Обновить номинал
        existingRate.setNominal(request.getNominal());
        // Обновить значение курса
        existingRate.setValue(request.getValue());
        // Установить текущее время обновления
        LocalDateTime now = LocalDateTime.now();
        existingRate.setUpdated(now);

        // Сохранить изменения в БД
        RateEntity updatedRate = rateRepository.save(existingRate);
        log.info("Курс валюты с ID {} успешно обновлен", request.getId());

        return rateMapper.toRateDto(updatedRate);

    }
}