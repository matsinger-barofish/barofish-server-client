package com.matsinger.barofishserver.domain.inquiry.domain;

public enum InquiryOrderBy {
    type("type"), isSecret("isSecret"), productName("product.title"), userName("user.userInfo.name"),
    answeredAt("answeredAt"), createdAt(
            "createdAt");

    public final String label;

    InquiryOrderBy(String label) {
        this.label = label;
    }

}
