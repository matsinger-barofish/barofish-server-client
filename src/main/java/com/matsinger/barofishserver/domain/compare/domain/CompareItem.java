package com.matsinger.barofishserver.domain.compare.domain;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(CompareItemId.class)
public class CompareItem implements Serializable {
    @Column(name = "compare_set_id", nullable = false)
    @Id
    private int compareSetId;
    @Column(name = "product_id", nullable = false)
    @Id
    private int productId;
}
