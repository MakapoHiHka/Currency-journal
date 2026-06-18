package com.openSolutions.currencyJournal.mapper;

import com.openSolutions.currencyJournal.domain.dto.request.RateSearchRequest;
import com.openSolutions.currencyJournal.domain.entity.RateEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import static com.openSolutions.currencyJournal.specification.RateSpecification.*;

@Component
public class RateSearchRequestToRateSpecificationConverter implements DtoConverter<RateSearchRequest, Specification<RateEntity>>{

    public Specification<RateEntity> convert(RateSearchRequest request){
        // Собираем спецификацию
        return  Specification
                .where(hasCurrencyId(request.getCurrencyId()))
                .and(hasCountryId(request.getCountryId()))
                .and(rateDateAfter(request.getStartDate()))
                .and(rateDateBefore(request.getEndDate()))
                .and(fetchCountryAndRateDict());
    }
}
