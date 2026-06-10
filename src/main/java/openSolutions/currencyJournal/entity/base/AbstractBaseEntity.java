package openSolutions.currencyJournal.entity.base;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

// Базовый абстрактный класс сущности с идентификатором
@MappedSuperclass
@Getter
@Setter
public abstract class AbstractBaseEntity implements Identifiable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "base_seq_gen")
    @SequenceGenerator(name = "base_seq_gen", sequenceName = "base_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;
}