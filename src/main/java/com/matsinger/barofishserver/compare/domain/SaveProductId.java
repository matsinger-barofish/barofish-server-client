package com.matsinger.barofishserver.compare.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveProductId  implements Serializable {
    private Integer userId;
    private Integer productId;
}
