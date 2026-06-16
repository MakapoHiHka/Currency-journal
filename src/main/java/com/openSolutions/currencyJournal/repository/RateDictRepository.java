package com.openSolutions.currencyJournal.repository;

import com.openSolutions.currencyJournal.entity.RateDictEntity;
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
     * Найти валюту по символьному коду
     */
    Optional<RateDictEntity> findByCharCode(String charCode);


    Optional<RateDictEntity> findByName(String name);

    /**
     * Проверить существование валюты по числовому коду
     */
    boolean existsByNumCode(Integer numCode);

    /**
     * Проверить существование валюты по символьному коду
     */
    boolean existsByCharCode(String charCode);

    /**
     * Найти все валюты с сортировкой по имени
     */
    @EntityGraph(attributePaths = {"rates"})
    List<RateDictEntity> findAllByOrderByNameAsc();

    @Query("SELECT r FROM RateDictEntity r WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY r.name")
    List<RateDictEntity> findByNameContaining(@Param("name") String name);

    @Query("SELECT r FROM RateDictEntity r WHERE r.charCode IN :codes")
    List<RateDictEntity> findByCharCodeIn(@Param("codes") List<String> codes);

    @Query("SELECT r FROM RateDictEntity r WHERE r.numCode IN :codes")
    List<RateDictEntity> findByNumCodeIn(@Param("codes") List<Integer> codes);
}