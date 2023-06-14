package com.matsinger.barofishserver.product.productinfo;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product_process")
public class ProductProcess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String field;

//    @OneToOne(mappedBy = "productProcess")
//    private Product product;
//
//    public void setProduct(Product product) {
//        this.product = product;
//    }
}
