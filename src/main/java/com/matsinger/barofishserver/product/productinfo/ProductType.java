package com.matsinger.barofishserver.product.productinfo;

import com.matsinger.barofishserver.product.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product_type")
public class ProductType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String field;

    @OneToOne(mappedBy = "productType")
    private Product product;

    public void setProduct(Product product) {
        this.product = product;
    }
}
