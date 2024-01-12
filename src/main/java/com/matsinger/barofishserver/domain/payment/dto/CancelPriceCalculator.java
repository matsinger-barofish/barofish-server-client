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

    private Boolean isTaxFree;
    private Integer existingDeliveryFee;
    private Integer productPriceToBeCanceled;
    // 위는 주입 받는 필드, 아래는 이후 주입 받거나 계산되는 필드
    private Integer newDeliveryFee = 0;

    private Integer taxablePrice;
    private Integer nonTaxablePrice;
    private Integer finalCancelPrice;

    public void calculate() {
        calculateTaxPrice();
        calculateTotalCancelPrice();
    }

    public void calculateTaxPrice() {
        // 상품이 비과세일 경우
        // 과세 금액 = 새로운 배송비
        // 비과세 금액 = 취소할 상품 가격 - 새로운 배송비
        if (isTaxFree) {
            taxablePrice = newDeliveryFee;
            nonTaxablePrice = productPriceToBeCanceled - newDeliveryFee;
        }
        // 상품이 과세일 경우
        // 과세 금액 = 취소할 상품 금액 + 새로운 배송비
        if (!isTaxFree) {
            taxablePrice = productPriceToBeCanceled + newDeliveryFee;
            nonTaxablePrice = 0;
        }
    }

    // 취소할 금액 = 취소할 상품 가격 + 기존 배송비 - 취소한 다음의 배송비
    public void calculateTotalCancelPrice() {
        this.finalCancelPrice = productPriceToBeCanceled + existingDeliveryFee - newDeliveryFee;
    }

    public void setNewDeliveryFee(int newDeliveryFee) {
        this.newDeliveryFee = newDeliveryFee;
    }
}
