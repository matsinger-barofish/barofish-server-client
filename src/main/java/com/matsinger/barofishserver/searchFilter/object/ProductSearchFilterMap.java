package com.matsinger.barofishserver.searchFilter.object;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(ProductSearchFilterMapId.class)
@Table(name = "product_search_filter_map", schema = "barofish_dev", catalog = "")
public class ProductSearchFilterMap {
    @Id
    @Column(name = "product_id", nullable = false)
    private int productId;
    @Id
    @Column(name = "field_id", nullable = false)
    private int fieldId;
}
