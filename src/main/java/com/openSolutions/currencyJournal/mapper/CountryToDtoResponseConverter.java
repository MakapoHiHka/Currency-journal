package com.openSolutions.currencyJournal.mapper;

import com.openSolutions.currencyJournal.domain.dto.response.CountryDtoResponse;
import com.openSolutions.currencyJournal.domain.entity.CountryEntity;
import org.springframework.stereotype.Component;

@Component
public class CountryToDtoResponseConverter implements DtoConverter<CountryEntity, CountryDtoResponse> {
    public CountryDtoResponse convert(CountryEntity countryEntity){
        CountryDtoResponse response = new CountryDtoResponse();
        response.setId(countryEntity.getId());
        response.setName(countryEntity.getName());
        response.setNumCode(countryEntity.getNumCode());
        response.setCharCode(countryEntity.getCharCode());
        return response;
    }
}
