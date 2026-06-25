package openSolutions.currencyJournal;

import com.openSolutions.currencyJournal.domain.dto.request.PageQueryRequest;
import com.openSolutions.currencyJournal.domain.dto.request.RateSearchRequest;
import com.openSolutions.currencyJournal.domain.dto.request.RateUpdateRequest;
import com.openSolutions.currencyJournal.domain.dto.response.RateDtoResponse;
import com.openSolutions.currencyJournal.domain.entity.RateEntity;
import com.openSolutions.currencyJournal.converter.DtoConverter;
import com.openSolutions.currencyJournal.converter.PageableConverter;
import com.openSolutions.currencyJournal.repository.RateRepository;
import com.openSolutions.currencyJournal.service.RateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты сервиса курсов валют")
class RateServiceTest {

    @Mock
    private RateRepository rateRepository;

    @Mock
    private DtoConverter<RateEntity, RateDtoResponse> rateToDtoResponseConverter;

    @Mock
    private PageableConverter<PageQueryRequest> pageableMapper;

    @Mock
    private DtoConverter<RateSearchRequest, Specification<RateEntity>> rateSearchRequestToSpecificationConverter;

    @InjectMocks
    private RateService rateService;

    private RateEntity testRateEntity;
    private RateDtoResponse testRateDto;

    @BeforeEach
    void setUp() {
        // Создаём тестовую сущность курса валюты (имитируем запись из БД)
        testRateEntity = new RateEntity();
        testRateEntity.setId(1L);
        testRateEntity.setCurrencyId("R01235"); // USD
        testRateEntity.setNominal(1L);
        testRateEntity.setValue(new BigDecimal("92.50"));
        testRateEntity.setRateDate(LocalDateTime.now());

        // Создаём тестовый DTO
        testRateDto = new RateDtoResponse();
        testRateDto.setId(1L);
        testRateDto.setCurrencyId("R01235");
        testRateDto.setNominal(1L);
        testRateDto.setValue(new BigDecimal("92.50"));
    }

    @Test
    @DisplayName("getRates: должен вернуть страницу с курсами валют")
    void getRates_ShouldReturnPagedResults() {
        // Клиент запрашивает первую страницу с 10 записями
        RateSearchRequest request = new RateSearchRequest();
        request.setPage(0);
        request.setSize(10);

        // сервис преобразует запрос в Pageable
        Pageable pageable = PageRequest.of(0, 10);
        when(pageableMapper.getPageable(any(RateSearchRequest.class)))
                .thenReturn(pageable);

        // Specification для фильтрации (в данном тесте фильтров нет)
        when(rateSearchRequestToSpecificationConverter.convert(any(RateSearchRequest.class)))
                .thenReturn(null);

        // результат из репозитория (одна запись в БД)
        Page<RateEntity> entityPage = new PageImpl<>(List.of(testRateEntity));
        when(rateRepository.findAll((Specification<RateEntity>) any(), any(Pageable.class)))
                .thenReturn(entityPage);

        //конвертация Entity -> DTO
        when(rateToDtoResponseConverter.convert(any(RateEntity.class)))
                .thenReturn(testRateDto);

        // Вызываем тестируемый метод
        Page<RateDtoResponse> result = rateService.getRates(request);

        // Проверяем результат
        assertNotNull(result, "Результат не должен быть null");
        assertEquals(1, result.getTotalElements(), "Должна вернуться 1 запись из БД");
        assertEquals(1, result.getContent().size(), "На странице должна быть 1 запись");

        RateDtoResponse firstRate = result.getContent().get(0);
        assertEquals("R01235", firstRate.getCurrencyId(), "Код валюты должен быть R01235 (USD)");
        assertEquals(1L, firstRate.getNominal(), "Номинал должен быть 1");
        assertEquals(new BigDecimal("92.50"), firstRate.getValue(), "Курс должен быть 92.50");

        // Проверяем, что репозиторий был вызван с правильными параметрами
        verify(rateRepository, times(1)).findAll((Specification<RateEntity>) any(), eq(pageable));
        verify(rateToDtoResponseConverter, times(1)).convert(testRateEntity);
    }

    @Test
    @DisplayName("getLatestRate: должен вернуть последний курс валюты")
    void getLatestRate_ShouldReturnLatestRate() {
        // Подготавливаем данные
        String currencyId = "R01235"; // USD

        // репозиторий возвращает последнюю запись курса
        when(rateRepository.findTopByCurrencyIdOrderByRateDateDesc(currencyId)).thenReturn(Optional.of(testRateEntity));

        // конвертер преобразует Entity в DTO
        when(rateToDtoResponseConverter.convert(testRateEntity)).thenReturn(testRateDto);

        // Запрашиваем последний курс
        Optional<RateDtoResponse> result = rateService.getLatestRate(currencyId);

        // Проверяем результат
        assertTrue(result.isPresent(), "Курс валюты должен быть найден");
        assertEquals("R01235", result.get().getCurrencyId(), "Код валюты должен совпадать с запрошенным");
        assertEquals(new BigDecimal("92.50"), result.get().getValue(), "Курс должен быть 92.50");

        // Проверяем вызовы моков
        verify(rateRepository, times(1)).findTopByCurrencyIdOrderByRateDateDesc(currencyId);
        verify(rateToDtoResponseConverter, times(1)).convert(testRateEntity);
    }

