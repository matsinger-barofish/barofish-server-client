package com.matsinger.barofishserver.domain.payment.portone.application;

import com.matsinger.barofishserver.domain.basketProduct.application.BasketCommandService;
import com.matsinger.barofishserver.domain.coupon.application.CouponCommandService;
import com.matsinger.barofishserver.domain.notification.application.NotificationCommandService;
import com.matsinger.barofishserver.domain.notification.dto.NotificationMessage;
import com.matsinger.barofishserver.domain.notification.dto.NotificationMessageType;
import com.matsinger.barofishserver.domain.order.application.OrderQueryService;
import com.matsinger.barofishserver.domain.order.application.OrderService;
import com.matsinger.barofishserver.domain.order.domain.OrderPaymentWay;
import com.matsinger.barofishserver.domain.order.domain.OrderState;
import com.matsinger.barofishserver.domain.order.domain.Orders;
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
import com.matsinger.barofishserver.domain.payment.portone.dto.PortOneBodyData;
import com.matsinger.barofishserver.domain.payment.repository.PaymentRepository;
import com.matsinger.barofishserver.domain.product.application.ProductQueryService;
import com.matsinger.barofishserver.domain.product.domain.Product;
import com.matsinger.barofishserver.domain.product.optionitem.application.OptionItemCommandService;
import com.matsinger.barofishserver.domain.product.optionitem.application.OptionItemQueryService;
import com.matsinger.barofishserver.domain.product.optionitem.domain.OptionItem;
import com.matsinger.barofishserver.domain.user.application.UserCommandService;
import com.matsinger.barofishserver.domain.userinfo.application.UserInfoQueryService;
import com.matsinger.barofishserver.domain.userinfo.domain.UserInfo;
import com.matsinger.barofishserver.global.exception.BusinessException;
import com.matsinger.barofishserver.utils.sms.SmsService;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PortOneCommandService {

    private final PaymentService paymentService;
    private final OrderRepository orderRepository;
    private final SmsService sms;
    private final OrderService orderService;
    private final ProductQueryService productQueryService;
    private final NotificationCommandService notificationCommandService;
    private final OptionItemQueryService optionItemQueryService;
    private final OptionItemCommandService optionItemCommandService;
    private final PortOneCallbackService portOneCallbackService;
    private final UserInfoQueryService userInfoQueryService;
    private final UserCommandService userCommandService;
    private final CouponCommandService couponCommandService;
    private final BasketCommandService basketCommandService;
    private final OrderProductInfoCommandService orderProductInfoCommandService;
    private final PaymentCommandService paymentCommandService;
    private final OrderQueryService orderQueryService;
    private final PaymentRepository paymentRepository;
    private final OrderProductInfoQueryService orderProductInfoQueryService;

    @Transactional
    public void processWhenStatusReady(PortOneBodyData request) {
        Orders order = orderQueryService.findById(request.getMerchant_uid());
        Payments payments = paymentService.getPaymentInfoFromPortOne(order.getId(), request.getImp_uid());

        paymentService.save(payments);
        order.setImpUid(request.getImp_uid());
        orderRepository.save(order);

        if (payments.getPayMethod().equals("vbank")) {
            sendVbankDataWithSms(request, payments);
        }
    }

    @Transactional
    public void checkCanDeliverAndProcessOrder(PortOneBodyData request) {
        Orders order = orderQueryService.findById(request.getMerchant_uid());
        Payments payments = paymentService.getPaymentInfoFromPortOne(order.getId(), request.getImp_uid());
        List<OrderProductInfo> orderProductInfos = orderProductInfoQueryService.findAllByOrderId(order.getId());

        for (OrderProductInfo orderProductInfo : orderProductInfos) {
            boolean canDeliver = orderService.checkProductCanDeliver(order.getDeliverPlace(), orderProductInfo);
            if (!canDeliver) {
                orderProductInfos.forEach( opi -> {
                    opi.setCancelReasonContent("배송 불가 지역");
                    opi.setState(OrderProductState.DELIVERY_DIFFICULT);
                    opi.setCancelReason(OrderCancelReason.ORDER_FAULT);
                });
                order.setState(OrderState.DELIVERY_DIFFICULT);
                order.setImpUid(request.getImp_uid());

                sendNotification(order, orderProductInfo, true);

                CancelData cancelData = createCancelData(orderProductInfos, order);
                requestRefund(cancelData);
            }
            if (canDeliver) {
                orderProductInfo.setState(OrderProductState.PAYMENT_DONE);
                order.setState(OrderState.PAYMENT_DONE);
                order.setImpUid(request.getImp_uid());

                UserInfo userInfo = userInfoQueryService.findByUserId(order.getUserId());
                sendNotification(order, orderProductInfo, false);
                reduceQuantity(orderProductInfo);
                userInfo.usePoint(order.getUsePoint());
                couponCommandService.useCouponV1(order.getCouponId(), order.getUserId());
                basketCommandService.deleteBasketAfterOrder(order, orderProductInfos);

                userCommandService.updateUserInfo(userInfo);
                orderProductInfoCommandService.saveAll(orderProductInfos);
                orderRepository.save(order);
                paymentCommandService.save(payments);
            }
        }
    }

    private void requestRefund(CancelData cancelData) {
        try {
            IamportClient iamportClient = portOneCallbackService.getIamportClient();
            IamportResponse<Payment> cancelResult = iamportClient.cancelPaymentByImpUid(cancelData);
            
            if (cancelResult.getCode() != 0) {
                System.out.println(cancelResult.getMessage());
                throw new BusinessException("환불에 실패하였습니다.");
            }
        } catch (Exception e) {
            throw new BusinessException("환불에 실패하였습니다.");
            }
    }

    @NotNull
    private static CancelData createCancelData(List<OrderProductInfo> orderProductInfos, Orders order) {
        int deliveryFeeSum = orderProductInfos.stream().mapToInt(v -> v.getDeliveryFee()).sum();
        int orderProductsAndDeliveryFeeSum = order.getOriginTotalPrice() + deliveryFeeSum;
        int taxFreeAmount = orderProductInfos.stream().mapToInt(v -> v.getTaxFreeAmount()).sum();

        CancelData cancelData = new CancelData(
                order.getImpUid(),
                true,
                BigDecimal.valueOf(orderProductsAndDeliveryFeeSum));
        cancelData.setTax_free(BigDecimal.valueOf(taxFreeAmount));

        if (order.getPaymentWay().equals(OrderPaymentWay.VIRTUAL_ACCOUNT)) {
            cancelData.setRefund_holder(order.getBankHolder());
            cancelData.setRefund_bank(order.getBankCode());
            cancelData.setRefund_account(order.getBankAccount());
        }
        return cancelData;
    }

    private void sendNotification(Orders order, OrderProductInfo orderProductInfo, boolean isDeliveryDifficult) {
        Product product = productQueryService.findById(orderProductInfo.getProductId());

        if (isDeliveryDifficult) {
            notificationCommandService.sendFcmToUser(order.getUserId(),
                    NotificationMessageType.ORDER_CANCEL,
                    NotificationMessage.builder()
                            .productName(product.getTitle())
                            .isCanceledByRegion(true)
                            .build());
            return;
        }

        notificationCommandService.sendFcmToUser(order.getUserId(),
                NotificationMessageType.PAYMENT_DONE,
                NotificationMessage.builder()
                        .productName(product.getTitle())
                        .isCanceledByRegion(false)
                        .build()
        );
    }

    private void reduceQuantity(OrderProductInfo freeDeliveryProduct) {
        OptionItem optionItem = optionItemQueryService.findById(freeDeliveryProduct.getOptionItemId());
        optionItem.reduceQuantity(freeDeliveryProduct.getAmount());
        optionItemCommandService.save(optionItem);
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

    public void processWhenStatusCanceled(PortOneBodyData request) {
        Orders order = orderQueryService.findById(request.getMerchant_uid());
        Payments payment = paymentRepository.findFirstByImpUid(request.getImp_uid());

        payment.setStatus(PaymentState.CANCELED);
        order.setState(OrderState.CANCELED);
        orderRepository.save(order);
        paymentCommandService.save(payment);
    }
}