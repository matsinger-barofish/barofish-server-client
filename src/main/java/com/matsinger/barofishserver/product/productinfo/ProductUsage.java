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
@Table(name = "product_usage")
public class ProductUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String field;

    @OneToOne(mappedBy = "productUsage")
    private Product product;

    public void setProduct(Product product) {
        this.product = product;
    }
}
