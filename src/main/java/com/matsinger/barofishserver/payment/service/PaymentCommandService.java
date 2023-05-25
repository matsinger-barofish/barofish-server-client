package com.matsinger.barofishserver.payment.service;

import com.matsinger.barofishserver.order.Order;
import com.matsinger.barofishserver.order.OrderState;
import com.matsinger.barofishserver.order.repository.OrderRepository;
import com.matsinger.barofishserver.payment.Payment;
import com.matsinger.barofishserver.payment.dto.request.PortOnePaymentRequestDto;
import com.matsinger.barofishserver.payment.dto.response.PaymentSuccessResponseDto;
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
    public PaymentSuccessResponseDto proceedPayment(PortOnePaymentRequestDto request) {


        Order findOrder = orderRepository.findById(request.getMerchant_uid())
                .orElseThrow(() -> {
                    throw new IllegalArgumentException(PaymentErrorMessage.ORDER_NOT_FOUND_MESSAGE);
                });

        if (request.getStatus().equals("fail")) {
            // 결제 실패했을 때
            findOrder.setState(OrderState.WAIT_DEPOSIT);
        } else if (!request.getPay_method().equals("trans") && !request.getPay_method().equals("vbank")) {
            // 실시간 계좌이체, 가상계좌가 아닐 때
            findOrder.setState(OrderState.PAYMENT_DONE);
        } else {
            // 실시간 계좌이체, 가상계좌일 때
            findOrder.setState(OrderState.WAIT_DEPOSIT);
        }

        // TODO: 로그 테이블에 로그 남기는 로직 추가

        int paymentId = createAndSavePayment(request, findOrder);


        return PaymentSuccessResponseDto.builder()
                .paymentId(paymentId)
                .status(request.getStatus()).build();
    }

    private int createAndSavePayment(PortOnePaymentRequestDto request, Order findOrder) {
        Payment payment = request.toEntity();
        payment.setOrder(findOrder);
        return paymentRepository.save(payment).getId();
    }
}
