package openSolutions.currencyJournal.repository;

import openSolutions.currencyJournal.entity.RateEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с журналом курса валют (rates)
 */
@Repository
public interface RateRepository extends JpaRepository<RateEntity, Long> {

    /**
     * Найти курс валюты по идентификатору валюты из ЦБ и дате
     */
    Optional<RateEntity> findByCurrencyIdAndRateDate(String currencyId, LocalDateTime rateDate);

    /**
     * Найти все курсы валюты за период
     */
    @Query("SELECT r FROM RateEntity r WHERE r.currencyId = :currencyId " +
            "AND r.rateDate BETWEEN :startDate AND :endDate " +
            "ORDER BY r.rateDate DESC")
    List<RateEntity> findByCurrencyIdAndPeriod(
            @Param("currencyId") String currencyId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Найти курсы валют с пагинацией и сортировкой
     */
    @Override
    Page<RateEntity> findAll(Pageable pageable);

    /**
     * Поиск курсов валют с фильтрацией по дате
     */
    @Query("SELECT r FROM RateEntity r WHERE r.rateDate >= :startDate " +
            "ORDER BY r.rateDate DESC")
    Page<RateEntity> findByRateDateAfter(
            @Param("startDate") LocalDateTime startDate,
            Pageable pageable);

    /**
     * Поиск курсов валют с фильтрацией по валюте и периоду
     */
    @Query("SELECT r FROM RateEntity r WHERE r.currencyId = :currencyId " +
            "AND r.rateDate BETWEEN :startDate AND :endDate " +
            "ORDER BY r.rateDate DESC")
    Page<RateEntity> findByCurrencyIdAndRateDateBetween(
            @Param("currencyId") String currencyId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    /**
     * Поиск курсов валют с динамической фильтрацией по нескольким параметрам
     */
    @Query("SELECT r FROM RateEntity r WHERE " +
            "(:currencyId IS NULL OR r.currencyId = :currencyId) AND " +
            "(:countryId IS NULL OR r.countryId = :countryId) AND " +
            "(:rateDictId IS NULL OR r.rateDictId = :rateDictId) AND " +
            "(:startDate IS NULL OR r.rateDate >= :startDate) AND " +
            "(:endDate IS NULL OR r.rateDate <= :endDate) " +
            "ORDER BY r.rateDate DESC")
    Page<RateEntity> findByFilters(
            @Param("currencyId") String currencyId,
            @Param("countryId") Long countryId,
            @Param("rateDictId") Long rateDictId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    /**
     * Найти последний курс валюты по currencyId
     */
    @Query("SELECT r FROM RateEntity r WHERE r.currencyId = :currencyId " +
            "ORDER BY r.rateDate DESC")
    Optional<RateEntity> findTopByCurrencyIdOrderByRateDateDesc(@Param("currencyId") String currencyId);

    /**
     * Найти курсы валют по списку currencyId
     */
    @Query("SELECT r FROM RateEntity r WHERE r.currencyId IN :currencyIds " +
            "ORDER BY r.rateDate DESC")
    List<RateEntity> findByCurrencyIdIn(@Param("currencyIds") List<String> currencyIds);

    /**
     * Проверить существование курса за дату
     */
    boolean existsByCurrencyIdAndRateDate(String currencyId, LocalDateTime rateDate);

    /**
     * Удалить курсы валют за период
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM RateEntity r WHERE r.rateDate BETWEEN :startDate AND :endDate")
    int deleteByRateDateBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Удалить курсы конкретной валюты за период
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM RateEntity r WHERE r.currencyId = :currencyId " +
            "AND r.rateDate BETWEEN :startDate AND :endDate")
    int deleteByCurrencyIdAndRateDateBetween(
            @Param("currencyId") String currencyId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Найти курсы валют для конкретной страны
     */
    @Query("SELECT r FROM RateEntity r WHERE r.countryId = :countryId " +
            "AND r.rateDate BETWEEN :startDate AND :endDate " +
            "ORDER BY r.rateDate DESC")
    List<RateEntity> findByCountryIdAndPeriod(
            @Param("countryId") Long countryId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Получить уникальный список currencyId из журнала
     */
    @Query("SELECT DISTINCT r.currencyId FROM RateEntity r ORDER BY r.currencyId")
    List<String> findDistinctCurrencyIds();

    /**
     * Найти курсы валют по номиналу
     */
    @Query("SELECT r FROM RateEntity r WHERE r.nominal = :nominal " +
            "AND r.rateDate BETWEEN :startDate AND :endDate")
    List<RateEntity> findByNominalAndPeriod(
            @Param("nominal") Long nominal,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Найти все курсы валют с сортировкой по дате (новые сначала)
     */
    List<RateEntity> findAllByOrderByRateDateDesc();
}