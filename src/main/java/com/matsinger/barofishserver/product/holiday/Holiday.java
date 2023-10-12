package com.matsinger.barofishserver.product.holiday;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder @NoArgsConstructor @AllArgsConstructor
public class Holiday {

    private String dateName;       //  "임시공휴일",
    private String date;        //  20231002,
    private String isHoliday;      //  "Y",

    @Override
    public String toString() {
        return "Holiday{" +
                "dateName='" + dateName + '\'' +
                ", date='" + date + '\'' +
                ", isHoliday='" + isHoliday + '\'' +
                '}';
    }
}
