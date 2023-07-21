package com.matsinger.barofishserver.productinfo.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product_location")
public class ProductLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String field;

//    @OneToMany(mappedBy = "productLocation")
//    private Product product;

//    public void setProduct(Product product) {
//        this.product = product;
//    }
}
