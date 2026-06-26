package com.openSolutions.currencyJournal.service.implementation;

import com.openSolutions.currencyJournal.converter.DtoConverter;
import com.openSolutions.currencyJournal.domain.dto.response.CountryDtoResponse;
import com.openSolutions.currencyJournal.domain.entity.CountryEntity;
import com.openSolutions.currencyJournal.repository.CountryRepository;
import com.openSolutions.currencyJournal.service.CountryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class CountryServiceImpl implements CountryService {

    private final CountryRepository countryRepository;
    private final DtoConverter<CountryEntity, CountryDtoResponse> countryToDtoResponseConverter;
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
