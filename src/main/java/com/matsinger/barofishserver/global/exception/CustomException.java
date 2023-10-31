package com.matsinger.barofishserver.global.exception;

public class CustomException extends RuntimeException {

    private String errorMessage;
    public CustomException(String message) {
        super(message);
    }


}
