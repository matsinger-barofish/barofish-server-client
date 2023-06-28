package com.matsinger.barofishserver.order.object;

public enum OrderOrderBy {
    ordererName("ordererName"), id("id"), email("user.userInfo.email"), phone("orderTel"), receiverName(
            "deliverPlace.receiverName"), address("deliverPlace.address"), postalCode("deliverPlace.postalCode"), couponDiscount(
            "couponDiscount"), totalAmount("totalPrice"), orderAt("orderAt");
    public String label;

    private OrderOrderBy(String label) {
        this.label = label;
    }
}
