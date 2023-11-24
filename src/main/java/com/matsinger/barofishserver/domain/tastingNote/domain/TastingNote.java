package com.matsinger.barofishserver.domain.tastingNote.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder @NoArgsConstructor @AllArgsConstructor
@Table(name = "tasting_note")
public class TastingNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "order_product_info_id", nullable = false)
    private Integer orderProductInfoId;

    @Column(name = "product_id", nullable = false)
    private Integer productId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Min(1) @Max(5)
    @Column(name = "taste_1", nullable = false)
    private Integer taste1;

    @Min(1) @Max(5)
    @Column(name = "taste_2", nullable = false)
    private Integer taste2;

    @Min(1) @Max(5)
    @Column(name = "taste_3", nullable = false)
    private Integer taste3;

    @Min(1) @Max(5)
    @Column(name = "taste_4", nullable = false)
    private Integer taste4;

    @Min(1) @Max(5)
    @Column(name = "taste_5", nullable = false)
    private Integer taste5;

    @Min(1) @Max(5)
    @Column(name = "texture_1", nullable = false)
    private Integer tendernessSoftness;

    @Min(1) @Max(5)
    @Column(name = "texture_2", nullable = false)
    private Integer texture2;

    @Min(1) @Max(5)
    @Column(name = "texture_3", nullable = false)
    private Integer texture3;

    @Min(1) @Max(5)
    @Column(name = "texture_4", nullable = false)
    private Integer texture4;

    @Min(1) @Max(5)
    @Column(name = "texture_5", nullable = false)
    private Integer texture5;

    @Min(1) @Max(5)
    @Column(name = "texture_6", nullable = false)
    private Integer texture6;

    @Min(1) @Max(5)
    @Column(name = "texture_7", nullable = false)
    private Integer texture7;

    @Min(1) @Max(5)
    @Column(name = "texture_8", nullable = false)
    private Integer texture8;
}
