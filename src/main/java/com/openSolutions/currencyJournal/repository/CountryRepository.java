package com.openSolutions.currencyJournal.repository;

import com.openSolutions.currencyJournal.entity.CountryEntity;
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
     * Найти страну по символьному коду
     */
    Optional<CountryEntity> findByCharCode(String charCode);

    /**
     * Найти страну по имени
     */
    Optional<CountryEntity> findByName(String name);

    /**
     * Проверить существование страны по числовому коду
     */
    boolean existsByNumCode(Integer numCode);

    /**
     * Проверить существование страны по символьному коду
     */
    boolean existsByCharCode(String charCode);

    /**
     * Найти все страны с пагинацией и сортировкой
     */
    @Override
    Page<CountryEntity> findAll(Pageable pageable);

    /**
     * Поиск стран по имени с пагинацией
     */
    @Query("SELECT c FROM CountryEntity c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<CountryEntity> findByNameContaining(@Param("name") String name, Pageable pageable);

    /**
     * Найти страны по списку кодов
     */
    @Query("SELECT c FROM CountryEntity c WHERE c.charCode IN :codes")
    List<CountryEntity> findByCharCodeIn(@Param("codes") List<String> codes);


    @EntityGraph(attributePaths = {"rates"})
    List<CountryEntity> findAllByOrderByNameAsc();
}