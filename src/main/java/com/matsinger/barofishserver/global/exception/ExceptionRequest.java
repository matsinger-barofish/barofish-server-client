package com.matsinger.barofishserver.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ExceptionRequest {

    private final String errorFields1;
    private final Integer errorFields2;
    private List<ErrorExceptionRequestInternal> internalFields;

    @Override
    public String toString() {
        return "ExceptionRequest{" +
                "errorFields1='" + errorFields1 + '\'' +
                ", errorFields2=" + errorFields2 +
                ", internalFields=" + internalFields +
                '}';
    }
}
