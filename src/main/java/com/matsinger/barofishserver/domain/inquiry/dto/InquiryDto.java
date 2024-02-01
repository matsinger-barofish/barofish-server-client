package com.matsinger.barofishserver.domain.inquiry.dto;

import com.matsinger.barofishserver.domain.inquiry.domain.InquiryType;
import com.matsinger.barofishserver.domain.product.dto.ProductListDto;
import com.matsinger.barofishserver.domain.store.dto.SimpleStore;
import com.matsinger.barofishserver.domain.userinfo.dto.UserInfoDto;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InquiryDto {
    private int id;
    private InquiryType type;
    private Boolean isSecret;
    private int productId;
    private UserInfoDto user;
    private String content;
    private Timestamp createdAt;
    private String answer;
    private Timestamp answeredAt;
    private ProductListDto product;
    private SimpleStore store;
    private Boolean isMine;
}
