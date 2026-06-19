package com.openSolutions.currencyJournal.service;

import com.openSolutions.currencyJournal.domain.dto.response.CountryDtoResponse;
import com.openSolutions.currencyJournal.domain.dto.response.RateDictDtoResponse;
import com.openSolutions.currencyJournal.mapper.*;
import com.openSolutions.currencyJournal.repository.CountryRepository;
import com.openSolutions.currencyJournal.repository.RateDictRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DictionaryService {

    private final RateDictRepository rateDictRepository;
    private final CountryRepository countryRepository;
    private final CountryToDtoResponseConverter countryToDtoResponseConverter;
    private final RateDictToDtoResponseConverter rateDictToDtoResponseConverter;

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
