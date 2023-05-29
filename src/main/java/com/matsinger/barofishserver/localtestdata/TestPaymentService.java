package com.matsinger.barofishserver.localtestdata;

import com.matsinger.barofishserver.order.Order;
import com.matsinger.barofishserver.order.repository.OrderRepository;
import com.matsinger.barofishserver.payment.dto.request.PortOnePaymentRequestDto;
import com.matsinger.barofishserver.payment.service.PaymentCommandService;
import com.matsinger.barofishserver.user.User;
import com.matsinger.barofishserver.userauth.UserAuth;
import com.matsinger.barofishserver.userauth.UserAuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Component
@RequiredArgsConstructor
public class TestPaymentService {

    private final PaymentCommandService paymentCommandService;
    private final OrderRepository orderRepository;
    private final UserAuthRepository userAuthRepository;

    private final List<String> userSuffixes = TestUserService.suffixes;
    private static final List<String> scenarios = List.of(
            "카드",
            "가상계좌",
            "결제실패",
            "결제하지 않고 주문서 이탈",
            "결제 취소 성공(발송 전)",
            "결제 취소 실패(발송 후)"
    );

    public void createPayment() {

        int seq = 0;
        for (String suffix : userSuffixes) {
            UserAuth findUserAuth = userAuthRepository.findByLoginId("user" + suffix).get();
            User user = findUserAuth.getUser();
            List<Order> findOrders = orderRepository.findByUser(user).get();

            PortOnePaymentRequestDto PaymentRequest = new PortOnePaymentRequestDto();

            for (Order findOrder : findOrders) {
                seq++;
                PaymentRequest = scenario1(scenarios, seq, findUserAuth, PaymentRequest, findOrder);
                PaymentRequest = scenario2(scenarios, seq, findUserAuth, PaymentRequest, findOrder);
                PaymentRequest = scenario3(scenarios, seq, findUserAuth, PaymentRequest, findOrder);
                PaymentRequest = scenario4(scenarios, seq, findUserAuth, PaymentRequest, findOrder);
                PaymentRequest = scenario5(scenarios, seq, findUserAuth, PaymentRequest, findOrder);
                PaymentRequest = scenario6(scenarios, seq, findUserAuth, PaymentRequest, findOrder);

                if (seq == 4) {
                    continue;
                }
                paymentCommandService.proceedPayment(PaymentRequest);
            }
        }
    }

    private PortOnePaymentRequestDto scenario1(List<String> scenarios, int seq, UserAuth findUserAuth, PortOnePaymentRequestDto PaymentRequest, Order findOrder) {
        if (findUserAuth.getLoginId().endsWith("A") && findOrder.getName().endsWith("A")) {
            // 유저A 주문A : 카드
            PaymentRequest = PortOnePaymentRequestDto.builder()
                    .imp_uid("test" + seq)
                    .merchant_uid(findOrder.getId())
                    .pay_method("card")
                    .paid_amount(findOrder.getTotalPrice())
                    .status("paid")
                    .name(scenarios.get(seq - 1))
                    .pg_provider("tosspay")
                    .pg_tid("1234" + seq)
                    .buyer_name("test" + seq)
                    .buyer_email("test" + seq + "@gmail.com")
                    .buyer_tel("010-1234-123" + seq)
                    .buyer_addr("서울시 강남구 " + seq + "번지")
                    .paid_at(String.valueOf(System.currentTimeMillis() / 1000))
                    .receipt_url("www.test" + seq + ".com").build();
        }
        return PaymentRequest;
    }

    private PortOnePaymentRequestDto scenario2(List<String> scenarios, int seq, UserAuth findUserAuth, PortOnePaymentRequestDto PaymentRequest, Order findOrder) {
        if (findUserAuth.getLoginId().endsWith("A") && findOrder.getName().endsWith("B")) {
            // 가상계좌
            PaymentRequest = PortOnePaymentRequestDto.builder()
                    .imp_uid("test" + seq)
                    .merchant_uid(findOrder.getId())
                    .pay_method("vbank")
                    .paid_amount(findOrder.getTotalPrice())
                    .status("ready")
                    .name(scenarios.get(seq - 1))
                    .pg_provider("tosspay")
                    .pg_tid("1234" + seq)
                    .buyer_name("test" + seq)
                    .buyer_email("test" + seq + "@gmail.com")
                    .buyer_tel("010-1234-123" + seq)
                    .buyer_addr("서울시 강남구 " + seq + "번지")
                    .paid_at(String.valueOf(System.currentTimeMillis() / 1000))
                    .receipt_url("www.test" + seq + ".com").build();
        }
        return PaymentRequest;
    }

