package com.matsinger.barofishserver.review.domain;

public enum ReviewOrderBy {
    orderId("orderId"), productName("product.title"), storeName("store.storeInfo.name"), reviewerName(
            "user.userInfo.name"), reviewerEmail("user.userInfo.email"), createdAt("createdAt");

    public final String label;

    ReviewOrderBy(String label) {
        this.label = label;
    }
}
