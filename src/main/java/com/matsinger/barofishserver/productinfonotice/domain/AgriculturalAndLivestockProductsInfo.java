package com.matsinger.barofishserver.productinfonotice.domain;

import com.matsinger.barofishserver.product.domain.Product;
import com.matsinger.barofishserver.productinfonotice.dto.AgriculturalAndLivestockProductsInfoDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "agricultural_and_livestock_products")
public class AgriculturalAndLivestockProductsInfo {

    @Id
    @Column(name = "product_id", nullable = false)
    private int product_id;

    @OneToOne
    @JoinColumn(name = "product_id", nullable = false)
    @MapsId
    private Product product;

    @Column(name = "name_of_product", nullable = false)
    private String nameOfProduct;

    @Column(name = "volume", nullable = false)
    private String volume;

    @Column(name = "producer", nullable = false)
    private String producer;

    @Column(name = "originCountry", nullable = false)
    private String originCountry;

    @Column(name = "quality_maintenance_deadline", nullable = false)
    private String qualityMaintenanceDeadline;

    @Column(name = "genetically_modified_info", nullable = false)
    private String geneticallyModifiedInfo;

    @Column(name = "product_grade", nullable = false)
    private String productGrade;

    @Column(name = "import_information", nullable = false)
    private String importInformation;

    @Column(name = "contents_of_product", nullable = false)
    private String contentsOfProduct;

    @Column(name = "how_to_keep", nullable = false)
    private String howToKeep;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "cautionGuidelines", nullable = false)
    private String cautionGuidelines;

    public static AgriculturalAndLivestockProductsInfoDto getForm() {
        return AgriculturalAndLivestockProductsInfoDto.builder()
                .nameOfProduct(null)
                .volume(null)
                .producer(null)
                .originCountry(null)
                .qualityMaintenanceDeadline(null)
                .geneticallyModifiedInfo(null)
                .productGrade(null)
                .importInformation(null)
                .contentsOfProduct(null)
                .howToKeep(null)
                .phoneNumber(null)
                .cautionGuidelines(null)
                .build();
    }
}
