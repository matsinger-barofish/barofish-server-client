package com.matsinger.barofishserver.domain.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 상품이 과세일 경우
// 과세 금액 = 취소할 금액
// 상품이 비과세일 경우
// 과세 금액 = (기존 배송비 - 취소한 다음의 배송비)
// 비과세 금액 = 취소할 상품 가격 - (기존 배송비 - 취소한 다음의 배송비)
@Getter
@Builder @AllArgsConstructor @NoArgsConstructor
public class CancelPriceCalculator {

    Boolean isTaxFree;
    Integer existingDeliveryFee;
    Integer productPriceToBeCanceled;
    // 위는 주입 받는 필드, 아래는 이후 주입 받거나 계산되는 필드
    Integer newDeliveryFee = 0;

    Integer taxablePrice;
    Integer nonTaxablePrice;
    Integer totalCancelPrice;

    public void calculate() {
        calculateTaxPrice();
        calculateTotalCancelPrice();
    }

    // 상품이 과세일 경우
    // 과세 금액 = 취소할 금액
    // 상품이 비과세일 경우
    // 과세 금액 = (기존 배송비 - 취소한 다음의 배송비)
    // 비과세 금액 = 취소할 상품 가격 - (기존 배송비 - 취소한 다음의 배송비)
    public void calculateTaxPrice() {
        if (isTaxFree) {
            taxablePrice = productPriceToBeCanceled;
            nonTaxablePrice = 0;
        }
        if (!isTaxFree) {
            taxablePrice = existingDeliveryFee - newDeliveryFee;
            nonTaxablePrice = productPriceToBeCanceled - (existingDeliveryFee - newDeliveryFee);
        }
    }

    // 취소할 금액 = 취소할 상품 가격 + 기존 배송비 - 취소한 다음의 배송비
    public void calculateTotalCancelPrice() {
        this.totalCancelPrice = productPriceToBeCanceled + existingDeliveryFee - newDeliveryFee;
    }

    public void setNewDeliveryFee(int newDeliveryFee) {
        this.newDeliveryFee = newDeliveryFee;
    }
}
