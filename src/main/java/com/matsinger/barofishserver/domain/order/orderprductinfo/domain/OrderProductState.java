package com.matsinger.barofishserver.domain.order.orderprductinfo.domain;

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
    REFUND_DONE,
    DELIVERY_DIFFICULT,
    CANCELED_BY_PARTNER,
    CANCELED_BY_ADMIN;

    public static boolean isCanceled(OrderProductState state) {
        if (!state.equals(OrderProductState.CANCELED) &&
            !state.equals(OrderProductState.CANCELED_BY_ADMIN) &&
            !state.equals(OrderProductState.CANCELED_BY_PARTNER)) {
            return false;
        }
        return true;
    }
}
