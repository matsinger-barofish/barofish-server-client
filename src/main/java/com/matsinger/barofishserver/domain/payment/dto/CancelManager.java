package com.matsinger.barofishserver.domain.payment.dto;

import com.matsinger.barofishserver.domain.order.domain.Orders;
import com.matsinger.barofishserver.domain.order.orderprductinfo.domain.OrderProductInfo;
import com.matsinger.barofishserver.domain.order.orderprductinfo.domain.OrderProductState;
import com.matsinger.barofishserver.global.exception.BusinessException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
public class CancelManager {

    private List<OrderProductInfo> tobeCanceled;
    private List<OrderProductInfo> notTobeCanceled;
    private Orders order;

    private int cancelProductPrice;
    private int cancelDeliveryFee;

    private int notTobeCanceledProductPrice;
    private int notTobeCanceledDeliveryFee;

    private int nonTaxablePriceTobeCanceled;
    private int taxablePriceTobeCanceled;

    private boolean allCanceled = false;

    public CancelManager(Orders order, List<OrderProductInfo> tobeCanceled, List<OrderProductInfo> notTobeCanceled) {
        this.tobeCanceled = tobeCanceled;
        this.notTobeCanceled = notTobeCanceled;
        this.order = order;

        cancelProductPrice = tobeCanceled.stream()
                .mapToInt(v -> v.getTotalProductPrice()).sum();
        cancelDeliveryFee = tobeCanceled.stream()
                .mapToInt(v -> v.getDeliveryFee()).sum();

        notTobeCanceledProductPrice = notTobeCanceled.stream()
                .mapToInt(v -> v.getTotalProductPrice()).sum();
        notTobeCanceledDeliveryFee = notTobeCanceled.stream()
                .mapToInt(v -> v.getDeliveryFee()).sum();

        if (notTobeCanceled.isEmpty()) {
            allCanceled = tobeCanceled.stream()
                    .noneMatch(v -> v.getState().equals(OrderProductState.CANCELED));
        }

        // 쿠폰, 포인트는 포트원에 보내는 주문 가격에 포함돼 있지 않기 때문에 비과세 가격에서 쿠폰, 포인트 가격을 빼줌
        if (allCanceled) {
            nonTaxablePriceTobeCanceled = tobeCanceled.stream()
                    .mapToInt(v -> v.getTaxFreeAmount()).sum() -
                    order.getCouponDiscount() - order.getUsedPoint();
        }
        if (!allCanceled) {
            nonTaxablePriceTobeCanceled = tobeCanceled.stream()
                    .mapToInt(v -> v.getTaxFreeAmount()).sum();
        }
        taxablePriceTobeCanceled = cancelProductPrice - nonTaxablePriceTobeCanceled + cancelDeliveryFee;
    }

    public int getProductAndDeliveryFee() {
        return notTobeCanceledProductPrice + notTobeCanceledDeliveryFee;
    }

    public int getOrderPriceAfterCancellation() {
        return notTobeCanceledProductPrice +
                notTobeCanceledDeliveryFee -
                order.getCouponDiscount() -
                order.getUsedPoint();
    }

    public boolean allCanceled() {
        if (notTobeCanceled.isEmpty()) {
            for (OrderProductInfo orderProductInfo : tobeCanceled) {
                log.info("allCanceled scope - orderProductState = {}", orderProductInfo.getState());


                if (!OrderProductState.isCanceled(orderProductInfo.getState())) {
                    return false;
                }
            }
        }
        if (!notTobeCanceled.isEmpty()) {
            return false;
        }
        return true;
    }

    public List<OrderProductInfo> getAllOrderProducts() {
        List<OrderProductInfo> allOrderProducts = new ArrayList<>();
        allOrderProducts.addAll(tobeCanceled);
        allOrderProducts.addAll(notTobeCanceled);
        return allOrderProducts;
    }

    public int getAllCancelPrice() {
        return nonTaxablePriceTobeCanceled +
                taxablePriceTobeCanceled -
                order.getCouponDiscount() -
                order.getUsedPoint();
    }

    public int getPartialCancelPrice() {
        return nonTaxablePriceTobeCanceled +
                taxablePriceTobeCanceled;
    }

    public void setCancelProductState(OrderProductState state) {
        validateCancelProductState();
        tobeCanceled.stream()
                .forEach(v -> v.setState(state));
    }

    public void validateCancelProductState() {
        for (OrderProductInfo cancelProduct : tobeCanceled) {
            OrderProductState state = cancelProduct.getState();
            if (state.equals(OrderProductState.CANCELED)) {
                throw new BusinessException("취소 완료된 상태입니다.");
            }
            if (state.equals(OrderProductState.DELIVERY_DONE) ||
                    state.equals(OrderProductState.EXCHANGE_REQUEST) ||
                    state.equals(OrderProductState.EXCHANGE_ACCEPT) ||
                    state.equals(OrderProductState.FINAL_CONFIRM) ||
                    state.equals(OrderProductState.REFUND_REQUEST) ||
                    state.equals(OrderProductState.REFUND_ACCEPT) ||
                    state.equals(OrderProductState.REFUND_DONE)) {
                throw new BusinessException("취소 불가능한 상태입니다.");
            }
            if (state.equals(OrderProductState.CANCEL_REQUEST)) {
                throw new BusinessException("이미 취소 요청된 상태입니다.");
            }
            if (state.equals(OrderProductState.DELIVERY_READY)) {
                throw new BusinessException("상품이 출고되어 취소가 불가능합니다.");
            }
        }
    }
}
