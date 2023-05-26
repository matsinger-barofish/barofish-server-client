package com.matsinger.barofishserver.compare;

import com.matsinger.barofishserver.product.Product;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(SaveProductId.class)
@Table(name = "save_product", schema = "barofish_dev", catalog = "")
public class SaveProduct {
    @Column(name = "user_id", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;
    @Column(name = "product_id", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int productId;
}
