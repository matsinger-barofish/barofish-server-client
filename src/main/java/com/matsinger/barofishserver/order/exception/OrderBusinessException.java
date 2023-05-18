package com.example.demo.order.exception;

public class OrderBusinessException extends RuntimeException {
    public OrderBusinessException(String message) {
        super(message);
    }
}
