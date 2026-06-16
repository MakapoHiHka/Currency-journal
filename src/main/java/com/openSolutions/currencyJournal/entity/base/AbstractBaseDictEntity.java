package com.openSolutions.currencyJournal.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

// Базовый абстрактный класс сущности справочника
@MappedSuperclass
@Getter
@Setter
public abstract class AbstractBaseDictEntity<EID> extends AbstractBaseEntity<EID> {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "num_code", nullable = false, unique = true)
    private Integer numCode;

    @Column(name = "char_code", nullable = false, unique = true)
    private String charCode;
}