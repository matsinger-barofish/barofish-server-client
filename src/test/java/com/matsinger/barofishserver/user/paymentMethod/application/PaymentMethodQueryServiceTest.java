package com.matsinger.barofishserver.user.paymentMethod.application;

import com.matsinger.barofishserver.domain.user.paymentMethod.application.PaymentMethodQueryService;
import com.matsinger.barofishserver.domain.user.paymentMethod.repository.PaymentMethodRepository;
import com.matsinger.barofishserver.domain.user.paymentMethod.domain.PaymentMethod;
import com.matsinger.barofishserver.domain.user.paymentMethod.dto.PaymentMethodDto;
import com.matsinger.barofishserver.domain.user.domain.User;
import com.matsinger.barofishserver.domain.user.domain.UserState;
import com.matsinger.barofishserver.domain.user.repository.UserRepository;
import com.matsinger.barofishserver.utils.AES256;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@SpringBootTest
@ActiveProfiles("local")
class PaymentMethodQueryServiceTest {

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AES256 aes256;

    @Autowired
    private PaymentMethodQueryService paymentMethodQueryService;

    @AfterEach
    void tearDown() {
        paymentMethodRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("결제 수단 목록을 불러올 수 있다.")
    @Test
    void getPaymentMethods() throws Exception {
        // given
        User createdUser = User.builder()
                .state(UserState.ACTIVE)
                .joinAt(Timestamp.valueOf(LocalDateTime.now()))
                .build();
        userRepository.save(createdUser);

        PaymentMethod paymentMethod1 = createPaymentMethod(createdUser, "test1");
        PaymentMethod paymentMethod2 = createPaymentMethod(createdUser, "test2");
        paymentMethodRepository.saveAll(List.of(paymentMethod1, paymentMethod2));

        // when // then
        assertThat(paymentMethodQueryService.getPaymentMethods(createdUser.getId()))
                .isPresent();
        Optional<List<PaymentMethodDto>> paymentMethods = paymentMethodQueryService.getPaymentMethods(createdUser.getId());
        assertThat(paymentMethods.get()).hasSize(2);
        assertThat(paymentMethods.get())
                .extracting("name", "cardName")
                .containsExactlyInAnyOrder(
                        tuple("test1", "test1"),
                        tuple("test2", "test2")
                );
    }

    private PaymentMethod createPaymentMethod(User createdUser, String cardName) throws Exception {
        return PaymentMethod.builder()
                .userId(createdUser.getId())
                .name(cardName)
                .cardName(cardName)
                .cardNo(aes256.encrypt("1234-1234-"))
                .expiryAt("12/12")
                .birth("940918")
                .passwordTwoDigit("1234")
                .customerUid("1234")
                .build();
    }
}