package com.matsinger.barofishserver.coupon.domain;

public enum CouponOrderBy {
    id("id"), title("title"), type("type"), amount("amount"), minPrice("minPrice"), startAt("startAt"), endAt("endAt");

    public final String label;

    CouponOrderBy(String label) {
        this.label = label;
    }
}
