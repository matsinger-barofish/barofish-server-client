package com.matsinger.barofishserver.order;

public enum OrderState {
    NONE,
    PAYMENT_PREPARATION,
    LACK_OF_DEPOSIT,
    PAYMENT_COMPLETED,
    DELIVERY_READY,
    CANCEL_REQUEST,
    ON_DELIVERY,
    FINAL_CONFIRMED
}
