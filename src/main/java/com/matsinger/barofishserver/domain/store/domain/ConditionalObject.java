package com.matsinger.barofishserver.domain.store.domain;

public interface ConditionalObject {
    public Integer getId();

    public Boolean meetConditions(int totalPrice);
}
