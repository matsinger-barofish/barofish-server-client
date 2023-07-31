package com.matsinger.barofishserver.order.orderprductinfo.domain;

public enum OrderProductState {
    WAIT_DEPOSIT,
    PAYMENT_DONE,
    DELIVERY_READY,
    ON_DELIVERY,
    DELIVERY_DONE,
    EXCHANGE_REQUEST,
    EXCHANGE_ACCEPT,
    FINAL_CONFIRM,
    CANCELED,
    CANCEL_REQUEST,
    REFUND_REQUEST,
    REFUND_ACCEPT,
    REFUND_DONE
}
