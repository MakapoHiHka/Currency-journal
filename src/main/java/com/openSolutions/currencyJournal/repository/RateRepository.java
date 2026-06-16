package com.openSolutions.currencyJournal.repository;

import com.openSolutions.currencyJournal.entity.RateEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RateRepository extends JpaRepository<RateEntity, Long> {

    // ИСПРАВЛЕНО: добавлен JOIN FETCH
    @Query("SELECT r FROM RateEntity r LEFT JOIN FETCH r.country LEFT JOIN FETCH r.rateDict " +
            "WHERE r.currencyId = :currencyId AND r.rateDate BETWEEN :startDate AND :endDate " +
            "ORDER BY r.rateDate DESC")
    List<RateEntity> findByCurrencyIdAndPeriod(
            @Param("currencyId") String currencyId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Override
    @EntityGraph(attributePaths = {"country", "rateDict"})
    Page<RateEntity> findAll(Pageable pageable);

    @Query("SELECT r FROM RateEntity r LEFT JOIN FETCH r.country LEFT JOIN FETCH r.rateDict " +
            "WHERE r.rateDate >= :startDate ORDER BY r.rateDate DESC")
    Page<RateEntity> findByRateDateAfter(
            @Param("startDate") LocalDateTime startDate,
            Pageable pageable);

    @Query("SELECT r FROM RateEntity r LEFT JOIN FETCH r.country LEFT JOIN FETCH r.rateDict " +
            "WHERE r.currencyId = :currencyId AND r.rateDate BETWEEN :startDate AND :endDate " +
            "ORDER BY r.rateDate DESC")
    Page<RateEntity> findByCurrencyIdAndRateDateBetween(
            @Param("currencyId") String currencyId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    @EntityGraph(attributePaths = {"country", "rateDict"})
    Optional<RateEntity> findTopByCurrencyIdOrderByRateDateDesc(@Param("currencyId") String currencyId);

    @EntityGraph(attributePaths = {"country", "rateDict"})
    List<RateEntity> findByCurrencyIdIn(@Param("currencyIds") List<String> currencyIds);

    boolean existsByCurrencyIdAndRateDate(String currencyId, LocalDateTime rateDate);

    @Modifying
    @Transactional
    @Query("DELETE FROM RateEntity r WHERE r.rateDate BETWEEN :startDate AND :endDate")
    int deleteByRateDateBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Modifying
    @Transactional
    @Query("DELETE FROM RateEntity r WHERE r.currencyId = :currencyId AND r.rateDate BETWEEN :startDate AND :endDate")
    int deleteByCurrencyIdAndRateDateBetween(
            @Param("currencyId") String currencyId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // ИСПРАВЛЕНО: убран @EntityGraph, добавлен JOIN FETCH в @Query
    @Query("SELECT r FROM RateEntity r LEFT JOIN FETCH r.country LEFT JOIN FETCH r.rateDict " +
            "WHERE r.countryId = :countryId AND r.rateDate BETWEEN :startDate AND :endDate " +
            "ORDER BY r.rateDate DESC")
    List<RateEntity> findByCountryIdAndRateDateBetween(
            @Param("countryId") Long countryId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT DISTINCT r.currencyId FROM RateEntity r ORDER BY r.currencyId")
    List<String> findDistinctCurrencyIds();

    // ИСПРАВЛЕНО: добавлен JOIN FETCH
    @Query("SELECT r FROM RateEntity r LEFT JOIN FETCH r.country LEFT JOIN FETCH r.rateDict " +
            "WHERE r.nominal = :nominal AND r.rateDate BETWEEN :startDate AND :endDate")
    List<RateEntity> findByNominalAndRateDateBetween(
            @Param("nominal") Long nominal,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @EntityGraph(attributePaths = {"country", "rateDict"})
    List<RateEntity> findAllByRateDateAfterOrderByRateDateDesc(@Param("startDate") LocalDateTime startDate);

    // ИСПРАВЛЕНО: добавлен JOIN FETCH
    @Query("SELECT r FROM RateEntity r LEFT JOIN FETCH r.country LEFT JOIN FETCH r.rateDict " +
            "WHERE r.currencyId = :currencyId AND r.rateDate >= :startDate " +
            "ORDER BY r.rateDate DESC")
    Page<RateEntity> findByCurrencyIdAndRateDateAfter(
            @Param("currencyId") String currencyId,
            @Param("startDate") LocalDateTime startDate,
            Pageable pageable);

    // ИСПРАВЛЕНО: добавлен JOIN FETCH
    @Query("SELECT r FROM RateEntity r LEFT JOIN FETCH r.country LEFT JOIN FETCH r.rateDict " +
            "WHERE r.currencyId = :currencyId AND r.rateDate <= :endDate " +
            "ORDER BY r.rateDate DESC")
    Page<RateEntity> findByCurrencyIdAndRateDateBefore(
            @Param("currencyId") String currencyId,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    // ИСПРАВЛЕНО: добавлен @EntityGraph
    @EntityGraph(attributePaths = {"country", "rateDict"})
    Page<RateEntity> findByCurrencyId(String currencyId, Pageable pageable);

    // ИСПРАВЛЕНО: добавлен JOIN FETCH
    @Query("SELECT r FROM RateEntity r LEFT JOIN FETCH r.country LEFT JOIN FETCH r.rateDict " +
            "WHERE r.rateDate BETWEEN :startDate AND :endDate " +
            "ORDER BY r.rateDate DESC")
    Page<RateEntity> findByRateDateBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    // ИСПРАВЛЕНО: добавлен JOIN FETCH
    @Query("SELECT r FROM RateEntity r LEFT JOIN FETCH r.country LEFT JOIN FETCH r.rateDict " +
            "WHERE r.rateDate <= :endDate " +
            "ORDER BY r.rateDate DESC")
    Page<RateEntity> findByRateDateBefore(
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    Page<RateEntity> findByCountryId(Long countryId, Pageable pageable);

    @Query("SELECT r FROM RateEntity r WHERE r.countryId = :countryId " +
            "AND r.rateDate BETWEEN :startDate AND :endDate " +
            "ORDER BY r.rateDate DESC")
    Page<RateEntity> findByCountryIdAndRateDateBetween(
            @Param("countryId") Long countryId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    @Query("SELECT r FROM RateEntity r WHERE r.countryId = :countryId " +
            "AND r.rateDate >= :startDate " +
            "ORDER BY r.rateDate DESC")
    Page<RateEntity> findByCountryIdAndRateDateAfter(
            @Param("countryId") Long countryId,
            @Param("startDate") LocalDateTime startDate,
            Pageable pageable);

    @Query("SELECT r FROM RateEntity r WHERE r.countryId = :countryId " +
            "AND r.rateDate <= :endDate " +
            "ORDER BY r.rateDate DESC")
    Page<RateEntity> findByCountryIdAndRateDateBefore(
            @Param("countryId") Long countryId,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    // Комбинация countryId + currencyId
    Page<RateEntity> findByCountryIdAndCurrencyId(Long countryId, String currencyId, Pageable pageable);

    @Query("SELECT r FROM RateEntity r WHERE r.countryId = :countryId " +
            "AND r.currencyId = :currencyId " +
            "AND r.rateDate BETWEEN :startDate AND :endDate " +
            "ORDER BY r.rateDate DESC")
    Page<RateEntity> findByCountryIdAndCurrencyIdAndRateDateBetween(
            @Param("countryId") Long countryId,
            @Param("currencyId") String currencyId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    @Query("SELECT r FROM RateEntity r WHERE r.countryId = :countryId " +
            "AND r.currencyId = :currencyId " +
            "AND r.rateDate >= :startDate " +
            "ORDER BY r.rateDate DESC")
    Page<RateEntity> findByCountryIdAndCurrencyIdAndRateDateAfter(
            @Param("countryId") Long countryId,
            @Param("currencyId") String currencyId,
            @Param("startDate") LocalDateTime startDate,
            Pageable pageable);

    @Query("SELECT r FROM RateEntity r WHERE r.countryId = :countryId " +
            "AND r.currencyId = :currencyId " +
            "AND r.rateDate <= :endDate " +
            "ORDER BY r.rateDate DESC")
    Page<RateEntity> findByCountryIdAndCurrencyIdAndRateDateBefore(
            @Param("countryId") Long countryId,
            @Param("currencyId") String currencyId,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);
}