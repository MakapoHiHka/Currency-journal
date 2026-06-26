package com.openSolutions.currencyJournal.repository;

import com.openSolutions.currencyJournal.domain.entity.CountryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы со справочником стран
 */
@Repository
public interface CountryRepository extends JpaRepository<CountryEntity, Long> {

    /**
     * Найти страну по числовому коду
     */
    Optional<CountryEntity> findByNumCode(Integer numCode);

    /**
     * Найти все страны с пагинацией и сортировкой
     */
    @Override
    Page<CountryEntity> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"rates"})
    List<CountryEntity> findAllByOrderByNameAsc();
}