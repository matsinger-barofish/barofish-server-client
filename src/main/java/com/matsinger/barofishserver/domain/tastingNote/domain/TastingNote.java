package com.matsinger.barofishserver.domain.tastingNote.domain;

import com.matsinger.barofishserver.domain.order.orderprductinfo.domain.OrderProductInfo;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tasting_note")
@Getter
public class TastingNote {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "order_product_info_id", nullable = false)
    private Integer orderProductInfoId;

    @Size(min = 1, max = 5)
    @Column(name = "oily", nullable = false)
    private Integer oily;

    @Size(min = 1, max = 5)
    @Column(name = "taste_2", nullable = false)
    private Integer taste2;

    @Size(min = 1, max = 5)
    @Column(name = "taste_3", nullable = false)
    private Integer taste3;

    @Size(min = 1, max = 5)
    @Column(name = "taste_4", nullable = false)
    private Integer taste4;

    @Size(min = 1, max = 5)
    @Column(name = "taste_5", nullable = false)
    private Integer taste5;

    @Size(min = 1, max = 5)
    @Column(name = "tendernessSoftness", nullable = false)
    private Integer tendernessSoftness;

    @Size(min = 1, max = 5)
    @Column(name = "texture_2", nullable = false)
    private Integer texture2;

    @Size(min = 1, max = 5)
    @Column(name = "texture_3", nullable = false)
    private Integer texture3;

    @Size(min = 1, max = 5)
    @Column(name = "texture_4", nullable = false)
    private Integer texture4;

    @Size(min = 1, max = 5)
    @Column(name = "texture_5", nullable = false)
    private Integer texture5;
}
