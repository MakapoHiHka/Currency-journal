package com.openSolutions.currencyJournal.mapper;

import com.openSolutions.currencyJournal.domain.dto.response.RateDtoResponse;
import com.openSolutions.currencyJournal.domain.dto.response.RateDictDtoResponse;
import com.openSolutions.currencyJournal.domain.entity.RateEntity;
import com.openSolutions.currencyJournal.domain.entity.RateDictEntity;
import org.springframework.stereotype.Component;

/**
 * Маппер для конвертации сущностей в DTO
 */
@Component
public class RateToDtoResponseConverter implements DtoConverter<RateEntity, RateDtoResponse> {

    /**
     * Конвертировать RateEntity в RateDto
     */
    public RateDtoResponse convert(RateEntity entity) {
        if (entity == null) {
            return null;
        }

        RateDtoResponse dto = new RateDtoResponse();
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
}