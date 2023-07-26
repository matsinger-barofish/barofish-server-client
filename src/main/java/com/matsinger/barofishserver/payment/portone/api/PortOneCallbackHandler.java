package com.matsinger.barofishserver.payment.portone.api;

import com.matsinger.barofishserver.coupon.application.CouponCommandService;
import com.matsinger.barofishserver.notification.application.NotificationCommandService;
import com.matsinger.barofishserver.notification.dto.NotificationMessage;
import com.matsinger.barofishserver.notification.dto.NotificationMessageType;
import com.matsinger.barofishserver.order.orderprductinfo.domain.OrderCancelReason;
import com.matsinger.barofishserver.order.orderprductinfo.domain.OrderProductInfo;
import com.matsinger.barofishserver.order.orderprductinfo.domain.OrderProductState;
import com.matsinger.barofishserver.order.domain.OrderState;
import com.matsinger.barofishserver.order.application.OrderService;
import com.matsinger.barofishserver.order.domain.Orders;
import com.matsinger.barofishserver.payment.application.PaymentService;
import com.matsinger.barofishserver.payment.domain.PaymentState;
import com.matsinger.barofishserver.payment.domain.Payments;
import com.matsinger.barofishserver.payment.dto.GetVBankAccountReq;
import com.matsinger.barofishserver.payment.portone.application.PortOneCallbackService;
import com.matsinger.barofishserver.product.application.ProductService;
import com.matsinger.barofishserver.product.optionitem.domain.OptionItem;
import com.matsinger.barofishserver.user.application.UserCommandService;
import com.matsinger.barofishserver.userinfo.domain.UserInfo;
import com.matsinger.barofishserver.utils.Common;
import com.matsinger.barofishserver.utils.sms.SmsService;
import com.siot.IamportRestClient.exception.IamportResponseException;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/callback/iamport_pay_result")
public class PortOneCallbackHandler {

    private final Common utils;
    private final PaymentService paymentService;
    private final PortOneCallbackService callbackService;
    private final UserCommandService userService;
    private final OrderService orderService;
    private final ProductService productService;
    private final CouponCommandService couponCommandService;
    private final SmsService sms;
    private final NotificationCommandService notificationCommandService;


    @PostMapping("")
    public ResponseEntity<Object> portOneCallback(@RequestHeader(value = "x-real-ip", required = false) String XRealIp,
                                                  @RequestBody(required = false) PortOneCallbackService.PortOneBodyData data) {
        System.out.println("Portone callback received");
        if (!XRealIp.equals("52.78.100.19") && !XRealIp.equals("52.78.48.223"))
            return ResponseEntity.status(403).body(null);

        try {
            Orders order = orderService.selectOrder(data.getMerchant_uid());
            if (order != null) {
                if (data.getStatus().equals("ready")) {
                    Payments paymentData = paymentService.getPaymentInfo(order.getId(), data.getImp_uid());
                    GetVBankAccountReq
                            vBankReq =
                            GetVBankAccountReq.builder().orderId(order.getId()).price(order.getTotalPrice()).vBankCode(
                                    paymentData.getVbankCode()).vBankDue(Math.toIntExact((paymentData.getVbankDate().getTime() /
                                    1000))).vBankHolder(paymentData.getVbankHolder()).build();
                    paymentService.upsertPayments(paymentData);
                    order.setImpUid(data.getImp_uid());
                    orderService.updateOrder(order);
                    if (paymentData.getPayMethod().equals("vbank")) {
                        String
                                smsContent =
                                "[바로피쉬] 가상계좌 결제 요청\n" +
                                        String.format("주문번호: %s\n", data.getMerchant_uid()) +
                                        String.format("결제 금액: %d원\n", paymentData.getPaidAmount()) +
                                        String.format("가상계좌은행: %s\n", paymentData.getVbankName()) +
                                        String.format("가상계좌번호: %s\n",
                                                paymentData.getVbankNum().replaceAll("[^\\d]", ""));
                        sms.sendSms(paymentData.getBuyerTel(), smsContent, "가상 계좌 결제 요청");
                    }
                } else if (data.getStatus().equals("paid")) {
                    Payments paymentData = paymentService.getPaymentInfo(order.getId(), data.getImp_uid());
                    List<OrderProductInfo> infos = orderService.selectOrderProductInfoListWithOrderId(order.getId());
                    infos.forEach(info -> {
                        if (!orderService.checkProductCanDeliver(order.getDeliverPlace(), info)) {
                            int cancelPrice = 0;
                            try {
                                cancelPrice = orderService.getCancelPrice(order, List.of(info));
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                            System.out.println("callback cancel " + cancelPrice);
                            try {
                                paymentService.cancelPayment(data.getImp_uid(), cancelPrice);
                            } catch (IamportResponseException | IOException e) {
                                throw new RuntimeException(e);
                            }
                            info.setCancelReasonContent("배송 불가 지역");
                            info.setCancelReason(OrderCancelReason.ORDER_FAULT);
                            info.setState(OrderProductState.CANCELED);
                            notificationCommandService.sendFcmToUser(order.getUserId(),
                                    NotificationMessageType.ORDER_CANCEL,
                                    NotificationMessage.builder().productName(info.getProduct().getTitle()).build());
                        } else {
                            OptionItem optionItem = productService.selectOptionItem(info.getOptionItemId());
                            if (optionItem.getAmount() != null)
                                optionItem.setAmount(optionItem.getAmount() - info.getAmount());
                            productService.addOptionItem(optionItem);
                            info.setState(OrderProductState.PAYMENT_DONE);
                            notificationCommandService.sendFcmToUser(order.getUserId(),
                                    NotificationMessageType.PAYMENT_DONE,
                                    NotificationMessage.builder().productName(info.getProduct().getTitle()).build());
                        }
                    });
                    order.setState(OrderState.PAYMENT_DONE);
                    order.setImpUid(data.getImp_uid());
                    orderService.updateOrderProductInfo(infos);
                    orderService.updateOrder(order);
                    paymentService.upsertPayments(paymentData);
                    UserInfo userInfo = userService.selectUserInfo(order.getUserId());
                    userInfo.setPoint(userInfo.getPoint() - order.getUsePoint());
                    userService.updateUserInfo(userInfo);
                    couponCommandService.useCoupon(order.getCouponId(), order.getUserId());
                } else if (data.getStatus().equals("canceled")) {
                    Payments payment = paymentService.findPaymentByImpUid(data.getImp_uid());
                    payment.setStatus(PaymentState.CANCELED);
                    order.setState(OrderState.CANCELED);
                    orderService.updateOrder(order);
                    paymentService.upsertPayments(payment);
                }
            }
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        return ResponseEntity.ok(null);
    }
}
