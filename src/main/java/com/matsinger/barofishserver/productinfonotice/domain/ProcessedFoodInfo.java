package com.matsinger.barofishserver.productinfonotice.domain;

import com.matsinger.barofishserver.product.domain.Product;
import com.matsinger.barofishserver.productinfonotice.dto.ProcessedFoodInfoDto;
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
@Table(name = "processed_food")
public class ProcessedFoodInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @OneToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "name_of_product", nullable = false)
    private String nameOfProduct;

    @Column(name = "types_of_food", nullable = false)
    private String typesOfFood;

    @Column(name = "producer", nullable = false)
    private String producer;

    @Column(name = "quality_maintenance_deadline", nullable = false)
    private String qualityMaintenanceDeadline;

    @Column(name = "volume", nullable = false)
    private String volume;

    @Column(name = "raw_material_info", nullable = false)
    private String rawMaterialInfo;

    @Column(name = "nutritional_ingredients", nullable = false)
    private String nutritionalIngredients;

    @Column(name = "genetically_modified_info", nullable = false)
    private String geneticallyModifiedInfo;

    @Column(name = "imported_phrase", nullable = false)
    private String importedPhrase;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "caution_guidelines", nullable = false)
    private String cautionGuidelines;

    public static ProcessedFoodInfoDto getForm() {
        return ProcessedFoodInfoDto.builder()
                .nameOfProduct(null)
                .producer(null)
                .qualityMaintenanceDeadline(null)
                .volume(null)
                .rawMaterialInfo(null)
                .nutritionalIngredients(null)
                .geneticallyModifiedInfo(null)
                .importedPhrase(null)
                .phoneNumber(null)
                .cautionGuidelines(null)
                .build();
    }

    public ProcessedFoodInfoDto toDto() {
        return ProcessedFoodInfoDto.builder()
                .productId(product.getId())
                .nameOfProduct(this.nameOfProduct)
                .producer(this.producer)
                .qualityMaintenanceDeadline(this.qualityMaintenanceDeadline)
                .volume(this.volume)
                .rawMaterialInfo(this.rawMaterialInfo)
                .nutritionalIngredients(this.nutritionalIngredients)
                .geneticallyModifiedInfo(this.geneticallyModifiedInfo)
                .importedPhrase(this.importedPhrase)
                .phoneNumber(this.phoneNumber)
                .cautionGuidelines(this.cautionGuidelines)
                .build();
    }

    public void update (ProcessedFoodInfoDto dto) {
        this.nameOfProduct = dto.getNameOfProduct();
        this.producer = dto.getProducer();
        this.qualityMaintenanceDeadline = dto.getQualityMaintenanceDeadline();
        this.volume = dto.getVolume();
        this.rawMaterialInfo = dto.getRawMaterialInfo();
        this.nutritionalIngredients = dto.getNutritionalIngredients();
        this.geneticallyModifiedInfo = dto.getGeneticallyModifiedInfo();
        this.importedPhrase = dto.getImportedPhrase();
        this.phoneNumber = dto.getPhoneNumber();
        this.cautionGuidelines = dto.getCautionGuidelines();
    }
}
