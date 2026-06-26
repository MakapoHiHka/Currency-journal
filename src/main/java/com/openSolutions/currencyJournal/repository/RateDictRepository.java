package com.openSolutions.currencyJournal.repository;

import com.openSolutions.currencyJournal.domain.entity.RateDictEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы со справочником валют
 */
@Repository
public interface RateDictRepository extends JpaRepository<RateDictEntity, Long> {

    /**
     * Найти валюту по числовому коду
     */
    Optional<RateDictEntity> findByNumCode(Integer numCode);

    /**
     * Найти все валюты с сортировкой по имени
     */
    @EntityGraph(attributePaths = {"rates"})
    List<RateDictEntity> findAllByOrderByNameAsc();
}