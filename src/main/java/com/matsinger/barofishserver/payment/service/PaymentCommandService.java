package com.matsinger.barofishserver.payment.service;

import com.matsinger.barofishserver.order.Order;
import com.matsinger.barofishserver.order.OrderState;
import com.matsinger.barofishserver.order.exception.OrderBusinessException;
import com.matsinger.barofishserver.order.repository.OrderRepository;
import com.matsinger.barofishserver.payment.Payment;
import com.matsinger.barofishserver.payment.dto.request.PortOnePaymentRequestDto;
import com.matsinger.barofishserver.payment.exception.PaymentErrorMessage;
import com.matsinger.barofishserver.payment.repository.PaymentRepository;
import com.matsinger.barofishserver.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentCommandService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    private final ProductRepository productRepository;
    public String proceedPayment(PortOnePaymentRequestDto request) {

        Order findOrder = orderRepository.findById(request.getMerchant_uid())
                .orElseThrow(() -> {
                    throw new IllegalArgumentException(PaymentErrorMessage.ORDER_NOT_FOUND_MESSAGE);
                });

        if (request.getPay_method() != "trans" && request.getPay_method() != "vbank") {
            // 실시간 계좌이체, 가상계좌가 아닐 때 로직 처리
            findOrder.setState(OrderState.PAYMENT_DONE);
        }
        // 실시간 계좌이체, 가상계좌일 때 로직 처리
        findOrder.setState(OrderState.WAIT_DEPOSIT);

        Payment payment = request.toEntity();
        payment.setOrder(findOrder);
        paymentRepository.save(payment);

        // TODO: 로그 테이블에 로그 남기는 로직 추가

        return request.getStatus();
    }
}
