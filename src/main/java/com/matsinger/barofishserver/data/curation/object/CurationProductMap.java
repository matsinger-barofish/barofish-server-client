package com.matsinger.barofishserver.data.curation.object;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.matsinger.barofishserver.product.object.Product;
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

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "curation_id")
    private Curation curation;


}
