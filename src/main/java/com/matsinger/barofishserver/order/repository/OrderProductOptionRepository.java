package com.matsinger.barofishserver.order.repository;

import com.matsinger.barofishserver.order.object.OrderProductOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderProductOptionRepository extends JpaRepository<OrderProductOption, Integer> {
    OrderProductOption findFirstByOrderProductId(Integer orderProductId);
}
