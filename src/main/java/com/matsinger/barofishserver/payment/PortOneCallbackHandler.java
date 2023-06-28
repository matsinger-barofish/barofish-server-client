package com.matsinger.barofishserver.payment;

import com.matsinger.barofishserver.order.object.OrderProductInfo;
import com.matsinger.barofishserver.order.object.OrderProductState;
import com.matsinger.barofishserver.order.object.OrderState;
import com.matsinger.barofishserver.order.OrderService;
import com.matsinger.barofishserver.order.object.Orders;
import com.matsinger.barofishserver.user.UserService;
import com.matsinger.barofishserver.user.object.UserInfo;
import com.matsinger.barofishserver.utils.Common;
import jakarta.servlet.http.HttpServletRequest;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
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


    @PostMapping("")
    public ResponseEntity<Object> portOneCallback(@RequestHeader(value = "x-real-ip", required = false) String XRealIp,
                                                  @RequestBody(required = false) PortOneCallbackService.PortOneBodyData data) {
        System.out.println(XRealIp + " callback received");
        //TODO: nginx 도메인 연결 시
        if (!XRealIp.equals("52.78.100.19") && !XRealIp.equals("52.78.48.223"))
            return ResponseEntity.status(403).body(null);
        System.out.println("Callback has received2\n" +
                data.getMerchant_uid() +
                "\n" +
                utils.now() +
                "\n" +
                data.getStatus());

//        utils.fetch(findByMidUrl, "POST", "application/json");
//        Payments payment = paymentService.selectPayment(data.getMerchant_uid());
        try {
            Orders order = orderService.selectOrder(data.getMerchant_uid());
            if (order != null) {
                if (data.getStatus().equals("paid")) {
                    Payments paymentData = paymentService.getPaymentInfo(order.getId(), data.getImp_uid());
                    List<OrderProductInfo> infos = orderService.selectOrderProductInfoListWithOrderId(order.getId());
                    infos.forEach(orderProductInfo -> {
                        orderProductInfo.setState(OrderProductState.PAYMENT_DONE);
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
