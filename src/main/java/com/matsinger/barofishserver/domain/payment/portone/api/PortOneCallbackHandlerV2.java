package com.matsinger.barofishserver.domain.payment.portone.api;

import com.matsinger.barofishserver.domain.basketProduct.application.BasketCommandService;
import com.matsinger.barofishserver.domain.coupon.application.CouponCommandService;
import com.matsinger.barofishserver.domain.notification.application.NotificationCommandService;
import com.matsinger.barofishserver.domain.notification.dto.NotificationMessage;
import com.matsinger.barofishserver.domain.notification.dto.NotificationMessageType;
import com.matsinger.barofishserver.domain.order.application.OrderCommandService;
import com.matsinger.barofishserver.domain.order.application.OrderQueryService;
import com.matsinger.barofishserver.domain.order.application.OrderService;
import com.matsinger.barofishserver.domain.order.domain.OrderState;
import com.matsinger.barofishserver.domain.order.domain.Orders;
import com.matsinger.barofishserver.domain.order.dto.OrderProductCancelCalculator;
import com.matsinger.barofishserver.domain.order.orderprductinfo.application.OrderProductInfoCommandService;
import com.matsinger.barofishserver.domain.order.orderprductinfo.application.OrderProductInfoQueryService;
import com.matsinger.barofishserver.domain.order.orderprductinfo.domain.OrderCancelReason;
import com.matsinger.barofishserver.domain.order.orderprductinfo.domain.OrderProductInfo;
import com.matsinger.barofishserver.domain.order.orderprductinfo.domain.OrderProductState;
import com.matsinger.barofishserver.domain.order.repository.OrderRepository;
import com.matsinger.barofishserver.domain.payment.application.PaymentCommandService;
import com.matsinger.barofishserver.domain.payment.application.PaymentService;
import com.matsinger.barofishserver.domain.payment.domain.PaymentState;
import com.matsinger.barofishserver.domain.payment.domain.Payments;
import com.matsinger.barofishserver.domain.payment.portone.application.PortOneCallbackService;
import com.matsinger.barofishserver.domain.payment.portone.dto.PortOneBodyData;
import com.matsinger.barofishserver.domain.payment.repository.PaymentRepository;
import com.matsinger.barofishserver.domain.product.application.ProductService;
import com.matsinger.barofishserver.domain.product.domain.Product;
import com.matsinger.barofishserver.domain.product.domain.ProductDeliverFeeType;
import com.matsinger.barofishserver.domain.product.optionitem.application.OptionItemCommandService;
import com.matsinger.barofishserver.domain.product.optionitem.application.OptionItemQueryService;
import com.matsinger.barofishserver.domain.product.optionitem.domain.OptionItem;
import com.matsinger.barofishserver.domain.product.repository.ProductRepository;
import com.matsinger.barofishserver.domain.store.application.StoreInfoQueryService;
import com.matsinger.barofishserver.domain.store.domain.StoreInfo;
import com.matsinger.barofishserver.domain.user.application.UserCommandService;
import com.matsinger.barofishserver.domain.userinfo.application.UserInfoQueryService;
import com.matsinger.barofishserver.domain.userinfo.domain.UserInfo;
import com.matsinger.barofishserver.global.exception.BusinessException;
import com.matsinger.barofishserver.utils.Common;
import com.matsinger.barofishserver.utils.sms.SmsService;
import com.siot.IamportRestClient.exception.IamportResponseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v2/callback/iamport_pay_result")
public class PortOneCallbackHandlerV2 {

    private final Common utils;
    private final PaymentService paymentService;
    private final PortOneCallbackService callbackService;
    private final UserCommandService userService;
    private final OrderService orderService;
    private final ProductService productService;
    private final CouponCommandService couponCommandService;
    private final SmsService sms;
    private final NotificationCommandService notificationCommandService;
    private final BasketCommandService basketCommandService;
    private final OrderQueryService orderQueryService;
    private final OrderRepository orderRepository;
    private final OrderProductInfoQueryService orderProductInfoQueryService;
    private final OrderCommandService orderCommandService;
    private final StoreInfoQueryService storeInfoQueryService;
    private final ProductRepository productRepository;
    private final PaymentCommandService paymentCommandService;
    private final OptionItemCommandService optionItemCommandService;
    private final UserInfoQueryService userInfoQueryService;
    private final UserCommandService userCommandService;
    private final OrderProductInfoCommandService orderProductInfoCommandService;
    private final PaymentRepository paymentRepository;
    private OptionItemQueryService optionItemQueryService;

