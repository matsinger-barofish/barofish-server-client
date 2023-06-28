package com.matsinger.barofishserver.user;

import com.matsinger.barofishserver.user.object.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Integer> {
    List<PaymentMethod> findAllByUserId(Integer userId);

    Boolean existsByCardNoAndUserId(String cardNo, Integer userId);
}
