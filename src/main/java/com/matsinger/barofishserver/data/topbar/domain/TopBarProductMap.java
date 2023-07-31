package com.matsinger.barofishserver.data.topbar.domain;

import com.matsinger.barofishserver.data.topbar.domain.TopBar;
import com.matsinger.barofishserver.product.domain.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class TopBarProductMap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "top_bar_id")
    private TopBar topBar;


}