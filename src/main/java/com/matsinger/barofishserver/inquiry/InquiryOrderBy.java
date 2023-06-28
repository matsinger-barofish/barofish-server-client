package com.matsinger.barofishserver.inquiry;

public enum InquiryOrderBy {
    type("type"), isSecret("isSecret"), productName("product.title"), userName("user.userInfo.name"),
    answeredAt("answeredAt"), createdAt(
            "createdAt");

    public final String label;

    private InquiryOrderBy(String label) {
        this.label = label;
    }

}
