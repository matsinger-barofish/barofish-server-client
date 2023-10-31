package com.matsinger.barofishserver.domain.store.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "store_scrap", schema = "barofish_dev", catalog = "")
@IdClass(StoreScrapId.class)
public class StoreScrap {
    @Column(name = "user_id", nullable = false)
    @Id
    private int userId;

    @Column(name = "store_id", nullable = false)
    @Id
    private int storeId;

}
