package com.matsinger.barofishserver.inquiry;

import com.matsinger.barofishserver.product.object.ProductListDto;
import com.matsinger.barofishserver.store.object.SimpleStore;
import com.matsinger.barofishserver.user.object.UserInfoDto;
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
}
