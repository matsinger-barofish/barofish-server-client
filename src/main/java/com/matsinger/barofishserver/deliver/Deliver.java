package com.matsinger.barofishserver.deliver;

import lombok.*;

import java.util.List;

public class Deliver {
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Company {
        private String Code;
        private Boolean International;
        private String Name;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrackingDetails {
        private String kind;
        private String code;
        private Integer level;
        private String manName;
        private String manPic;
        private String timeString;
        private String where;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrackingInfo {
        private String adUrl;
        private List<TrackingDetails> trackingDetails;
        private String invoiceNo;
        private String itemImage;
        private String itemName;
        private Integer level;
        private String result;
        private String senderName;
    }

}
