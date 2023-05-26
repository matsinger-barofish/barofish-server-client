package com.matsinger.barofishserver.compare;

import com.matsinger.barofishserver.product.Product;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(CompareItemId.class)
public class CompareItem implements Serializable {
    @Column(name = "compare_set_id", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int compareSetId;
    @Column(name = "product_id", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int productId;


//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "product_id")
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Product product;

    public int getCompareSetId() {
        return compareSetId;
    }

    public void setCompareSetId(int compareSetId) {
        this.compareSetId = compareSetId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompareItem that = (CompareItem) o;
        return compareSetId == that.compareSetId && productId == that.productId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(compareSetId, productId);
    }
}
