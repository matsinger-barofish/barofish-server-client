package com.matsinger.barofishserver.domain.order.application;

import com.matsinger.barofishserver.domain.order.domain.Orders;
import com.matsinger.barofishserver.domain.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderQueryService {

    private final OrderRepository orderRepository;

    public Orders findById(String orderId) {
        return orderRepository.findById(orderId)
                              .orElseThrow(() -> new IllegalArgumentException("주문 정보를 찾을 수 없습니다."));
    }
}
