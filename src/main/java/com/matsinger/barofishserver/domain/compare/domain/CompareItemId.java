package com.matsinger.barofishserver.domain.compare.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompareItemId  implements Serializable {
    private Integer compareSetId;
    private Integer productId;
}
