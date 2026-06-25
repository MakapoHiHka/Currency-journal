package com.openSolutions.currencyJournal.converter.dto;

import com.openSolutions.currencyJournal.domain.dto.response.RateDictDtoResponse;
import com.openSolutions.currencyJournal.domain.entity.RateDictEntity;
import com.openSolutions.currencyJournal.converter.DtoConverter;
import org.springframework.stereotype.Component;

@Component
public class RateDictToDtoResponseConverter implements DtoConverter<RateDictEntity, RateDictDtoResponse> {

    /**
     * Конвертировать RateDictEntity в RateDictDto
     */
    public RateDictDtoResponse convert(RateDictEntity entity) {
        if (entity == null) {
            return null;
        }

        RateDictDtoResponse dto = new RateDictDtoResponse();
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
