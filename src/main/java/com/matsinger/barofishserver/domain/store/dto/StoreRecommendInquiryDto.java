package com.matsinger.barofishserver.domain.store.dto;

import lombok.Getter;

@Getter
public class StoreRecommendInquiryDto {

    private Integer storeId;
    private String loginId;
    private String backgroundImage;
    private Boolean isReliable;
    private String profileImage;
    private String name;
    private String location;
    private String keyword;
    private String visitNote;
    private Integer refundDeliverFee;
    private String oneLineDescription;
    private String deliverCompany;

    private Boolean isLike;

    public SimpleStore toDto(String[] keyword) {
        return SimpleStore.builder()
                .storeId(this.storeId)
                .loginId(this.loginId)
                .backgroundImage(this.backgroundImage)
                .isReliable(this.isReliable)
                .profileImage(this.profileImage)
                .name(this.name)
                .location(this.location)
                .keyword(keyword)
                .visitNote(this.visitNote)
                .refundDeliverFee(this.refundDeliverFee)
                .oneLineDescription(this.oneLineDescription)
                .deliverCompany(this.deliverCompany)
                .isLike(this.isLike)
                .build();
    }
}