    @PostMapping("")
    public ResponseEntity<Object> portOneCallback(@RequestHeader(value = "x-real-ip", required = false) String XRealIp,
                                                  @RequestBody(required = false) PortOneBodyData request) throws IamportResponseException, IOException {
        System.out.println("Portone callback received");
        if (!XRealIp.equals("52.78.100.19") && !XRealIp.equals("52.78.48.223")) {
            return ResponseEntity.status(403).body(null);
        }

        Orders order = orderQueryService.findById(request.getMerchant_uid());
        Payments payments = paymentService.getPaymentInfo(order.getId(), request.getImp_uid());
        if (request.getStatus().equals("ready")) {
            paymentService.save(payments);
            order.setImpUid(request.getImp_uid());
            orderRepository.save(order);

            if (payments.getPayMethod().equals("vbank")) {
                sendVbankDataWithSms(request, payments);
            }
        }

        if (request.getStatus().equals("paid")) {
            List<OrderProductInfo> orderProductInfos = orderProductInfoQueryService.findAllByOrderId(order.getId());
            Map<StoreInfo, List<OrderProductInfo>> storeProductMap = createStoreProductMap(orderProductInfos);

            OrderProductCancelCalculator calculator = new OrderProductCancelCalculator();
            for (StoreInfo storeInfo : storeProductMap.keySet()) {
                calculator.divideIntoDeliveryType(storeProductMap.get(storeInfo));
                Integer minOrderPrice = storeInfo.getMinOrderPrice();

                processOrderProductsPayment(
                        calculator,
                        ProductDeliverFeeType.FREE,
                        order,
                        minOrderPrice);
                processOrderProductsPayment(
                        calculator,
                        ProductDeliverFeeType.C_FIX,
                        order,
                        minOrderPrice);
                processOrderProductsPayment(
                        calculator,
                        ProductDeliverFeeType.C_FREE_IF_OVER,
                        order,
                        minOrderPrice);
            }
            boolean allCanceled = orderProductInfos.stream().allMatch(v -> v.getState().equals(OrderProductState.CANCELED));
            if (allCanceled) {
                order.setState(OrderState.PAYMENT_DONE);
                order.setImpUid(request.getImp_uid());
            }
            if (!allCanceled) {
                order.setState(OrderState.PAYMENT_DONE);
                order.setImpUid(request.getImp_uid());
                UserInfo userInfo = userInfoQueryService.findById(order.getUserId());
                userInfo.usePoint(order.getUsePoint());
                userCommandService.updateUserInfo(userInfo);
                couponCommandService.useCouponV1(order.getCouponId(), order.getUserId());
            }
            basketCommandService.deleteBasketAfterOrder(order, orderProductInfos);
            orderProductInfoCommandService.saveAll(orderProductInfos);
            orderRepository.save(order);
            paymentCommandService.save(payments);
        }
        if (request.getStatus().equals("canceled")) {
            Payments payment = paymentRepository.findFirstByImpUid(request.getImp_uid());
            payment.setStatus(PaymentState.CANCELED);
            order.setState(OrderState.CANCELED);
            orderRepository.save(order);
            paymentCommandService.save(payment);
        }
        return ResponseEntity.ok(null);
    }

