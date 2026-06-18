package com.openSolutions.currencyJournal.repository;

import com.openSolutions.currencyJournal.domain.entity.RateEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RateRepository extends JpaRepository<RateEntity, Long>, JpaSpecificationExecutor<RateEntity> {

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

    @EntityGraph(attributePaths = {"country", "rateDict"})
    Optional<RateEntity> findTopByCurrencyIdOrderByRateDateDesc(@Param("currencyId") String currencyId);

    boolean existsByCurrencyIdAndRateDate(String currencyId, LocalDateTime rateDate);

}