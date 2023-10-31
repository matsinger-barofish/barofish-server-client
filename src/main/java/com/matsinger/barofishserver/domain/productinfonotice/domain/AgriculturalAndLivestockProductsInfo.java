package com.matsinger.barofishserver.domain.productinfonotice.domain;

import com.matsinger.barofishserver.domain.productinfonotice.dto.AgriculturalAndLivestockProductsInfoDto;
import com.matsinger.barofishserver.domain.product.domain.Product;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @OneToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "name_of_product", nullable = false)
    private String nameOfProduct;

    @Column(name = "volume", nullable = false)
    private String volume;

    @Column(name = "producer", nullable = false)
    private String producer;

    @Column(name = "origin_country", nullable = false)
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

    @Column(name = "caution_guidelines", nullable = false)
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

    public AgriculturalAndLivestockProductsInfoDto toDto() {
        return AgriculturalAndLivestockProductsInfoDto.builder()
                .productId(product.getId())
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

    public void update(AgriculturalAndLivestockProductsInfoDto dto) {
        this.nameOfProduct = dto.getNameOfProduct();
        this.volume = dto.getVolume();
        this.producer = dto.getProducer();
        this.originCountry = dto.getOriginCountry();
        this.qualityMaintenanceDeadline = dto.getQualityMaintenanceDeadline();
        this.geneticallyModifiedInfo = dto.getGeneticallyModifiedInfo();
        this.productGrade = dto.getProductGrade();
        this.importInformation = dto.getImportInformation();
        this.contentsOfProduct = dto.getContentsOfProduct();
        this.howToKeep = dto.getHowToKeep();
        this.phoneNumber = dto.getPhoneNumber();
        this.cautionGuidelines = dto.getCautionGuidelines();
    }
}
