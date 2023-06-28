package com.matsinger.barofishserver.product.filter;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(ProductFilterValueId.class)
@Table(name = "product_filter_value", schema = "barofish_dev", catalog = "")
public class ProductFilterValue {
    @Id
    @Column(name = "compare_filter_id", nullable = false)
    private int compareFilterId;

    @Id
    @Column(name = "product_id", nullable = false)
    private int productId;

    @Basic
    @Column(name = "value", nullable = false)
    private String value;
}