    @Test
    @DisplayName("getLatestRate: должен вернуть пустой Optional, если курс не найден")
    void getLatestRate_ShouldReturnEmptyWhenNotFound() {
        // Подготавливаем данные
        String currencyId = "R99999"; // Несуществующая валюта

        // репозиторий не находит запись
        when(rateRepository.findTopByCurrencyIdOrderByRateDateDesc(currencyId)).thenReturn(Optional.empty());

        // Запрашиваем последний курс
        Optional<RateDtoResponse> result = rateService.getLatestRate(currencyId);

        // Проверяем результат
        assertFalse(result.isPresent(), "Результат должен быть пустым, так как валюта не найдена");
        assertEquals(Optional.empty(), result, "Должен вернуться пустой Optional");

        // Конвертер не должен вызываться
        verify(rateToDtoResponseConverter, never()).convert(any());
    }

    @Test
    @DisplayName("updateRate: должен обновить курс валюты")
    void updateRate_ShouldUpdate() {
        //  Подготавливаем запрос на обновление
        RateUpdateRequest request = new RateUpdateRequest(1L, 2L, new BigDecimal("95.00"));

        // репозиторий находит существующую запись
        when(rateRepository.findById(1L)).thenReturn(Optional.of(testRateEntity));

        // репозиторий сохраняет обновлённую запись
        when(rateRepository.save(any(RateEntity.class)))
                .thenAnswer(invocation -> {
                    RateEntity entity = invocation.getArgument(0);
                    // Симулируем, что БД вернула сохранённую сущность
                    return entity;
                });

        //конвертер преобразует Entity в DTO
        when(rateToDtoResponseConverter.convert(any(RateEntity.class)))
                .thenAnswer(invocation -> {
                    RateEntity entity = invocation.getArgument(0);
                    // Создаём DTO на основе актуального состояния Entity
                    RateDtoResponse dto = new RateDtoResponse();
                    dto.setId(entity.getId());
                    dto.setCurrencyId(entity.getCurrencyId());
                    dto.setNominal(entity.getNominal());
                    dto.setValue(entity.getValue());
                    return dto;
                });

        //Вызываем метод обновления
        RateDtoResponse result = rateService.updateRate(request);

        //Проверяем результат
        assertNotNull(result, "Результат не должен быть null");
        assertEquals(1L, result.getId(), "ID записи должен остаться 1");
        assertEquals(2L, result.getNominal(), "Номинал должен быть обновлён до 2");
        assertEquals(new BigDecimal("95.00"), result.getValue(), "Курс должен быть обновлён до 95.00");

        // Также проверяем, что Entity была мутирована
        assertEquals(2L, testRateEntity.getNominal(), "Entity должна иметь номинал 2");
        assertEquals(new BigDecimal("95.00"), testRateEntity.getValue(), "Entity должна иметь значение 95.00");

        // Проверяем вызовы моков
        verify(rateRepository, times(1)).findById(1L);
        verify(rateRepository, times(1)).save(testRateEntity);
        verify(rateToDtoResponseConverter, times(1)).convert(testRateEntity);
    }

    @Test
    @DisplayName("updateRate: должен выбросить исключение, если запись не найдена")
    void updateRate_ShouldThrowExceptionWhenNotFound() {
        // Подготавливаем запрос для несуществующей записи
        RateUpdateRequest request = new RateUpdateRequest(999L, 2L, new BigDecimal("95.00"));
        // Запрашиваем обновление записи с ID=999 (которой нет в БД)

        // репозиторий не находит запись
        when(rateRepository.findById(999L))
                .thenReturn(Optional.empty());

        //Проверяем, что выбрасывается исключение
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            rateService.updateRate(request);
        }, "Должно быть выброшено RuntimeException при попытке обновить несуществующую запись");

        // Проверяем сообщение об ошибке
        assertTrue(exception.getMessage().contains("не найдена"), "Сообщение об ошибке должно содержать 'не найдена'");
        assertTrue(exception.getMessage().contains("999"), "Сообщение об ошибке должно содержать ID записи (999)");

        // Проверяем, что save() не вызывался
        verify(rateRepository, times(1)).findById(999L);
        verify(rateRepository, never()).save(any());
    }
}