    private void processOrderProductsPayment(OrderProductCancelCalculator calculator,
                                             ProductDeliverFeeType deliverFeeType,
                                             Orders order,
                                             Integer minOrderPrice) throws IamportResponseException, IOException {
        if (deliverFeeType.equals(ProductDeliverFeeType.FREE)) {
            for (OrderProductInfo freeDeliveryProduct : calculator.getFree()) {
                boolean canDeliver = orderService.checkProductCanDeliver(order.getDeliverPlace(), freeDeliveryProduct);
                if (!canDeliver) {
                    cancelProductIfUnableToDeliver(
                            order,
                            freeDeliveryProduct,
                            minOrderPrice,
                            ProductDeliverFeeType.FREE,
                            calculator);
                }
                if (canDeliver) {
                    reduceQuantity(freeDeliveryProduct);
                    freeDeliveryProduct.setState(OrderProductState.PAYMENT_DONE);
                    sendNotification(freeDeliveryProduct, order.getUserId());
                }
            }
        }
        if (deliverFeeType.equals(ProductDeliverFeeType.C_FIX)) {
            for (OrderProductInfo cFixDeliveryProduct : calculator.getCFix()) {
                boolean canDeliver = orderService.checkProductCanDeliver(order.getDeliverPlace(), cFixDeliveryProduct);
                if (!canDeliver) {
                    cancelProductIfUnableToDeliver(
                            order,
                            cFixDeliveryProduct,
                            minOrderPrice,
                            ProductDeliverFeeType.C_FIX,
                            calculator);
                }
                if (canDeliver) {
                    reduceQuantity(cFixDeliveryProduct);
                    cFixDeliveryProduct.setState(OrderProductState.PAYMENT_DONE);
                    sendNotification(cFixDeliveryProduct, order.getUserId());
                }
            }
        }
        if (deliverFeeType.equals(ProductDeliverFeeType.C_FREE_IF_OVER)) {
            for (OrderProductInfo cIfOverDeliveryProduct : calculator.getCIfOver()) {
                boolean canDeliver = orderService.checkProductCanDeliver(order.getDeliverPlace(), cIfOverDeliveryProduct);
                if (!canDeliver) {
                    cancelProductIfUnableToDeliver(
                            order,
                            cIfOverDeliveryProduct,
                            minOrderPrice,
                            ProductDeliverFeeType.C_FREE_IF_OVER,
                            calculator);
                }
                if (canDeliver) {
                    reduceQuantity(cIfOverDeliveryProduct);
                    cIfOverDeliveryProduct.setState(OrderProductState.PAYMENT_DONE);
                    sendNotification(cIfOverDeliveryProduct, order.getUserId());
                }
            }
        }
        if (deliverFeeType.equals(ProductDeliverFeeType.FIX)) {
            for (OrderProductInfo fixDeliveryProduct : calculator.getFix()) {
                boolean canDeliver = orderService.checkProductCanDeliver(order.getDeliverPlace(), fixDeliveryProduct);
                if (!canDeliver) {
                    cancelProductIfUnableToDeliver(
                            order,
                            fixDeliveryProduct,
                            minOrderPrice,
                            ProductDeliverFeeType.FIX,
                            calculator);
                }
                if (canDeliver) {
                    reduceQuantity(fixDeliveryProduct);
                    fixDeliveryProduct.setState(OrderProductState.PAYMENT_DONE);
                    sendNotification(fixDeliveryProduct, order.getUserId());
                }
            }
        }
        if (deliverFeeType.equals(ProductDeliverFeeType.FREE_IF_OVER)) {
            for (OrderProductInfo ifOverDeliveryProduct : calculator.getIfOver()) {
                boolean canDeliver = orderService.checkProductCanDeliver(order.getDeliverPlace(), ifOverDeliveryProduct);
                if (!canDeliver) {
                    cancelProductIfUnableToDeliver(
                            order,
                            ifOverDeliveryProduct,
                            minOrderPrice,
                            ProductDeliverFeeType.FREE_IF_OVER,
                            calculator);
                    return; // 합배송이 안되는 조건부 무료배송 상품의 경우 전체 취소만 가능
                }
            }
        }
    }

    private void sendNotification(OrderProductInfo freeDeliveryProduct, Integer userId) {
        notificationCommandService.sendFcmToUser(
                userId,
                NotificationMessageType.PAYMENT_DONE,
                NotificationMessage.builder()
                        .productName(freeDeliveryProduct.getProduct().getTitle())
                        .build());
    }

    private void reduceQuantity(OrderProductInfo freeDeliveryProduct) {
        OptionItem optionItem = optionItemQueryService.findById(freeDeliveryProduct.getOptionItemId());
        optionItem.reduceQuantity(freeDeliveryProduct.getAmount());
        optionItemCommandService.save(optionItem);
    }

    private void cancelProductIfUnableToDeliver(Orders order,
                                                OrderProductInfo orderProductInfo,
                                                Integer minOrderPrice,
                                                ProductDeliverFeeType deliveryFeeType,
                                                OrderProductCancelCalculator calculator) throws IamportResponseException, IOException {
        int additionalDeliveryFee = cancelProductAndGetAdditionalDeliveryFee(
                minOrderPrice,
                deliveryFeeType,
                orderProductInfo,
                calculator);

        if (deliveryFeeType.equals(ProductDeliverFeeType.FREE_IF_OVER)) {
            int totalDeliveryFee = calculator.cancelAllIfOverProduct(); // 전체 취소

            paymentCommandService.cancelPayment(
                    order,
                    calculator.getIfOver(),
                    totalDeliveryFee);
            setCancelInfoUnableToDeliver(calculator.getIfOver(), order);
            return;
        }
        paymentCommandService.cancelPayment(
                order,
                List.of(orderProductInfo),
                additionalDeliveryFee);
        setCancelInfoUnableToDeliver(List.of(orderProductInfo), order);
    }

