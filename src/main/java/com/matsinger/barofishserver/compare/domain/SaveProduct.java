package com.matsinger.barofishserver.compare.domain;

import com.matsinger.barofishserver.compare.domain.SaveProductId;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(SaveProductId.class)
@Table(name = "save_product", schema = "barofish_dev", catalog = "")
public class SaveProduct {
    @Column(name = "user_id", nullable = false)
    @Id
    private int userId;
    @Column(name = "product_id", nullable = false)
    @Id
    private int productId;
}
