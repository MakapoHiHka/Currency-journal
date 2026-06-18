package com.openSolutions.currencyJournal.parser;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.openSolutions.currencyJournal.domain.dto.cbr.CbrDailyRatesDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Парсер XML ответа от ЦБ РФ
 */
@Component
public class CbrXmlParser {

    private static final Logger log = LoggerFactory.getLogger(CbrXmlParser.class);

    private final XmlMapper xmlMapper;
    private final RestTemplate restTemplate;

    public CbrXmlParser() {
        this.xmlMapper = new XmlMapper();
        this.restTemplate = new RestTemplate();
    }

    /**
     * Загрузить и распарсить XML с URL ЦБ
     */
    public CbrDailyRatesDto parseFromUrl(String urlStr) throws Exception {
        log.info("Загрузка XML курсов валют с URL: {}", urlStr);

        ResponseEntity<String> response = restTemplate.getForEntity(urlStr, String.class);

        //проверить ответ на успешность
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Не удалось получить данные с ЦБ. Status: " + response.getStatusCode());
        }

        String xmlContent = response.getBody();
        log.debug("Получен XML ответ длиной {} символов", xmlContent.length());

        CbrDailyRatesDto result = xmlMapper.readValue(xmlContent, CbrDailyRatesDto.class);

        int currencyCount = result.getCurrencies() != null ? result.getCurrencies().size() : 0;
        log.info("Успешно распарсено {} валют из XML ЦБ (дата: {})", currencyCount, result.getDate());

        return result;
    }
}