package com.openSolutions.currencyJournal.domain.entity.base;


public interface Identifiable<EID> {

    EID getId();
    void setId(EID id);
}