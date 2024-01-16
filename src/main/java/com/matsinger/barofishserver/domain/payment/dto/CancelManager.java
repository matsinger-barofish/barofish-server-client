package com.matsinger.barofishserver.domain.payment.dto;

import com.matsinger.barofishserver.domain.order.domain.Orders;
import com.matsinger.barofishserver.domain.order.orderprductinfo.domain.OrderProductInfo;
import com.matsinger.barofishserver.domain.order.orderprductinfo.domain.OrderProductState;
import com.matsinger.barofishserver.global.exception.BusinessException;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

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

        nonTaxablePriceTobeCanceled = tobeCanceled.stream()
                .mapToInt(v -> v.getTaxFreeAmount()).sum();
        taxablePriceTobeCanceled = cancelProductPrice - nonTaxablePriceTobeCanceled + cancelDeliveryFee;
    }

    public int getProductAndDeliveryFee() {
        return notTobeCanceledProductPrice + notTobeCanceledDeliveryFee;
    }

    public int getOrderPriceAfterCancellation() {
        return notTobeCanceledProductPrice +
                notTobeCanceledDeliveryFee +
                order.getCouponDiscount() +
                order.getUsedPoint();
    }

    public boolean allCanceled() {
        if (notTobeCanceled.isEmpty()) {
            return tobeCanceled.stream()
                    .noneMatch(v -> v.getState().equals(OrderProductState.CANCELED));
        }
        return false;
    }

    public void validateStateAndSetCanceled() {
        boolean cancellableState = false;
        for (OrderProductInfo cancelProduct : tobeCanceled) {
            OrderProductState state = cancelProduct.getState();
            if (state.equals(OrderProductState.WAIT_DEPOSIT)) {
                cancelProduct.setState(OrderProductState.CANCELED);
                cancellableState = true;
            }
            if (state.equals(OrderProductState.PAYMENT_DONE)) {
                cancelProduct.setState(OrderProductState.CANCELED);
                cancellableState = true;
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
            if (state.equals(OrderProductState.CANCELED)) {
                throw new BusinessException("취소 완료된 상태입니다.");
            }
            if (state.equals(OrderProductState.DELIVERY_READY)) {
                throw new BusinessException("상품이 출고되어 취소가 불가능합니다.");
            }
            if (!cancellableState) {
                throw new RuntimeException("주문 상태를 확인해주세요.");
            }
            cancelProduct.setState(OrderProductState.CANCELED);
        }
    }

    public List<OrderProductInfo> getAllOrderProducts() {
        List<OrderProductInfo> allOrderProducts = new ArrayList<>();
        allOrderProducts.addAll(tobeCanceled);
        allOrderProducts.addAll(notTobeCanceled);
        return allOrderProducts;
    }
}