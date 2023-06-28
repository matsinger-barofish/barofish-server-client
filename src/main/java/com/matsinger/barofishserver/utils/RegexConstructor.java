package com.matsinger.barofishserver.utils;

import lombok.*;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Component
public class RegexConstructor {

    public String email =

            "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"" +
                    "(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
    public String phone = "^(?:\\+?82-?|0)(1(?:0|1|[6-9]))[.-]?(\\d{3,4})[.-]?(\\d{4})$";
    public String
            password =
            "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_\\-+=\\[\\]{}|\\\\:;\"'<>,.?/])(?=.*[^\\s]).{8,20}$";
    public String httpUrl = "^https?:\\/\\/[-\\w.]+(:\\d+)?(\\/([\\w/_.]*(\\?\\S+)?)?)?";


    public String cardNo = "^(\\d{4})-?(\\d{4})-?(\\d{4})-?(\\d{4})$";
    public String expiryAt = "^(0[1-9]|1[0-2])\\/\\d{2}$";
    public String birth = "^\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])$";
    public String cardPassword = "^\\d{2}$";

}
