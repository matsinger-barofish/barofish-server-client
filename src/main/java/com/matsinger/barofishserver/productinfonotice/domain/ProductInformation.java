package com.matsinger.barofishserver.productinfonotice.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.matsinger.barofishserver.product.domain.Product;
import com.matsinger.barofishserver.productinfonotice.dto.AgriculturalAndLivestockProductsInfoDto;
import com.matsinger.barofishserver.productinfonotice.dto.ProcessedFoodInfoDto;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "itemCode")
@JsonSubTypes({
        @JsonSubTypes.Type(value = AgriculturalAndLivestockProductsInfoDto.class, name = "19"),
        @JsonSubTypes.Type(value = ProcessedFoodInfoDto.class, name = "20")
})
public interface ProductInformation {

    public String getItemCode();

    public Integer getProductId();

    public Object toEntity(Product product);
}
