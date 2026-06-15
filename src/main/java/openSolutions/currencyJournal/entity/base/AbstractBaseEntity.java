package openSolutions.currencyJournal.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

// Базовый абстрактный класс сущности с идентификатором
@MappedSuperclass
@Getter
@Setter
public abstract class AbstractBaseEntity implements Identifiable {

    // Уникальный идентификатор сущности
    // IDENTITY означает, что БД сама генерирует значение (автоинкремент)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
}