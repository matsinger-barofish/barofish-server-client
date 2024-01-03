package com.matsinger.barofishserver.domain.order.dto;

import com.matsinger.barofishserver.domain.order.orderprductinfo.domain.OrderProductInfo;
import com.matsinger.barofishserver.global.exception.BusinessException;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class OrderProductCancelCalculator {

    private Integer cFixMaxDeliveryFee = 0;
    private Integer cIfOverMaxDeliveryFee = 0;

    private Integer combinedProductsTotalPrice = 0;
    private Integer ifOverProductsTotalPrice = 0;

    List<OrderProductInfo> free = new ArrayList<>();

    List<OrderProductInfo> cFix = new ArrayList<>();
    List<OrderProductInfo> cIfOver = new ArrayList<>();

    List<OrderProductInfo> fix = new ArrayList<>();
    List<OrderProductInfo> ifOver = new ArrayList<>();

    public List<Integer> getCFixProductIds() {
        return cFix.stream().map(v -> v.getProductId()).toList();
    }
    public List<Integer> getCIfOverProductIds() {
        return cIfOver.stream().map(v -> v.getProductId()).toList();
    }

    public void setCIfOverMaxDeliveryFee(int deliveryFee) {
        this.cIfOverMaxDeliveryFee = deliveryFee;
        int topPrice = 0;
        int topPriceIdx = 0;
        for (int i=0; i<cIfOver.size(); i++) {
            OrderProductInfo orderProductInfo = cIfOver.get(i);
            if (orderProductInfo.getPrice() > topPrice) {
                topPriceIdx = i;
                topPrice = orderProductInfo.getPrice();
            }
            orderProductInfo.setDeliveryFee(0);
        }
        cIfOver.get(topPriceIdx).setDeliveryFee(deliveryFee);
    }

    public boolean checkCombinedFreeShippingCond(int minOrderPrice) {
        return combinedProductsTotalPrice >= minOrderPrice;
    }

    public void cancelFreeProduct(OrderProductInfo orderProductInfo) {
        for (OrderProductInfo freeDeliveryProduct : free) {
            if (freeDeliveryProduct.getId() == orderProductInfo.getId()) {
                free.remove(freeDeliveryProduct);
                this.combinedProductsTotalPrice -= freeDeliveryProduct.getPrice();
                return;
            }
        }
    }

    public boolean cancelCFixProduct(OrderProductInfo orderProductInfo) {
        for (OrderProductInfo fixDeliveryProduct : cFix) {
            if (fixDeliveryProduct.getId() == orderProductInfo.getId()) {
                cFix.remove(fixDeliveryProduct);
                this.combinedProductsTotalPrice -= fixDeliveryProduct.getPrice();

                if (cFix.size() == 0) {
                    cFixMaxDeliveryFee = 0;
                    return true;
                }
                return false;
            }
        }
        throw new BusinessException("주문 상품을 찾을 수 없습니다.");
    }

    public boolean cancelCIfOverProduct(OrderProductInfo orderProductInfo) {
        for (OrderProductInfo conditionalDeliveryProduct : cIfOver) {
            if (conditionalDeliveryProduct.getId() == orderProductInfo.getId()) {
                cIfOver.remove(conditionalDeliveryProduct);
                this.combinedProductsTotalPrice -= conditionalDeliveryProduct.getPrice();

                if (cIfOver.size() == 0) {
                    cIfOverMaxDeliveryFee = 0;
                    return true;
                }
                return false;
            }
        }
        throw new BusinessException("주문 상품을 찾을 수 없습니다.");
    }

    public void cancelFixProduct(OrderProductInfo orderProductInfo) {
        for (OrderProductInfo fixDeliveryProduct : fix) {
            if (fixDeliveryProduct.getId() == orderProductInfo.getId()) {
                fix.remove(fixDeliveryProduct);
            }
        }
        throw new BusinessException("주문 상품을 찾을 수 없습니다.");
    }

    public int cancelAllIfOverProduct() {
        int totalDeliveryFee = 0;
        for (OrderProductInfo orderProductInfo : ifOver) {
            totalDeliveryFee += orderProductInfo.getDeliveryFee();
        }
        return totalDeliveryFee;
    }

    public void divideIntoDeliveryType(List<OrderProductInfo> orderProductInfos) {
        int cFixMaxDeliveryFee = 0; int cIfOverMaxDeliveryFee = 0;
        int combinedProductsTotalPrice = 0; int ifOverProductsTotalPrice = 0;
        for (OrderProductInfo orderProductInfo : orderProductInfos) {

            if (orderProductInfo.isFree()) {
                combinedProductsTotalPrice += orderProductInfo.getPrice();
                free.add(orderProductInfo);
            }
            if (orderProductInfo.isCFix()) {
                cFixMaxDeliveryFee = orderProductInfo.compareWithDeliveryFee(cFixMaxDeliveryFee);
                combinedProductsTotalPrice += orderProductInfo.getPrice();
                cFix.add(orderProductInfo);
            }
            if (orderProductInfo.isCIfOver()) {
                cIfOverMaxDeliveryFee = orderProductInfo.compareWithDeliveryFee(cIfOverMaxDeliveryFee);
                combinedProductsTotalPrice += orderProductInfo.getPrice();
                cIfOver.add(orderProductInfo);
            }
            if (orderProductInfo.isFIX()) {
                fix.add(orderProductInfo);
            }
            if (orderProductInfo.isIfOver()) {
                ifOver.add(orderProductInfo);
                ifOverProductsTotalPrice += orderProductInfo.getPrice();
            }
            throw new BusinessException("주문 상품의 배송 타입을 알 수 없습니다.");
        }

        this.cFixMaxDeliveryFee = cFix.stream().mapToInt(v -> v.getDeliveryFee()).max().getAsInt();
        this.cIfOverMaxDeliveryFee = cIfOver.stream().mapToInt(v -> v.getDeliveryFee()).max().getAsInt();
        this.combinedProductsTotalPrice = combinedProductsTotalPrice;
        this.ifOverProductsTotalPrice = ifOverProductsTotalPrice;
    }
}
