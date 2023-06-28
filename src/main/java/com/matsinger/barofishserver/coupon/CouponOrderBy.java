package com.matsinger.barofishserver.coupon;

public enum CouponOrderBy {
    id("id"), title("title"), type("type"), amount("amount"), minPrice("minPrice"), startAt("startAt"), endAt("endAt");

    public final String label;

    private CouponOrderBy(String label) {
        this.label = label;
    }
}
