package com.openSolutions.currencyJournal.mapper;

import com.openSolutions.currencyJournal.domain.dto.request.RateSearchRequest;
import com.openSolutions.currencyJournal.domain.entity.RateEntity;
import com.openSolutions.currencyJournal.utils.SpecificationUtil;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class RateSearchRequestToRateSpecificationConverter implements DtoConverter<RateSearchRequest, Specification<RateEntity>>{

    public Specification<RateEntity> convert(RateSearchRequest request){
        // Собираем спецификацию
        Specification<RateEntity> spec = Specification.where(
                SpecificationUtil.equal("currencyId", request.getCurrencyId())
        );

        // Добавляем остальные условия
        spec = spec.and(SpecificationUtil.equalRelation("country", "id", request.getCountryId()));
        spec = spec.and(SpecificationUtil.greaterThanOrEqual("rateDate", request.getStartDate()));
        spec = spec.and(SpecificationUtil.lessThanOrEqual("rateDate", request.getEndDate()));
        spec = spec.and(SpecificationUtil.fetchJoin("country", "rateDict"));

        return spec;
    }
}
