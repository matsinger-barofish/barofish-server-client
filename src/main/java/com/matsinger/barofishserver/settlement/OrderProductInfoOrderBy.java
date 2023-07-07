package com.matsinger.barofishserver.settlement;

public enum OrderProductInfoOrderBy {
    orderId("orderId"), productName("product.title"), settlePrice("settlePrice"), price("price"), amount("amount"), isSettled(
            "isSettled"), settledAt("settledAt");


    public final String label;

    OrderProductInfoOrderBy(String label) {
        this.label = label;
    }
}
