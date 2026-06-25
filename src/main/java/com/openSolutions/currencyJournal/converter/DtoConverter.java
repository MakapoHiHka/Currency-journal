package com.openSolutions.currencyJournal.converter;

public interface DtoConverter<S, D> {
    D convert(S source);
}
