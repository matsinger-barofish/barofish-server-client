package com.matsinger.barofishserver.domain.coupon.domain;

public enum IssuanceState {
    TOBE_ISSUED, // 발급 예정
    ISSUING, // 발급중
    SUSPENDED; // 발급 중지
}
