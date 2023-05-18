package com.matsinger.barofishserver.order.repository;

import com.matsinger.barofishserver.order.OrderProductOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderProductOptionRepository extends JpaRepository<OrderProductOption, Long> {
}