    private void setCancelInfoUnableToDeliver(List<OrderProductInfo> orderProductInfos, Orders order) {
        for (OrderProductInfo orderProductInfo : orderProductInfos) {
            orderProductInfo.setCancelReasonContent("배송 불가 지역");
            orderProductInfo.setCancelReason(OrderCancelReason.ORDER_FAULT);
            orderProductInfo.setState(OrderProductState.CANCELED);
            notificationCommandService.sendFcmToUser(
                    order.getUserId(),
                    NotificationMessageType.ORDER_CANCEL,
                    NotificationMessage.builder()
                            .productName(orderProductInfo.getProduct().getTitle())
                            .isCanceledByRegion(true)
                            .build());
        }
    }

    private int cancelProductAndGetAdditionalDeliveryFee(
            int minOrderPrice,
            ProductDeliverFeeType deliverFeeType,
            OrderProductInfo orderProductInfo,
            OrderProductCancelCalculator calculator) {

        boolean canCombinedFreeShipping = calculator.checkCombinedFreeShippingCond(minOrderPrice);

        if (deliverFeeType.equals(ProductDeliverFeeType.FREE)) {
            calculator.cancelFreeProduct(orderProductInfo);
            if (!canCombinedFreeShipping) {
                int cIfOverMaxDeliveryFee = productRepository.findAllById(calculator.getCIfOverProductIds())
                        .stream().mapToInt(v -> v.getDeliverFee()).max().getAsInt();
                calculator.setCIfOverMaxDeliveryFee(cIfOverMaxDeliveryFee);
                return cIfOverMaxDeliveryFee;
            }
            return 0;
        }
        if (deliverFeeType.equals(ProductDeliverFeeType.C_FIX)) {
            calculator.cancelCFixProduct(orderProductInfo);
            if (!canCombinedFreeShipping) {
                int cFixMaxDeliveryFee = productRepository.findAllById(calculator.getCFixProductIds())
                        .stream().mapToInt(v -> v.getDeliverFee()).max().getAsInt();
                calculator.setCIfOverMaxDeliveryFee(cFixMaxDeliveryFee);
                return cFixMaxDeliveryFee;
            }
            return 0;
        }
        if (deliverFeeType.equals(ProductDeliverFeeType.C_FREE_IF_OVER)) {
            calculator.cancelCIfOverProduct(orderProductInfo);
            if (!canCombinedFreeShipping) {
                int cIfOverMaxDeliveryFee = productRepository.findAllById(calculator.getCIfOverProductIds())
                        .stream().mapToInt(v -> v.getDeliverFee()).max().getAsInt();
                calculator.setCIfOverMaxDeliveryFee(cIfOverMaxDeliveryFee);
                return cIfOverMaxDeliveryFee;
            }
            return 0;
        }
        if (deliverFeeType.equals(ProductDeliverFeeType.FIX)) {
            calculator.cancelFixProduct(orderProductInfo);
            Product product = orderProductInfo.getProduct();
            return product.getDeliverFee();
        }

        throw new BusinessException("주문 상품 정보를 찾을 수 없습니다.");
    }

    private void sendVbankDataWithSms(PortOneBodyData request, Payments payments) {
        String smsContent =
                "[바로피쉬] 가상계좌 결제 요청\n" +
                        String.format("주문번호: %s\n", request.getMerchant_uid()) +
                        String.format("결제 금액: %d원\n", payments.getPaidAmount()) +
                        String.format("가상계좌은행: %s\n", payments.getVbankName()) +
                        String.format("가상계좌번호: %s\n",
                                payments.getVbankNum().replaceAll("[^\\d]", "") + "\n") +
                        String.format("가상계좌 예금주명: %s\n", payments.getVbankHolder()) +
                        "24시간 이내로 이체해주세요.";
        sms.sendSms(payments.getBuyerTel(), smsContent, "가상 계좌 결제 요청");
    }

    private Map<StoreInfo, List<OrderProductInfo>> createStoreProductMap(List<OrderProductInfo> orderProductInfos) {
        Map<StoreInfo, List<OrderProductInfo>> storeProductMap = new HashMap<>();
        for (OrderProductInfo orderProductInfo : orderProductInfos) {
            Product product = orderProductInfo.getProduct();
            StoreInfo storeInfo = storeInfoQueryService.findById(product.getStoreId());

            List<OrderProductInfo> existingArrays = storeProductMap.getOrDefault(storeInfo, new ArrayList<>());
            existingArrays.add(orderProductInfo);
            storeProductMap.put(storeInfo, existingArrays);
        }
        return storeProductMap;
    }
}
