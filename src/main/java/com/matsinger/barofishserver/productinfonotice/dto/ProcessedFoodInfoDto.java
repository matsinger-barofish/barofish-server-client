package com.matsinger.barofishserver.productinfonotice.dto;

import com.matsinger.barofishserver.product.domain.Product;
import com.matsinger.barofishserver.productinfonotice.domain.ProductInformation;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessedFoodInfoDto implements ProductInformation {

    @Id
    @Column(name = "product_id", nullable = false)

    private int product_id;
    private String nameOfProduct;                   // 식품의 유형
    private String producer;                        // 생산자 및 소재지 (수입품의 경우 생산자, 수입자 및 제조국)
    private String qualityMaintenanceDeadline;      // 제조연월일, 소비기한 또는 품질유지기한
    private String volume;                          // 포장단위별 내용물의 용량(중량), 수량
    private String rawMaterialInfo;                 // 원재료명 (농수산물의 원산지 표시 등에 관한 법률에 따른 원산지 표시 포함) 및 함량
    private String nutritionalIngredients;          // 영양성분(영양성분 표시대상 식품에 한함)
    private String geneticallyModifiedInfo;          // 유전자변형식품에 해당하는 경우의 표시
    private String importedPhrase;                  // 수입식품의 경우 “수입식품안전관리 특별법에 따른 수입신고를 필함”의 문구
    private String phoneNumber;                     // 소비자상담관련 전화번호
    private String cautionGuidelines;               // 소비자안전을 위한 주의사항
}
