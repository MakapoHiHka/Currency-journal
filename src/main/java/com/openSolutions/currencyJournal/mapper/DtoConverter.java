package com.openSolutions.currencyJournal.mapper;

public interface DtoConverter<S, D> {
    D convert(S source);
}
