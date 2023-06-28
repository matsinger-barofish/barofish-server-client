package com.matsinger.barofishserver.product.filter;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductFilterValueId implements Serializable {
    private int compareFilterId;
    private int productId;
}
