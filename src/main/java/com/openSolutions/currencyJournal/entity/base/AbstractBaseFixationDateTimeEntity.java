package com.openSolutions.currencyJournal.entity.base;



import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

// Базовый абстрактный класс сущности с фиксацией даты и времени создания/обновления
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class AbstractBaseFixationDateTimeEntity<EID> extends AbstractBaseEntity<EID> implements FixationDateTime {

    // Дата и время создания записи (заполняется автоматически при сохранении)
    @CreatedDate
    @Column(name = "created", nullable = false, updatable = false)
    private LocalDateTime created;

    // Дата и время последнего обновления (заполняется автоматически при изменении)
    @LastModifiedDate
    @Column(name = "updated")
    private LocalDateTime updated;
}