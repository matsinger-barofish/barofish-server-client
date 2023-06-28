package com.matsinger.barofishserver.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryFilterId implements Serializable {
    Integer compareFilterId;
    Integer categoryId;
}
