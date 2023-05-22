package com.matsinger.barofishserver.order.service;

import com.matsinger.barofishserver.order.Order;
import com.matsinger.barofishserver.order.OrderProductInfo;
import com.matsinger.barofishserver.order.OrderProductOption;
import com.matsinger.barofishserver.order.dto.OrderProductInfoDto;
import com.matsinger.barofishserver.order.dto.OrderProductOptionDto;
import com.matsinger.barofishserver.order.dto.response.OrderResponseDto;
import com.matsinger.barofishserver.order.exception.OrderErrorMessage;
import com.matsinger.barofishserver.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderQueryService {

    private final OrderRepository orderRepository;

    public OrderResponseDto getOrder(String orderId) {
        Order findOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    throw new IllegalStateException(OrderErrorMessage.ORDER_NOT_FOUND_EXCEPTION);
                });

        List<OrderProductInfoDto> products = new ArrayList<>();

        for (OrderProductInfo productInfo: findOrder.getOrderProductInfo()) {
            List<OrderProductOptionDto> options = new ArrayList<>();

            for (OrderProductOption productOption : productInfo.getOrderProductOption()) {
                options.add(productOption.toDto());
            }
            products.add(productInfo.toDto(options));
        }
        return OrderResponseDto.builder()
                .userId(findOrder.getId())
                .totalPrice(findOrder.getTotalPrice())
                .products(products).build();
    }
}
