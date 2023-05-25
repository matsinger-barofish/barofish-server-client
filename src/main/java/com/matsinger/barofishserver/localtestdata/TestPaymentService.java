package com.matsinger.barofishserver.localtestdata;

import com.matsinger.barofishserver.order.Order;
import com.matsinger.barofishserver.order.repository.OrderRepository;
import com.matsinger.barofishserver.payment.Payment;
import com.matsinger.barofishserver.payment.PaymentState;
import com.matsinger.barofishserver.payment.dto.request.PortOnePaymentRequestDto;
import com.matsinger.barofishserver.payment.repository.PaymentRepository;
import com.matsinger.barofishserver.payment.service.PaymentCommandService;
import com.matsinger.barofishserver.user.User;
import com.matsinger.barofishserver.userauth.UserAuth;
import com.matsinger.barofishserver.userauth.UserAuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@Component
@RequiredArgsConstructor
public class TestPaymentService {

    private final PaymentCommandService paymentCommandService;
    private final OrderRepository orderRepository;
    private final UserAuthRepository userAuthRepository;

    public void createPayment() {

        for (int i=1; i<3; i++) {
            UserAuth findUserAuth = userAuthRepository.findByLoginId("test" + i).get();
            User user = findUserAuth.getUser();
            List<Order> findOrders = orderRepository.findByUser(user).get();

            for (int j = 0; j < findOrders.size(); j++) {
                Order findOrder = findOrders.get(j);
                PortOnePaymentRequestDto PaymentRequest = PortOnePaymentRequestDto.builder()
                        .imp_uid("test" + j)
                        .merchant_uid(findOrder.getId())
                        .pay_method("card")
                        .paid_amount(findOrder.getTotalPrice())
                        .status("paid")
                        .name("testOrder" + j)
                        .pg_provider("tosspay")
                        .pg_tid("1234" + j)
                        .buyer_name("test" + j)
                        .buyer_email("test" + j + "@gmail.com")
                        .buyer_tel("010-1234-123" + j)
                        .buyer_addr("서울시 강남구 " + j + "번지")
                        .paid_at(String.valueOf(System.currentTimeMillis() / 1000))
                        .receipt_url("www.test" + j + ".com").build();

                paymentCommandService.proceedPayment(PaymentRequest);
            }
        }
    }
}
