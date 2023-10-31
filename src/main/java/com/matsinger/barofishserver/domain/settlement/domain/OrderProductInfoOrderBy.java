package com.matsinger.barofishserver.domain.settlement.domain;

public enum OrderProductInfoOrderBy {
    orderId("orderId"), productName("product.title"), settlePrice("settlePrice"), price("price"), amount("amount"), isSettled(
            "isSettled"), settledAt("settledAt");


    public final String label;

    OrderProductInfoOrderBy(String label) {
        this.label = label;
    }
}
