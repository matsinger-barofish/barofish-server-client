package com.matsinger.barofishserver.domain.order.orderprductinfo.repository;

import com.matsinger.barofishserver.domain.order.orderprductinfo.domain.OrderProductOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderProductOptionRepository extends JpaRepository<OrderProductOption, Integer> {
    OrderProductOption findFirstByOrderProductId(Integer orderProductId);
}
