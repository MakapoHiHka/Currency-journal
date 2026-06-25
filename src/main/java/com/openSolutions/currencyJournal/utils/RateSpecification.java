package com.openSolutions.currencyJournal.utils;

import com.openSolutions.currencyJournal.domain.entity.RateEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Спецификации для фильтрации журнала курса валют.
 */
@Component
public class RateSpecification {

    /**
     * Фильтр по ID валюты.
     */
    public static Specification<RateEntity> hasCurrencyId(String currencyId) {
        return (root, query, cb) -> {
            if (currencyId == null || currencyId.isEmpty()) {
                return null; // условие игнорируется
            }
            return cb.equal(root.get("currencyId"), currencyId);
        };
    }

    /**
     * Фильтр по ID страны.
     */
    public static Specification<RateEntity> hasCountryId(Long countryId) {
        return (root, query, cb) -> {
            if (countryId == null) {
                return null;
            }
            return cb.equal(root.get("country").get("id"), countryId);
        };
    }

    /**
     * Фильтр по дате начала (>= startDate).
     */
    public static Specification<RateEntity> rateDateAfter(LocalDate startDate) {
        return (root, query, cb) -> {
            if (startDate == null) {
                return null;
            }
            return cb.greaterThanOrEqualTo(root.get("rateDate"), startDate);
        };
    }

    /**
     * Фильтр по дате окончания (<= endDate).
     */
    public static Specification<RateEntity> rateDateBefore(LocalDate endDate) {
        return (root, query, cb) -> {
            if (endDate == null) {
                return null;
            }
            return cb.lessThanOrEqualTo(root.get("rateDate"), endDate);
        };
    }

    /**
     * Fetch join для загрузки связанных сущностей одним запросом
     */
    public static Specification<RateEntity> fetchCountryAndRateDict() {
        return (root, query, cb) -> {
            // Проверяем, что это не count query
            if (query != null && Long.class != query.getResultType()) {
                root.fetch("country", jakarta.persistence.criteria.JoinType.LEFT);
                root.fetch("rateDict", jakarta.persistence.criteria.JoinType.LEFT);
            }
            return null;
        };
    }
}