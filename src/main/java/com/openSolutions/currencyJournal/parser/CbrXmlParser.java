package com.openSolutions.currencyJournal.parser;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.openSolutions.currencyJournal.domain.pojo.CbrDailyRatesDto;
import com.openSolutions.currencyJournal.exceptions.BadCbrResponseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


/**
 * Парсер XML ответа от ЦБ РФ
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CbrXmlParser {

    private final RestTemplate restTemplate;

    /**
     * Загрузить и распарсить XML с URL ЦБ
     */
    public CbrDailyRatesDto parseFromUrl(String urlStr) {
        ResponseEntity<CbrDailyRatesDto> response = restTemplate.getForEntity(urlStr, CbrDailyRatesDto.class);
        //проверить ответ на успешность
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new BadCbrResponseException("Не удалось получить данные с ЦБ. Status: " + response.getStatusCode());
        }
        return response.getBody();
    }
}