package com.matsinger.barofishserver.domain.order.dto;

import com.matsinger.barofishserver.domain.order.orderprductinfo.domain.OrderProductInfo;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PriceCalculator {

    // totalProduct는 가장 가격이 높은 상품에 주문의 쿠폰, 적립금을 적용하기 위해서 사용
    private List<OrderProductInfo> totalProducts = new ArrayList<>();

    private int totalProductPrice = 0;
    private int totalDeliveryFee = 0;

    // conditionalShippingProducts는 스토어의 조건부 배송 가격을 산정할 때 사용
    private List<OrderProductInfo> conditionalShippingProducts = new ArrayList<>();
    // 아래 필드들은 스토어의 조건부 배송 가격을 산정할 때 사용
    int maxPriceProductIdx = 0; // (가격 * 수량)이 가장 높은 상품에 배송비를 적용
    int maxDeliveryFeeIdx = 0; // 가장 높은 배송비를 적용

    public void addConditionalProduct(OrderProductInfo productInfo) {
        conditionalShippingProducts.add(productInfo);
    }
    public void addToTotalProducts(OrderProductInfo productInfo) {
        totalProducts.add(productInfo);
    }

    public void addToTotalProductPrice(int price) {
        this.totalProductPrice += price;
    }

    public void addDeliveryFee(int deliveryFee) {
        this.totalDeliveryFee += deliveryFee;
    }

    public int getTotalOrderPrice() {
        return totalProductPrice + totalDeliveryFee;
    }

    /**
     *  1. 조건부 무료배송 상품 중에서 (가격 * 수량)이 제일 높은 상품을 뽑아냄
     *  2. 조건부 무료배송 상품들의 배송비 0 으로 만들고
     *  3. 조건부 무료배송 상품의 총 가격이 조건부 무료배송 가격보다 높거나 같은 경우 가격 제일 높은 상품 배송비 0
     *  4. 조건부 무료배송 상품의 총 가격이 조건부 무료배송 가격보다 낮은 경우 가격 제일 높은 상품 배송비 0
     */
    public void setConditionalShippingPrice(int conditionalShippingPrice) {

        int seq = 0;
        int maxPrice = 0;
        int maxDeliveryFee = 0;
        int totalPrice = 0;
        for (OrderProductInfo conditionalShippingProduct : conditionalShippingProducts) {

            int productPrice = conditionalShippingProduct.getPrice();
            // 조건부 배송 상품 중에서 가장 가격이 높은 상품의 인덱스를 maxPriceProductIdx에 추가
            maxPrice = setMaxPriceProductIdx(productPrice, maxPrice, seq);
            // 조건부 배송 상품 중에서 가장 배송비가 높은 상품의 인덱스를 maxDeliveryFeeIdx에 추가
            maxDeliveryFee = setMaxDeliverFeeProductIdx(conditionalShippingProduct, maxDeliveryFee, seq);

            totalPrice += productPrice;
            seq++;
        }

        OrderProductInfo maxPriceProduct = conditionalShippingProducts.get(maxPriceProductIdx);
        conditionalShippingProducts.remove(maxPriceProductIdx);
        setConditionalShippingDeliveryFee(conditionalShippingPrice, totalPrice, maxPriceProduct);
        setDeliveryFeeZeroWithoutTopPricedProduct();
    }

    private void setConditionalShippingDeliveryFee(int conditionalShippingPrice, int totalPrice, OrderProductInfo maxPriceProduct) {
        if (totalPrice >= conditionalShippingPrice) {
            maxPriceProduct.setDeliveryFee(0);
        }
        if (totalPrice < conditionalShippingPrice) {
            int maxDeliveryFee = conditionalShippingProducts.get(maxDeliveryFeeIdx).getDeliveryFee();
            maxPriceProduct.setDeliveryFee(maxDeliveryFee);
            totalDeliveryFee += maxDeliveryFee;
        }
    }

    private int setMaxDeliverFeeProductIdx(OrderProductInfo conditionalShippingProduct, int maxDeliveryFee, int seq) {
        if (conditionalShippingProduct.getDeliveryFee() > maxDeliveryFee) {
            maxDeliveryFee = conditionalShippingProduct.getDeliveryFee();
            maxDeliveryFeeIdx = seq;
        }
        return maxDeliveryFee;
    }

    private int setMaxPriceProductIdx(int productPrice, int maxPrice, int seq) {
        if (productPrice > maxPrice) {
            maxPrice = productPrice;
            maxPriceProductIdx = seq;
        }
        return maxPrice;
    }

    private void setDeliveryFeeZeroWithoutTopPricedProduct() {
        for (OrderProductInfo orderProductInfo : conditionalShippingProducts) {
            orderProductInfo.setDeliveryFee(0);
        }
    }

    public void tearDownConditionalProductInfo() {
        conditionalShippingProducts = new ArrayList<>();
        maxPriceProductIdx = 0;
        maxDeliveryFeeIdx = 0;
    }
}
