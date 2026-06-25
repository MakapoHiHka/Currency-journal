package com.openSolutions.currencyJournal.service.implementation;

import com.openSolutions.currencyJournal.domain.dto.response.CountryDtoResponse;
import com.openSolutions.currencyJournal.domain.dto.response.RateDictDtoResponse;
import com.openSolutions.currencyJournal.domain.entity.CountryEntity;
import com.openSolutions.currencyJournal.domain.entity.RateDictEntity;
import com.openSolutions.currencyJournal.converter.*;
import com.openSolutions.currencyJournal.repository.CountryRepository;
import com.openSolutions.currencyJournal.repository.RateDictRepository;
import com.openSolutions.currencyJournal.service.DictionaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DictionaryServiceImpl implements DictionaryService {

    private final RateDictRepository rateDictRepository;
    private final CountryRepository countryRepository;
    private final DtoConverter<CountryEntity, CountryDtoResponse> countryToDtoResponseConverter;
    private final DtoConverter<RateDictEntity, RateDictDtoResponse> rateDictToDtoResponseConverter;

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
}
