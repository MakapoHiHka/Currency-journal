package com.openSolutions.currencyJournal.service.implementation;

import com.openSolutions.currencyJournal.converter.DtoConverter;
import com.openSolutions.currencyJournal.domain.dto.response.RateDictDtoResponse;
import com.openSolutions.currencyJournal.domain.entity.RateDictEntity;
import com.openSolutions.currencyJournal.repository.RateDictRepository;
import com.openSolutions.currencyJournal.service.RateDictService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class RateDictServiceImpl implements RateDictService {
    private final RateDictRepository rateDictRepository;
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
}
