package com.matsinger.barofishserver.domain.user.paymentMethod.repository;

import com.matsinger.barofishserver.domain.user.paymentMethod.domain.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Integer> {
    List<PaymentMethod> findAllByUserId(Integer userId);

    Boolean existsByCardNoAndUserId(String cardNo, Integer userId);

    void deleteAllByUserIdIn(List<Integer> userIds);
}
