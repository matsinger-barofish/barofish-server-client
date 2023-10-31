package com.matsinger.barofishserver.global.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ExceptionTestController {

    private final ExceptionService exceptionService;

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @GetMapping("/exceptionEntity")
    public ExceptionEntity myData() {
        return new ExceptionEntity("myName");//object 반환
    }

    @GetMapping("/service")
    public String serviceCall() {
        return exceptionService.serviceMethod();//일반적인 service호출
    }

    @GetMapping("/serviceException")
    public String serviceExceptionGet(@RequestParam(value = "errorParam") String errorParam) {
        return exceptionService.serviceExceptionMethod("에러 발생 인자 - parameter"); //service에서 예외발생
    }

    @PostMapping("/serviceException")
    public String serviceExceptionPost(@RequestBody ExceptionRequest request) {
        return exceptionService.serviceExceptionMethod("에러 발생 인자 - json"); //service에서 예외발생
    }

    @GetMapping("/controllerException")
    public void controllerException() {
        throw new NullPointerException();//controller에서 예외발생
    }

    @GetMapping("/customException")
    public String custom() {
        throw new CustomException("커스텀 예외 발생!");//custom예외 발생
    }
}