    private PortOnePaymentRequestDto scenario3(List<String> scenarios, int seq, UserAuth findUserAuth, PortOnePaymentRequestDto PaymentRequest, Order findOrder) {
        if (findUserAuth.getLoginId().endsWith("B") && findOrder.getName().endsWith("A")) {
            // 결제 실패
            PaymentRequest = PortOnePaymentRequestDto.builder()
                    .imp_uid("test" + seq)
                    .merchant_uid(findOrder.getId())
                    .pay_method("card")
                    .paid_amount(findOrder.getTotalPrice())
                    .status("fail")
                    .name(scenarios.get(seq - 1))
                    .pg_provider("tosspay")
                    .pg_tid("1234" + seq)
                    .buyer_name("test" + seq)
                    .buyer_email("test" + seq + "@gmail.com")
                    .buyer_tel("010-1234-123" + seq)
                    .buyer_addr("서울시 강남구 " + seq + "번지")
                    .paid_at(String.valueOf(System.currentTimeMillis() / 1000))
                    .receipt_url("www.test" + seq + ".com").build();
        }
        return PaymentRequest;
    }

    private PortOnePaymentRequestDto scenario4(List<String> scenarios, int seq, UserAuth findUserAuth, PortOnePaymentRequestDto PaymentRequest, Order findOrder) {
        if (findUserAuth.getLoginId().endsWith("B") && findOrder.getName().endsWith("B")) {
            // 결제하지 않고 주문서 이탈
        }
        return PaymentRequest;
    }

    private PortOnePaymentRequestDto scenario5(List<String> scenarios, int seq, UserAuth findUserAuth, PortOnePaymentRequestDto PaymentRequest, Order findOrder) {
        if (findUserAuth.getLoginId().endsWith("C") && findOrder.getName().endsWith("A")) {
            // 결제 취소 성공 (발송 전)
            PaymentRequest = PortOnePaymentRequestDto.builder()
                    .imp_uid("test" + seq)
                    .merchant_uid(findOrder.getId())
                    .pay_method("card")
                    .paid_amount(findOrder.getTotalPrice())
                    .status("paid")
                    .name(scenarios.get(seq - 1))
                    .pg_provider("tosspay")
                    .pg_tid("1234" + seq)
                    .buyer_name("test" + seq)
                    .buyer_email("test" + seq + "@gmail.com")
                    .buyer_tel("010-1234-123" + seq)
                    .buyer_addr("서울시 강남구 " + seq + "번지")
                    .paid_at(String.valueOf(System.currentTimeMillis() / 1000))
                    .receipt_url("www.test" + seq + ".com").build();
        }
        return PaymentRequest;
    }

    private PortOnePaymentRequestDto scenario6(List<String> scenarios, int seq, UserAuth findUserAuth, PortOnePaymentRequestDto PaymentRequest, Order findOrder) {
        if (findUserAuth.getLoginId().endsWith("C") && findOrder.getName().endsWith("B")) {
            // 결제 취소 실패 (발송 후)
            PaymentRequest = PortOnePaymentRequestDto.builder()
                    .imp_uid("test" + seq)
                    .merchant_uid(findOrder.getId())
                    .pay_method("card")
                    .paid_amount(findOrder.getTotalPrice())
                    .status("paid")
                    .name(scenarios.get(seq - 1))
                    .pg_provider("tosspay")
                    .pg_tid("1234" + seq)
                    .buyer_name("test" + seq)
                    .buyer_email("test" + seq + "@gmail.com")
                    .buyer_tel("010-1234-123" + seq)
                    .buyer_addr("서울시 강남구 " + seq + "번지")
                    .paid_at(String.valueOf(System.currentTimeMillis() / 1000))
                    .receipt_url("www.test" + seq + ".com").build();
        }
        return PaymentRequest;
    }
}
