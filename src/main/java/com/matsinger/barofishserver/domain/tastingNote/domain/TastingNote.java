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

    @Min(0) @Max(10)
    @Column(name = "taste_1", nullable = false)
    private Double taste1;

    @Min(0) @Max(10)
    @Column(name = "taste_2", nullable = false)
    private Double taste2;

    @Min(0) @Max(10)
    @Column(name = "taste_3", nullable = false)
    private Double taste3;

    @Min(0) @Max(10)
    @Column(name = "taste_4", nullable = false)
    private Double taste4;

    @Min(0) @Max(10)
    @Column(name = "taste_5", nullable = false)
    private Double taste5;

    @Min(0) @Max(10)
    @Column(name = "texture_1", nullable = false)
    private Double texture1;

    @Min(0) @Max(10)
    @Column(name = "texture_2", nullable = false)
    private Double texture2;

    @Min(0) @Max(10)
    @Column(name = "texture_3", nullable = false)
    private Double texture3;

    @Min(0) @Max(10)
    @Column(name = "texture_4", nullable = false)
    private Double texture4;

    @Min(0) @Max(10)
    @Column(name = "texture_5", nullable = false)
    private Double texture5;

    @Min(0) @Max(10)
    @Column(name = "texture_6", nullable = false)
    private Double texture6;

    @Min(0) @Max(10)
    @Column(name = "texture_7", nullable = false)
    private Double texture7;

    @Min(0) @Max(10)
    @Column(name = "texture_8", nullable = false)
    private Double texture8;
}
