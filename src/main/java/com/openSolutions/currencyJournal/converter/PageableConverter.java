package com.openSolutions.currencyJournal.converter;

import org.springframework.data.domain.Pageable;

public interface PageableConverter<S> {
    Pageable getPageable(S request);
}
