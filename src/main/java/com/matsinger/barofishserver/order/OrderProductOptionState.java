package com.matsinger.barofishserver.order;

public enum OrderProductOptionState {

    CANCEL_REQUEST,
    CANCELED,
    DELIVERY_READY,
    ON_DELIVERY,
    DELIVERY_DONE,
    EXCHANGE_REQUEST,
    EXCHANGE_ACCEPT,
    REFUND_REQUEST,
    REFUND_ACCEPT,
    REFUND_DONE,
    FINAL_CONFIRM
}
