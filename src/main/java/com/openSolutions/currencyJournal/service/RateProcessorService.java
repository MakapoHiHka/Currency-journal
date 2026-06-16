package com.openSolutions.currencyJournal.service;

import com.openSolutions.currencyJournal.dto.CbrCurrencyDto;
import com.openSolutions.currencyJournal.entity.CountryEntity;
import com.openSolutions.currencyJournal.entity.RateDictEntity;
import com.openSolutions.currencyJournal.entity.RateEntity;
import com.openSolutions.currencyJournal.repository.CountryRepository;
import com.openSolutions.currencyJournal.repository.RateDictRepository;
import com.openSolutions.currencyJournal.repository.RateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class RateProcessorService {

    private static final Logger log = LoggerFactory.getLogger(RateProcessorService.class);

    private final RateDictRepository rateDictRepository;
    private final RateRepository rateRepository;
    private final CountryRepository countryRepository;

    public RateProcessorService(RateDictRepository rateDictRepository,
                                RateRepository rateRepository,
                                CountryRepository countryRepository) {
        this.rateDictRepository = rateDictRepository;
        this.rateRepository = rateRepository;
        this.countryRepository = countryRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void processCurrency(CbrCurrencyDto currency, LocalDateTime rateDate) {
        log.debug("Обработка валюты: {} ({})", currency.getCharCode(), currency.getName());

        // Найти или создать запись в справочнике валют
        RateDictEntity rateDict = findOrCreateRateDict(currency);

        // Найти или создать страну по числовому коду валюты
        CountryEntity country = findOrCreateCountry(currency);

        // Создать запись в журнале курсов
        createRateRecord(currency, rateDict, country, rateDate);

        log.debug("Валюта {} успешно обработана", currency.getCharCode());
    }

    private RateDictEntity findOrCreateRateDict(CbrCurrencyDto currency) {
        Optional<RateDictEntity> existing = rateDictRepository.findByNumCode(currency.getNumCode());

        if (existing.isPresent()) {
            return existing.get();
        }

        RateDictEntity rateDict = new RateDictEntity();
        rateDict.setName(currency.getName());
        rateDict.setNumCode(currency.getNumCode());
        rateDict.setCharCode(currency.getCharCode());

        return rateDictRepository.save(rateDict);
    }

    /**
     * Найти или создать страну по числовому коду валюты
     * NumCode валюты совпадает с NumCode страны (ISO 3166-1)
     */
    private CountryEntity findOrCreateCountry(CbrCurrencyDto currency) {
        if (currency.getNumCode() == null) {
            log.warn("У валюты {} нет числового кода, страна не будет определена", currency.getCharCode());
            return null;
        }

        // Ищем страну по числовому коду
        Optional<CountryEntity> existing = countryRepository.findByNumCode(currency.getNumCode());

        if (existing.isPresent()) {
            log.trace("Страна найдена для валюты {}: {}", currency.getCharCode(), existing.get().getName());
            return existing.get();
        }

        // Если не найдена - создаём новую с данными валюты
        CountryEntity country = new CountryEntity();
        country.setName("Страна валюты " + currency.getCharCode());
        country.setNumCode(currency.getNumCode());
        country.setCharCode(currency.getCharCode());

        CountryEntity saved = countryRepository.save(country);
        log.info("Создана новая страна для валюты {}: {}", currency.getCharCode(), saved.getName());

        return saved;
    }

    private void createRateRecord(CbrCurrencyDto currency, RateDictEntity rateDict,
                                  CountryEntity country, LocalDateTime rateDate) {
        // Проверить, есть ли уже запись
        if (rateRepository.existsByCurrencyIdAndRateDate(currency.getId(), rateDate)) {
            log.debug("Запись уже существует для {} на {}", currency.getCharCode(), rateDate);
            return;
        }

        RateEntity rate = new RateEntity();
        rate.setCurrencyId(currency.getId());
        rate.setRateDictId(rateDict.getId());

        // Установить ID страны
        if (country != null) {
            rate.setCountryId(country.getId());
            rate.setCountry(country);
        } else {
            log.error("Не удалось определить страну для валюты {}, запись не будет создана",
                    currency.getCharCode());
            throw new IllegalStateException("Страна не определена для валюты " + currency.getCharCode());
        }

        rate.setRateDate(rateDate);
        rate.setNominal(currency.getNominal() != null ? currency.getNominal() : 1L);

        // Распарсить значение курса
        try {
            String valueStr = currency.getValue().replace(",", ".");
            rate.setValue(new BigDecimal(valueStr));
        } catch (Exception e) {
            log.error("Ошибка парсинга значения курса {}: {}", currency.getValue(), e.getMessage());
            rate.setValue(BigDecimal.ZERO);
        }

        rateRepository.save(rate);
    }


}