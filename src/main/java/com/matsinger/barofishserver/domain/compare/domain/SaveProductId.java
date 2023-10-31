package com.matsinger.barofishserver.domain.compare.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaveProductId  implements Serializable {
    private Integer userId;
    private Integer productId;
}
