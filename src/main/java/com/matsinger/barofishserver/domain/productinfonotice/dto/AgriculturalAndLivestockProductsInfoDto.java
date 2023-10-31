package com.matsinger.barofishserver.domain.productinfonotice.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.matsinger.barofishserver.domain.product.domain.Product;
import com.matsinger.barofishserver.domain.productinfonotice.domain.AgriculturalAndLivestockProductsInfo;
import com.matsinger.barofishserver.domain.productinfonotice.domain.ProductInformation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeName("19")
public class AgriculturalAndLivestockProductsInfoDto implements ProductInformation {

    private final String itemCode = "19";
    private Integer productId;
    private String nameOfProduct;                   // 품목 또는 명칭
    private String volume;                          // 포장단위별 내용물의 용량(중량), 수량, 크기
    private String producer;                        // 생산자,수입품의 경우 수입자를 함께 표기
    private String originCountry;                   // 농수산물의 원산지 표시 등에 관한 법률에 따른 원산지
    private String qualityMaintenanceDeadline;      // 제조연월일, 소비기한 또는 품질유지기한
    private String geneticallyModifiedInfo;          // 농수산물-농수산물 품질관리법에 따른 유전자변형농산물 표시, 지리적 표시
    private String productGrade;                    // 축산물 – 축산법에 따른 등급 표시, 가축 및 축산물 이력관리에 관한 법률에 따른 이력관리대상축산물 유무
    private String importInformation;               // 수입 농수축산물 - “수입식품안전관리 특별법에 따른 수입신고를 필함”의 문구
    private String contentsOfProduct;               // 상품구성
    private String howToKeep;                       // 보관방법 또는 취급방법
    private String phoneNumber;                     // 소비자상담관련 전화번호
    private String cautionGuidelines;               // 소비자안전을 위한 주의사항

    public AgriculturalAndLivestockProductsInfo toEntity(Product product) {
        return AgriculturalAndLivestockProductsInfo.builder()
                .product(product)
                .nameOfProduct(this.nameOfProduct)
                .volume(this.volume)
                .producer(this.producer)
                .originCountry(this.originCountry)
                .qualityMaintenanceDeadline(this.qualityMaintenanceDeadline)
                .geneticallyModifiedInfo(this.geneticallyModifiedInfo)
                .productGrade(this.productGrade)
                .importInformation(this.importInformation)
                .contentsOfProduct(this.contentsOfProduct)
                .howToKeep(this.howToKeep)
                .phoneNumber(this.phoneNumber)
                .cautionGuidelines(this.cautionGuidelines)
                .build();
    }
}
