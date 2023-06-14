package com.matsinger.barofishserver.product.productinfo;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Setter
@Table(name = "product_storage")
public class ProductStorage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String field;

//    @OneToOne(mappedBy = "productStorage")
//    private Product product;
//
//    public void setProduct(Product product) {
//        this.product = product;
//    }
}
