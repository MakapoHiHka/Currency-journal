package com.openSolutions.currencyJournal.entity.base;


public interface Identifiable<EID> {

    EID getId();
    void setId(EID id);
}