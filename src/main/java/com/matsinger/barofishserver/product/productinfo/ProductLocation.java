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
@Table(name = "product_location")
public class ProductLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String field;

    @OneToOne(mappedBy = "productLocation")
    private Product product;

    public void setProduct(Product product) {
        this.product = product;
    }
}
