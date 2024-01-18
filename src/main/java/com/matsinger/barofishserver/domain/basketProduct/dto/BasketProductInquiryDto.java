package com.matsinger.barofishserver.domain.basketProduct.dto;

import com.matsinger.barofishserver.domain.product.domain.ProductDeliverFeeType;
import lombok.Getter;

@Getter
public class BasketProductInquiryDto {

    private Integer productId;
    private String productName;

    private Integer optionItemId;
    private String optionItemName;
    private Boolean isNeeded;
    private Integer price;
    private Integer quantity;

    private ProductDeliverFeeType deliveryFeeType;
    private Integer deliveryFee;
    private int minOrderPrice;

    private Integer totalPrice;

    public void setDeliveryFee(Integer deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public void setTotalPrice(Integer totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void calculateDeliveryFee() {
        if (deliveryFeeType == ProductDeliverFeeType.FREE_IF_OVER) {
            if (price * quantity >= minOrderPrice) {
                deliveryFee = 0;
            }
        }
    }
}
