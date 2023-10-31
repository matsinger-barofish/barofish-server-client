package com.matsinger.barofishserver.domain.settlement.domain;

public enum SettlementOrderBy {
    storeName("store.name"), state("state"), settleAmount("settleAmount"), settledAt("settledAt");


    public final String label;

    SettlementOrderBy(String label) {
        this.label = label;
    }
}
