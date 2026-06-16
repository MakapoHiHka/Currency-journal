package openSolutions.currencyJournal.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import openSolutions.currencyJournal.entity.base.AbstractBaseDictEntity;

import java.util.ArrayList;
import java.util.List;

// Сущность справочника стран-носителей валюты
@Entity
@Table(name = "countries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CountryEntity extends AbstractBaseDictEntity<Long> {


    @OneToMany(mappedBy = "country")
    @JsonIgnore
    private List<RateEntity> rates = new ArrayList<>();

    public List<RateEntity> rates() {
        return rates;
    }
}