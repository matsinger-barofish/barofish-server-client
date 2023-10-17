package com.matsinger.barofishserver.product.weeksdate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@Builder @NoArgsConstructor @AllArgsConstructor
public class Holiday {

    private String dateName;       //  "임시공휴일",
    private String date;        //  20231002,
    private String isHoliday;      //  "Y",

    public boolean sameDate(String date) {
        if (Objects.equals(this.date, date)) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Holiday{" +
                "dateName='" + dateName + '\'' +
                ", date='" + date + '\'' +
                ", isHoliday='" + isHoliday + '\'' +
                '}';
    }
}
