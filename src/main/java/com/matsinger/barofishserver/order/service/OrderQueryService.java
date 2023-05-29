package com.matsinger.barofishserver.order.service;

import com.matsinger.barofishserver.order.Order;
import com.matsinger.barofishserver.order.OrderProductInfo;
import com.matsinger.barofishserver.order.OrderProductOption;
import com.matsinger.barofishserver.order.OrderStoreInfo;
import com.matsinger.barofishserver.order.dto.request.OrderReqProductInfoDto;
import com.matsinger.barofishserver.order.dto.response.OrderProductInfoDto;
import com.matsinger.barofishserver.order.dto.response.OrderProductOptionDto;
import com.matsinger.barofishserver.order.dto.response.OrderResponseDto;
import com.matsinger.barofishserver.order.dto.response.OrderStoreInfoDto;
import com.matsinger.barofishserver.order.exception.OrderBusinessException;
import com.matsinger.barofishserver.order.exception.OrderErrorMessage;
import com.matsinger.barofishserver.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderQueryService {

    private final OrderRepository orderRepository;

    public OrderResponseDto getOrder(String orderId) {
        Order findOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderBusinessException(OrderErrorMessage.ORDER_NOT_FOUND_EXCEPTION));

        return OrderResponseDto.builder()
                        .userId(findOrder.getUserId())
                        .orderId(findOrder.getId())
                        .totalPrice(findOrder.getTotalPrice())
                        .stores(createStoreDtos(findOrder)).build();
    }

    private List<OrderStoreInfoDto> createStoreDtos(Order findOrder) {
        List<OrderStoreInfoDto> stores = new ArrayList<>();
        for (OrderStoreInfo storeInfo : findOrder.getOrderStoreInfos()) {
            OrderStoreInfoDto storeInfoDto = storeInfo.toDto(createProductDtos(storeInfo));
            stores.add(storeInfoDto);
        }
        return stores;
    }

    private List<OrderProductInfoDto> createProductDtos(OrderStoreInfo storeInfo) {
        List<OrderProductInfoDto> products = new ArrayList<>();
        for (OrderProductInfo productInfo : storeInfo.getOrderProductInfos()) {
            List<OrderProductOptionDto> options = createOptionDtos(productInfo);
            products.add(productInfo.toDto(options));
        }
        return products;
    }

    private List<OrderProductOptionDto> createOptionDtos(OrderProductInfo productInfo) {
        List<OrderProductOptionDto> options = new ArrayList<>();
        for (OrderProductOption productOption : productInfo.getOrderProductOptions()) {
            options.add(productOption.toDto());
        }
        return options;
    }
}
