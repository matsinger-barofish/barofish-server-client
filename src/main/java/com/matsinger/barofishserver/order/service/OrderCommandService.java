package com.matsinger.barofishserver.order.service;

import com.matsinger.barofishserver.order.Order;
import com.matsinger.barofishserver.order.OrderProductInfo;
import com.matsinger.barofishserver.order.OrderProductOption;
import com.matsinger.barofishserver.order.OrderState;
import com.matsinger.barofishserver.order.dto.request.OrderRequestDto;
import com.matsinger.barofishserver.order.exception.OrderErrorMessage;
import com.matsinger.barofishserver.order.repository.OrderProductInfoRepository;
import com.matsinger.barofishserver.order.repository.OrderProductOptionRepository;
import com.matsinger.barofishserver.product.Product;
import com.matsinger.barofishserver.product.ProductRepository;
import com.matsinger.barofishserver.user.User;
import com.matsinger.barofishserver.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderCommandService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderProductInfoRepository orderProductInfoRepository;
    private final OrderProductOptionRepository orderProductOptionRepository;

    public boolean createOrder(OrderRequestDto request) {


        User findUser = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException(OrderErrorMessage.USER_NOT_FOUND_EXCEPTION));

        Order order = Order.builder()
                .user(findUser)
                .state(OrderState.NONE)
                .totalPrice(request.getTotalPrice())
                .orderedAt(LocalDateTime.now()).build();

        for (OrderRequestDto.OrderProductInfoDto productInfoDto : request.getProducts()) {
            Product findProduct = productRepository.findById(productInfoDto.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException(OrderErrorMessage.PRODUCT_NOT_FOUND_EXCEPTION));

            OrderProductInfo orderProductInfo = OrderProductInfo.builder()
                    .product(findProduct)
                    .price(productInfoDto.getOriginPrice())
                    .discountRate(productInfoDto.getDiscountRate())
                    .amount(productInfoDto.getAmount())
                    .deliveryFee(productInfoDto.getDeliveryFee()).build();
            orderProductInfo.setOrder(order);

            for (OrderRequestDto.OrderProductOptionDto optionDto : productInfoDto.getOptions()) {
                OrderProductOption orderProductOption = OrderProductOption.builder()
                        .orderProductId(findProduct.getId())
                        .name(optionDto.getOptionName())
                        .price(optionDto.getOptionPrice()).build();
                orderProductInfo.setOrderProductOption(orderProductOption);
                orderProductOptionRepository.save(orderProductOption);
            }

            orderProductInfoRepository.save(orderProductInfo);

        }

        return true;
    }
}
