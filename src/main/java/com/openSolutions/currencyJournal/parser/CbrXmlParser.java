package com.openSolutions.currencyJournal.parser;

import com.openSolutions.currencyJournal.domain.dto.cbr.CbrCurrencyDto;
import com.openSolutions.currencyJournal.domain.dto.cbr.CbrDailyRatesDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Парсер XML ответа от ЦБ РФ
 */
@Component
public class CbrXmlParser {

    private static final Logger log = LoggerFactory.getLogger(CbrXmlParser.class);

    // Формат даты из XML: "2023-08-12T12:00:00+03:00"
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    /**
     * Распарсить XML с курсами валют
     *
     * @param xmlStream InputStream с XML данными
     * @return CbrDailyRatesDto с распарсенными данными
     * @throws Exception если произошла ошибка парсинга
     */
    public CbrDailyRatesDto parse(InputStream xmlStream) throws Exception {
        log.debug("Начало парсинга XML курсов валют ЦБ");

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xmlStream);

        document.getDocumentElement().normalize();

        CbrDailyRatesDto result = new CbrDailyRatesDto();

        // Распарсить дату обновления
        Element root = document.getDocumentElement();
        String updatedAttr = root.getAttribute("updated");
        if (updatedAttr != null && !updatedAttr.isEmpty()) {
            try {
                result.setUpdated(LocalDateTime.parse(updatedAttr, DATE_TIME_FORMATTER));
            } catch (Exception e) {
                log.warn("Не удалось распарсить дату updated: {}, используется текущее время", updatedAttr);
                result.setUpdated(LocalDateTime.now());
            }
        } else {
            result.setUpdated(LocalDateTime.now());
        }

        // Распарсить список валют
        NodeList currencyNodes = root.getElementsByTagName("Valute");
        List<CbrCurrencyDto> currencies = new ArrayList<>();

        for (int i = 0; i < currencyNodes.getLength(); i++) {
            Node node = currencyNodes.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                CbrCurrencyDto currency = parseCurrency(element);
                currencies.add(currency);
            }
        }

        result.setCurrencies(currencies);

        log.info("Успешно распарсено {} валют из XML ЦБ", currencies.size());
        return result;
    }

    /**
     * Распарсить одну валюту из XML
     */
    private CbrCurrencyDto parseCurrency(Element element) {
        CbrCurrencyDto currency = new CbrCurrencyDto();

        // ID - атрибут элемента
        currency.setId(element.getAttribute("ID"));

        // NumCode
        String numCode = getElementValue(element, "NumCode");
        if (numCode != null && !numCode.isEmpty()) {
            try {
                currency.setNumCode(Integer.parseInt(numCode));
            } catch (NumberFormatException e) {
                log.warn("Неверный формат NumCode: {}", numCode);
            }
        }

        // CharCode
        currency.setCharCode(getElementValue(element, "CharCode"));

        // Nominal
        String nominalStr = getElementValue(element, "Nominal");
        if (nominalStr != null && !nominalStr.isEmpty()) {
            try {
                // Заменить запятую на точку и пробелы
                nominalStr = nominalStr.replace(",", "").replace(" ", "");
                currency.setNominal(Long.parseLong(nominalStr));
            } catch (NumberFormatException e) {
                log.warn("Неверный формат Nominal: {}", nominalStr);
                currency.setNominal(1L);
            }
        } else {
            currency.setNominal(1L);
        }

        // Name
        currency.setName(getElementValue(element, "Name"));

        // Value (курс)
        currency.setValue(getElementValue(element, "Value"));

        return currency;
    }

    /**
     * Получить значение элемента по тегу
     */
    private String getElementValue(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return null;
    }

    /**
     * Загрузить и распарсить XML с URL ЦБ
     */
    public CbrDailyRatesDto parseFromUrl(String urlStr) throws Exception {
        log.info("Загрузка XML курсов валют с URL: {}", urlStr);

        URL url = new URL(urlStr);
        try (InputStream inputStream = url.openStream()) {
            return parse(inputStream);
        }
    }
}