package openSolutions.currencyJournal.mapper;

import openSolutions.currencyJournal.dto.RateDto;
import openSolutions.currencyJournal.dto.RateDictDto;
import openSolutions.currencyJournal.dto.CountryDto;
import openSolutions.currencyJournal.entity.RateEntity;
import openSolutions.currencyJournal.entity.RateDictEntity;
import openSolutions.currencyJournal.entity.CountryEntity;
import org.springframework.stereotype.Component;

/**
 * Маппер для конвертации сущностей в DTO
 */
@Component
public class RateMapper {

    /**
     * Конвертировать RateEntity в RateDto
     */
    public RateDto toRateDto(RateEntity entity) {
        if (entity == null) {
            return null;
        }

        RateDto dto = new RateDto();
        dto.setId(entity.getId());
        dto.setCurrencyId(entity.getCurrencyId());
        dto.setCountryId(entity.getCountryId());
        dto.setRateDictId(entity.getRateDictId());
        dto.setNominal(entity.getNominal());
        dto.setValue(entity.getValue());
        dto.setRateDate(entity.getRateDate());
        dto.setCreated(entity.getCreated());
        dto.setUpdated(entity.getUpdated());

        // Добавить названия из связанных сущностей
        if (entity.getCountry() != null) {
            dto.setCountryName(entity.getCountry().getName());
        }

        if (entity.getRateDict() != null) {
            dto.setRateDictName(entity.getRateDict().getName());
            dto.setCharCode(entity.getRateDict().getCharCode());
            dto.setNumCode(entity.getRateDict().getNumCode());
        }

        return dto;
    }

    /**
     * Конвертировать RateDictEntity в RateDictDto
     */
    public RateDictDto toRateDictDto(RateDictEntity entity) {
        if (entity == null) {
            return null;
        }

        RateDictDto dto = new RateDictDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setNumCode(entity.getNumCode());
        dto.setCharCode(entity.getCharCode());

        // Подсчитать количество записей курса
        if (entity.getRates() != null) {
            dto.setRatesCount((long) entity.getRates().size());
        } else {
            dto.setRatesCount(0L);
        }

        return dto;
    }

    /**
     * Конвертировать CountryEntity в CountryDto
     */
    public CountryDto toCountryDto(CountryEntity entity) {
        if (entity == null) {
            return null;
        }

        CountryDto dto = new CountryDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setNumCode(entity.getNumCode());
        dto.setCharCode(entity.getCharCode());

        // Подсчитать количество записей курса
        if (entity.getRates() != null) {
            dto.setRatesCount((long) entity.getRates().size());
        } else {
            dto.setRatesCount(0L);
        }

        return dto;
    }
}