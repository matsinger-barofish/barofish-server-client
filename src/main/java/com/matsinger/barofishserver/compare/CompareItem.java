package com.matsinger.barofishserver.compare;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.io.Serializable;
import java.util.Objects;

public class CompareItem implements Serializable {
    @Column(name = "compare_set_id", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int compareSetId;
    @Column(name = "product_id", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int productId;

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
