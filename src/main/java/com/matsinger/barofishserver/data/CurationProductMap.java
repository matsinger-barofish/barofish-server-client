package com.matsinger.barofishserver.data;

import com.matsinger.barofishserver.product.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class CurationProductMap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column(name = "product_id")
//    private Long productId;
//
//    @Column(name = "curation_id")
//    private Long curationId;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "curation_id")
    private Curation curation;


}
