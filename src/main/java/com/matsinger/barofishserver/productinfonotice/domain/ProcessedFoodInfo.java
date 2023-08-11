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
    @Column(name = "product_id", nullable = false)
    private int product_id;

    @OneToOne
    @JoinColumn(name = "product_id", nullable = false)
    @MapsId
    private Product product;

    @Column(name = "name_of_product", nullable = false)
    private String nameOfProduct;

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

    @Column(name = "cautionGuidelines", nullable = false)
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
}
