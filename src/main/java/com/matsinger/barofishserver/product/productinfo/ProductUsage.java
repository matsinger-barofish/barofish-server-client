package com.matsinger.barofishserver.product.productinfo;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Setter
@Table(name = "product_usage")
public class ProductUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String field;

//    @OneToOne(mappedBy = "productUsage")
//    private Product product;
//
//    public void setProduct(Product product) {
//        this.product = product;
//    }
}
