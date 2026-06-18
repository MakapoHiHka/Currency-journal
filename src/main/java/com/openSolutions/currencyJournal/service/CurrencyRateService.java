package com.openSolutions.currencyJournal.service;

import com.openSolutions.currencyJournal.domain.dto.cbr.CbrCurrencyDto;
import com.openSolutions.currencyJournal.domain.dto.cbr.CbrDailyRatesDto;
import com.openSolutions.currencyJournal.domain.dto.request.RateSearchRequest;
import com.openSolutions.currencyJournal.domain.dto.request.RateUpdateRequest;
import com.openSolutions.currencyJournal.domain.dto.response.CountryDtoResponse;
import com.openSolutions.currencyJournal.domain.dto.response.RateDictDtoResponse;
import com.openSolutions.currencyJournal.domain.dto.response.RateDtoResponse;
import com.openSolutions.currencyJournal.domain.entity.CountryEntity;
import com.openSolutions.currencyJournal.domain.entity.RateDictEntity;
import com.openSolutions.currencyJournal.domain.entity.RateEntity;
import com.openSolutions.currencyJournal.mapper.CountryToDtoResponseConverter;
import com.openSolutions.currencyJournal.mapper.PageableMapper;
import com.openSolutions.currencyJournal.mapper.RateDictToDtoResponseConverter;
import com.openSolutions.currencyJournal.repository.CountryRepository;
import com.openSolutions.currencyJournal.repository.RateDictRepository;
import com.openSolutions.currencyJournal.repository.RateRepository;
import lombok.RequiredArgsConstructor;
import com.openSolutions.currencyJournal.mapper.RateToDtoResponseConverter;
import com.openSolutions.currencyJournal.parser.CbrXmlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.openSolutions.currencyJournal.specification.RateSpecification.*;

@Service
@RequiredArgsConstructor
public class CurrencyRateService {

    private static final Logger log = LoggerFactory.getLogger(CurrencyRateService.class);

    private final CbrXmlParser xmlParser;
    private final RateDictRepository rateDictRepository;
    private final CountryRepository countryRepository;
    private final RateRepository rateRepository;
    private final RateToDtoResponseConverter rateToDtoResponseConverter;
    private final PageableMapper pageableMapper;
    private final CountryToDtoResponseConverter countryToDtoResponseConverter;
    private final RateDictToDtoResponseConverter rateDictToDtoResponseConverter;

    @Value("${cbr.api.url:https://www.cbr-xml-daily.ru/daily_utf8.xml}")
    private String cbrApiUrl;

