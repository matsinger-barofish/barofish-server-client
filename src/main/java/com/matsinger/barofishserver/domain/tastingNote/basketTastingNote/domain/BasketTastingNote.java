package com.matsinger.barofishserver.domain.tastingNote.basketTastingNote.domain;

import com.matsinger.barofishserver.domain.product.domain.Product;
import com.matsinger.barofishserver.domain.tastingNote.domain.TastingNote;
import com.matsinger.barofishserver.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "basket_tasting_note")
@Getter
public class BasketTastingNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @OneToMany
    @Column(name = "product_id", nullable = false)
    private List<Product> product;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
