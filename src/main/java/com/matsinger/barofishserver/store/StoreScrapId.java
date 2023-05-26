package com.matsinger.barofishserver.store;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreScrapId implements Serializable {
    private Integer storeId;
    private Integer userId;
}
