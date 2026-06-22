package com.openSolutions.currencyJournal.mapper;

import org.springframework.data.domain.Pageable;

public interface PageableConverter<S> {
    Pageable getPageable(S request);
}