    /**
     * Ручная синхронизация курсов валют с ЦБ
     */
    @Transactional
    public int synchronizeWithCbr() {
        log.info("Запуск синхронизации курсов валют с ЦБ");
        long startTime = System.currentTimeMillis();

        try {
            // 1. Загружаем XML
            CbrDailyRatesDto ratesDto = xmlParser.parseFromUrl(cbrApiUrl);
            LocalDateTime rateDate = ratesDto.getUpdated();
            List<CbrCurrencyDto> currencies = ratesDto.getCurrencies();

            if (currencies == null || currencies.isEmpty()) {
                log.warn("Список валют пуст");
                return 0;
            }

            log.info("Получено {} валют для обработки (дата курса: {})", currencies.size(), rateDate);

            // 2. Загружаем справочники
            Map<Integer, RateDictEntity> rateDictByNumCode = loadRateDictByNumCode();
            Map<Integer, CountryEntity> countryByNumCode = loadCountryByNumCode();

            // 3. Загружаем существующие курсы за дату обновления
            Set<String> existingCurrencyIds = loadExistingCurrencyIds(rateDate);

            // 4. Создаём записи курсов
            List<RateEntity> newRates = new ArrayList<>();
            int processedCount = 0;
            int errorCount = 0;

            for (CbrCurrencyDto currency : currencies) {
                try {
                    // Найти справочники по числовому коду
                    RateDictEntity rateDict = rateDictByNumCode.get(currency.getNumCode());
                    CountryEntity country = countryByNumCode.get(currency.getNumCode());

                    if (rateDict == null) {
                        log.warn("Не найден справочник валюты для кода: {} ({})",
                                currency.getNumCode(), currency.getCharCode());
                        errorCount++;
                        continue;
                    }

                    if (country == null) {
                        log.warn("Не найден справочник страны для кода: {} ({})",
                                currency.getNumCode(), currency.getCharCode());
                        errorCount++;
                        continue;
                    }

                    // Создать запись курса
                    RateEntity rate = createRateEntity(currency, rateDict, country, rateDate);
                    newRates.add(rate);
                    existingCurrencyIds.add(currency.getId());
                    processedCount++;

                } catch (Exception e) {
                    errorCount++;
                    log.error("Ошибка при обработке валюты {}: {}",
                            currency.getCharCode(), e.getMessage());
                }
            }

            // 5. Сохраняем курсы
            if (!newRates.isEmpty()) {
                rateRepository.saveAll(newRates);
                log.info("Создано новых записей в журнале курсов: {}", newRates.size());
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

    /**
     * Загрузить все справочники валют в Map по числовому коду
     */
    private Map<Integer, RateDictEntity> loadRateDictByNumCode() {
        return rateDictRepository.findAll().stream()
                .collect(Collectors.toMap(RateDictEntity::getNumCode, Function.identity()));
    }

    /**
     * Загрузить все справочники стран в Map по числовому коду
     */
    private Map<Integer, CountryEntity> loadCountryByNumCode() {
        return countryRepository.findAll().stream()
                .collect(Collectors.toMap(CountryEntity::getNumCode, Function.identity()));
    }

    /**
     * Загрузить существующие currencyId за указанную дату
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
    public Page<RateDtoResponse> getRates(RateSearchRequest request) {
        log.debug("Запрос журнала курсов: currencyId={}, countryId={}, startDate={}, endDate={}",
                request.getCurrencyId(), request.getCountryId(),
                request.getStartDate(), request.getEndDate());

        // Собираем спецификацию
        Specification<RateEntity> spec = Specification
                .where(hasCurrencyId(request.getCurrencyId()))
                .and(hasCountryId(request.getCountryId()))
                .and(rateDateAfter(request.getStartDate()))
                .and(rateDateBefore(request.getEndDate()));

        Pageable pageable = pageableMapper.getPageable(request);
        Page<RateEntity> ratesPage = rateRepository.findAll(spec, pageable);

        return ratesPage.map(rateToDtoResponseConverter::convert);
    }

    /**
     * Чтение справочника валют
     */
    @Transactional(readOnly = true)
    public List<RateDictDtoResponse> getRateDict() {
        log.debug("Запрос справочника валют");
        return rateDictRepository.findAllByOrderByNameAsc().stream()
                .map(rateDictToDtoResponseConverter::convert)
                .collect(Collectors.toList());
    }

    /**
     * Чтение справочника стран
     */
    @Transactional(readOnly = true)
    public List<CountryDtoResponse> getCountries() {
        log.debug("Запрос справочника стран");
        return countryRepository.findAllByOrderByNameAsc().stream()
                .map(countryToDtoResponseConverter::convert)
                .collect(Collectors.toList());
    }

    /**
     * Получить последний курс валюты
     */
    @Transactional(readOnly = true)
    public Optional<RateDtoResponse> getLatestRate(String currencyId) {
        log.debug("Запрос последнего курса для валюты: {}", currencyId);
        return rateRepository.findTopByCurrencyIdOrderByRateDateDesc(currencyId)
                .map(rateToDtoResponseConverter::convert);
    }


    @Transactional
    public RateDtoResponse updateRate(RateUpdateRequest request) {
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

        return rateToDtoResponseConverter.convert(updatedRate);

    }
}