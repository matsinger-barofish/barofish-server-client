package com.matsinger.barofishserver.order.exception;

public class OrderBusinessException extends RuntimeException {

    public OrderBusinessException() {
    }
    public OrderBusinessException(String message) {
        super(message);
    }

    public OrderBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
    public OrderBusinessException(Throwable cause) {
        super(cause);
    }

}
