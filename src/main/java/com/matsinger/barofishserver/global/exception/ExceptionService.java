package com.matsinger.barofishserver.global.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExceptionService {

    public String serviceMethod() {
        return "exceptionService";
    }

    public String serviceExceptionMethod(String argument) {
        throw new BusinessException(String.format("서비스 예외 발생! - json"));
    }

}
