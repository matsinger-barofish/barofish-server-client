package com.matsinger.barofishserver.product.domain;

public enum ProductOrderBy {
    id("id"), storeName("store.storeInfo.name"), state("state"), title("title"), categoryName("category.name"), originPrice(
            "originPrice"), discountRate("discountRate"), createdAt("createdAt");

    public final String label;

    ProductOrderBy(String label) {
        this.label = label;
    }
}
