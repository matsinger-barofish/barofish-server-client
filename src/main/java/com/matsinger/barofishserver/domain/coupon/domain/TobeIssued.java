package com.matsinger.barofishserver.domain.coupon.domain;

public enum TobeIssued {
    ALL_USER, // 모든 유저에게 발급
    NEW_USER, // 신규 유저에게 발급 (해당 타입은 하나만 존재함)
    INDIVIDUAL_USER, // 개인에게 발급
    PER_PURCHASE; // 구매 횟수당 발급 (해당 타입은 하나만 존재함)
}
