package openSolutions.currencyJournal.repository;

import openSolutions.currencyJournal.entity.RateDictEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы со справочником валют (rate_dict)
 */
@Repository
public interface RateDictRepository extends JpaRepository<RateDictEntity, Long> {

    /**
     * Найти валюту по числовому коду (например, 840 для USD)
     */
    Optional<RateDictEntity> findByNumCode(Integer numCode);

    /**
     * Найти валюту по символьному коду (например, USD)
     */
    Optional<RateDictEntity> findByCharCode(String charCode);

    /**
     * Проверить существование валюты по числовому коду
     */
    boolean existsByNumCode(Integer numCode);

    /**
     * Проверить существование валюты по символьному коду
     */
    boolean existsByCharCode(String charCode);

//    /**
//     * Найти валюту по числовому или символьному коду
//     */
//    @Query("SELECT r FROM RateDictEntity r WHERE r.numCode = :code OR r.charCode = :code")
//    Optional<RateDictEntity> findByNumCodeOrCharCode(@Param("code") String code);

    /**
     * Найти все валюты с сортировкой по имени
     */
    List<RateDictEntity> findAllByOrderByNameAsc();
}