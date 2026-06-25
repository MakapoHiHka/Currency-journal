package com.openSolutions.currencyJournal.utils;

import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

/**
 * Спецификации для фильтрации журнала курса валют.
 */
@Component
public class SpecificationUtil {

    /**
     * Фильтр по ID валюты.
     */
    public static <T> Specification<T> equal(String fieldName, Object value) {
        return (root, query, cb) -> {
            if (value == null || (value instanceof String && ((String) value).isEmpty())) {
                return null;
            }
            return cb.equal(root.get(fieldName), value);
        };
    }
    /**
     * Фильтр по полю связанной сущности.
     */
    public static <T> Specification<T> equalRelation(String relationField, String nestedField, Object value) {
        return (root, query, cb) -> {
            if (value == null) {
                return null;
            }
            return cb.equal(root.get(relationField).get(nestedField), value);
        };
    }

    /**
     * Фильтр по дате начала (>= startDate).
     */
    public static <T, Y extends Comparable<? super Y>> Specification<T> greaterThanOrEqual(String fieldName, Y value) {
        return (root, query, cb) -> {
            if (value == null) {
                return null;
            }
            return cb.greaterThanOrEqualTo(root.get(fieldName), value);
        };
    }

    /**
     * Фильтр по дате окончания (<= endDate).
     */
    public static <T, Y extends Comparable<? super Y>> Specification<T> lessThanOrEqual(String fieldName, Y value) {
        return (root, query, cb) -> {
            if (value == null) {
                return null;
            }
            return cb.lessThanOrEqualTo(root.get(fieldName), value);
        };
    }

    /**
     * Fetch join для загрузки связанных сущностей одним запросом
     */
    public static <T> Specification<T> fetchJoin(String... paths) {
        return (root, query, cb) -> {
            if (query != null && query.getResultType() != Long.class && query.getResultType() != long.class) {
                for (String path : paths) {
                    root.fetch(path, JoinType.LEFT);
                }
            }
            return null;
        };
    }
}