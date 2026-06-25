package com.openSolutions.currencyJournal.service.implementation;

import com.openSolutions.currencyJournal.domain.dto.request.PageQueryRequest;
import com.openSolutions.currencyJournal.domain.dto.request.RateSearchRequest;
import com.openSolutions.currencyJournal.domain.dto.request.RateUpdateRequest;
import com.openSolutions.currencyJournal.domain.dto.response.RateDtoResponse;
import com.openSolutions.currencyJournal.domain.entity.RateEntity;
import com.openSolutions.currencyJournal.converter.*;
import com.openSolutions.currencyJournal.repository.RateRepository;
import com.openSolutions.currencyJournal.service.RateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RateServiceImpl implements RateService {

    private final RateRepository rateRepository;
    private final DtoConverter<RateEntity, RateDtoResponse> rateToDtoResponseConverter;
    private final PageableConverter<PageQueryRequest> pageableMapper;
    private final DtoConverter<RateSearchRequest, Specification<RateEntity>> rateSearchRequestToSpecificationConverter;


    /**
     *  Чтение журнала курса валют с фильтрацией
     */
    @Transactional(readOnly = true)
    public Page<RateDtoResponse> getRates(RateSearchRequest request) {
        log.debug("Запрос журнала курсов: currencyId={}, countryId={}, startDate={}, endDate={}",
                request.getCurrencyId(), request.getCountryId(),
                request.getStartDate(), request.getEndDate());

        Specification<RateEntity> spec = rateSearchRequestToSpecificationConverter.convert(request);

        Pageable pageable = pageableMapper.getPageable(request);
        Page<RateEntity> ratesPage = rateRepository.findAll(spec, pageable);

        return ratesPage.map(rateToDtoResponseConverter::convert);
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
