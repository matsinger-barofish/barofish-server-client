package com.matsinger.barofishserver.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ErrorExceptionRequestInternal {

    private final String internalErrorFields1;
    private final Integer internalErrorFields2;

    @Override
    public String toString() {
        return "ErrorExceptionRequestInternal{" +
                "internalErrorFields1='" + internalErrorFields1 + '\'' +
                ", internalErrorFields2=" + internalErrorFields2 +
                '}';
    }
}
