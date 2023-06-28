package com.matsinger.barofishserver.review.object;

public enum ReviewOrderBy {
    orderId("orderId"), productName("product.title"), storeName("store.storeInfo.name"), reviewerName(
            "user.userInfo.name"), reviewerEmail("user.userInfo.email"), createdAt("createdAt");

    public final String label;

    private ReviewOrderBy(String label) {
        this.label = label;
    }
}
