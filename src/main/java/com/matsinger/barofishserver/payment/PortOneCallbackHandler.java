package com.matsinger.barofishserver.payment;

import com.matsinger.barofishserver.order.object.OrderProductInfo;
import com.matsinger.barofishserver.order.object.OrderProductState;
import com.matsinger.barofishserver.order.object.OrderState;
import com.matsinger.barofishserver.order.OrderService;
import com.matsinger.barofishserver.order.object.Orders;
import com.matsinger.barofishserver.product.ProductService;
import com.matsinger.barofishserver.product.object.OptionItem;
import com.matsinger.barofishserver.user.UserService;
import com.matsinger.barofishserver.user.object.UserInfo;
import com.matsinger.barofishserver.utils.Common;
import com.matsinger.barofishserver.utils.sms.SmsService;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/callback/iamport_pay_result")
public class PortOneCallbackHandler {

    private final Common utils;
    private final PaymentService paymentService;
    private final PortOneCallbackService callbackService;
    private final UserService userService;
    private final OrderService orderService;
    private final ProductService productService;
    private final SmsService sms;


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
                    PaymentService.GetVBankAccountReq
                            vBankReq =
                            PaymentService.GetVBankAccountReq.builder().orderId(order.getId()).price(order.getTotalPrice()).vBankCode(
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
                        OptionItem optionItem = productService.selectOptionItem(info.getOptionItemId());
                        optionItem.setAmount(optionItem.getAmount() - info.getAmount());
                        productService.addOptionItem(optionItem);
                        info.setState(OrderProductState.PAYMENT_DONE);
                    });
                    order.setState(OrderState.PAYMENT_DONE);
                    order.setImpUid(data.getImp_uid());
                    orderService.updateOrderProductInfo(infos);
                    orderService.updateOrder(order);
                    paymentService.upsertPayments(paymentData);
                    UserInfo userInfo = userService.selectUserInfo(order.getUserId());
                    userService.addPoint(userInfo, order.getTotalPrice() * userInfo.getGrade().getPointRate() / 100);
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
