package openSolutions.currencyJournal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import openSolutions.currencyJournal.entity.base.AbstractBaseFixationDateTimeEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Сущность журнала курса валют
@Entity
@Table(name = "rates", indexes = {
        // Индекс для ускорения поиска по валюте и дате
        @Index(name = "idx_rates_currency_date", columnList = "currency_id, rate_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RateEntity extends AbstractBaseFixationDateTimeEntity {

    @Column(name = "currency_id", nullable = false)
    private String currencyId;

    // Идентификатор страны
    @Column(name = "country_id", nullable = false)
    private Long countryId;

    // Связь со страной-носителем валюты
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", insertable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_rates_country"))
    private CountryEntity country;

    // Идентификатор справочника валют (внешний ключ на таблицу rate_dict)
    @Column(name = "rate_dict_id", nullable = false)
    private Long rateDictId;

    // Связь со справочником валюты
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rate_dict_id", insertable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_rates_rate_dict"))
    private RateDictEntity rateDict;

    // Номинальное значение курса валюты
    @Column(name = "nominal", nullable = false)
    private Long nominal;

    // Значение курса валюты по отношению к рублю
    @Column(name = "value", nullable = false, precision = 10, scale = 4)
    private BigDecimal value;

    // Дата и время курса валюты
    @Column(name = "rate_date", nullable = false)
    private LocalDateTime rateDate;
}