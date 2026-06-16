package com.openSolutions.currencyJournal.entity.base;


import java.time.LocalDateTime;

// Интерфейс фиксации даты и времени создания/обновления сущности
public interface FixationDateTime {

    LocalDateTime getCreated();
    void setCreated(LocalDateTime created);
    LocalDateTime getUpdated();
    void setUpdated(LocalDateTime updated);
}