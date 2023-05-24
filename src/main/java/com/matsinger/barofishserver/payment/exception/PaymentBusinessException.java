package com.matsinger.barofishserver.payment.exception;

import com.matsinger.barofishserver.order.exception.OrderBusinessException;

public class PaymentBusinessException extends RuntimeException {

    public PaymentBusinessException() {
    }
    public PaymentBusinessException(String message) { super(message); }
    public PaymentBusinessException(String message, Throwable cause) {
    }
    public PaymentBusinessException(Throwable cause) { super(cause); }
}
