package com.matsinger.barofishserver.utils;

import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class Common {
    public ArrayList iteratorToList(Iterable data) {
        ArrayList list = new ArrayList<>();
        while (data.iterator().hasNext()) {
            list.add(data.iterator().next());
        }
        return list;
    }

    public String getPostWord(String str, String firstVal, String secondVal) {
        try {
            char laststr = str.charAt(str.length() - 1);
            if (laststr < 0xAC00 || laststr > 0xD7A3) {
                return str;
            }
            int lastCharIndex = (laststr - 0xAC00) % 28;
            if (lastCharIndex > 0) {
                if (firstVal.equals("으로") && lastCharIndex == 8) {
                    str += secondVal;
                } else {
                    str += firstVal;
                }
            } else {
                str += secondVal;
            }
        } catch (Exception e) {
        }
        return str;
    }

    public String validateString(String str, Long maxLen, String name) {
        if (str == null) throw new Error(String.format("%s 입력해주세요.", getPostWord(name, "을", "를")));
        str = str.trim();
        if (str.length() == 0) throw new Error(String.format("%s 입력해주세요.", getPostWord(name, "을", "를")));
        if (str.length() > maxLen)
            throw new Error(String.format("%s 최대 %d자 입니다.", getPostWord(name, "을", "를"), maxLen));
        return str;
    }
}
