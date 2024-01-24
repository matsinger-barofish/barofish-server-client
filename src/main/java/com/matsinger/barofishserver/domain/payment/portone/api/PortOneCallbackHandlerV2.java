package com.matsinger.barofishserver.domain.payment.portone.api;

import com.matsinger.barofishserver.domain.coupon.application.CouponCommandService;
import com.matsinger.barofishserver.domain.order.application.OrderCommandService;
import com.matsinger.barofishserver.domain.order.application.OrderQueryService;
import com.matsinger.barofishserver.domain.order.domain.OrderPaymentWay;
import com.matsinger.barofishserver.domain.order.domain.OrderState;
import com.matsinger.barofishserver.domain.order.domain.Orders;
import com.matsinger.barofishserver.domain.order.orderprductinfo.domain.OrderProductInfo;
import com.matsinger.barofishserver.domain.order.orderprductinfo.domain.OrderProductState;
import com.matsinger.barofishserver.domain.order.orderprductinfo.repository.OrderProductInfoRepository;
import com.matsinger.barofishserver.domain.order.repository.OrderRepository;
import com.matsinger.barofishserver.domain.payment.application.PaymentService;
import com.matsinger.barofishserver.domain.payment.domain.PaymentState;
import com.matsinger.barofishserver.domain.payment.domain.Payments;
import com.matsinger.barofishserver.domain.payment.portone.application.PortOneCommandService;
import com.matsinger.barofishserver.domain.payment.portone.dto.PortOneBodyData;
import com.matsinger.barofishserver.domain.payment.repository.PaymentRepository;
import com.matsinger.barofishserver.domain.userinfo.application.UserInfoQueryService;
import com.matsinger.barofishserver.domain.userinfo.domain.UserInfo;
import com.matsinger.barofishserver.domain.userinfo.repository.UserInfoRepository;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/callback/iamport_pay_result")
public class PortOneCallbackHandlerV2 {

    private final PortOneCommandService portOneCommandService;
    private final OrderQueryService orderQueryService;
    private final PaymentService paymentService;
    private final UserInfoQueryService userInfoQueryService;
    private final CouponCommandService couponCommandService;
    private final OrderCommandService orderCommandService;
    private final OrderRepository orderRepository;
    private final OrderProductInfoRepository orderProductInfoRepository;
    private final PaymentRepository paymentRepository;
    private final UserInfoRepository userInfoRepository;

    @PostMapping("")
    public ResponseEntity<Object> portOneCallback(@RequestHeader(value = "x-real-ip", required = false) String XRealIp,
                                                  @RequestBody(required = false) PortOneBodyData request) throws IamportResponseException, IOException {
        System.out.println("Portone callback received");
        if (!XRealIp.equals("52.78.100.19") && !XRealIp.equals("52.78.48.223")) {
            return ResponseEntity.status(403).body(null);
        }

        Orders order = orderQueryService.findById(request.getMerchant_uid());

        if (request.getStatus().equals("ready")) {
            portOneCommandService.processWhenStatusReady(request);
        }

        try {
            if (request.getStatus().equals("paid")) {
                portOneCommandService.checkCanDeliverAndProcessOrder(request);
            }
        } catch (Exception e) {
            order.setState(OrderState.CANCELED);
            Payments payment = paymentService.getPaymentInfoFromPortOne(order.getId(), request.getImp_uid());
            payment.setStatus(PaymentState.FAILED);

            List<OrderProductInfo> orderProductInfos = order.getProductInfos();
            orderProductInfos.stream()
                    .forEach(v -> v.setState(OrderProductState.CANCELED));

            UserInfo userInfo = userInfoQueryService.findByUserId(order.getUserId());
            userInfo.addPoint(order.getUsedPoint());
            couponCommandService.unUseCoupon(order.getCouponId(), userInfo.getUserId());

            CancelData cancelData = new CancelData(
                    order.getImpUid(),
                    true,
                    BigDecimal.valueOf(order.getTotalPrice())
            );
            if (order.getPaymentWay().equals(OrderPaymentWay.VIRTUAL_ACCOUNT)) {
                cancelData.setRefund_holder(order.getBankHolder());
                cancelData.setRefund_bank(order.getBankCode());
                cancelData.setRefund_account(order.getBankAccount());
            }
            int taxFreePrice = orderProductInfos.stream()
                    .mapToInt(v -> v.getTaxFreeAmount())
                    .sum();
            cancelData.setTax_free(BigDecimal.valueOf(taxFreePrice));
            portOneCommandService.sendPortOneCancelData(cancelData);

            orderRepository.save(order);
            orderProductInfoRepository.saveAll(orderProductInfos);
            paymentRepository.save(payment);
            userInfoRepository.save(userInfo);
        }

        if (request.getStatus().equals("canceled")) {
            portOneCommandService.processWhenStatusCanceled(request);
        }
        return ResponseEntity.ok(null);
    }
}
