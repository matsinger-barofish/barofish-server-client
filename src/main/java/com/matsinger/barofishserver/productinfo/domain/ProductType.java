package com.matsinger.barofishserver.productinfo.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Entity
@Table(name = "product_type")
public class ProductType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String field;

//    @OneToOne(mappedBy = "productType")
//    private Product product;
//
//    public void setProduct(Product product) {
//        this.product = product;
//    }
}
