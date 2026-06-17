package com.openSolutions.currencyJournal.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.openSolutions.currencyJournal.domain.entity.base.AbstractBaseDictEntity;

import java.util.ArrayList;
import java.util.List;

// Сущность справочника валют
@Entity
@Table(name = "rate_dict")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RateDictEntity extends AbstractBaseDictEntity<Long> {

    // Список курсов валют, связанных с данным справочником валюты
    @OneToMany(mappedBy = "rateDict")
    @JsonIgnore
    private List<RateEntity> rates = new ArrayList<>();

//     Метод для получения списка курсов валют
    public List<RateEntity> rates() {
        return rates;
    }
}