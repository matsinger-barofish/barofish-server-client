package com.matsinger.barofishserver.order.service;

import com.matsinger.barofishserver.order.Order;
import com.matsinger.barofishserver.order.OrderProductInfo;
import com.matsinger.barofishserver.order.OrderProductOption;
import com.matsinger.barofishserver.order.OrderState;
import com.matsinger.barofishserver.order.dto.request.OrderRequestDto;
import com.matsinger.barofishserver.order.exception.OrderErrorMessage;
import com.matsinger.barofishserver.order.repository.OrderProductInfoRepository;
import com.matsinger.barofishserver.order.repository.OrderProductOptionRepository;
import com.matsinger.barofishserver.order.repository.OrderRepository;
import com.matsinger.barofishserver.product.Product;
import com.matsinger.barofishserver.product.ProductRepository;
import com.matsinger.barofishserver.user.User;
import com.matsinger.barofishserver.user.UserRepository;
import com.matsinger.barofishserver.userauth.UserAuthRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderCommandService {

    private final UserRepository userRepository;
    private final UserAuthRepository userAuthRepository;
    private final ProductRepository productRepository;
    private final OrderProductInfoRepository orderProductInfoRepository;
    private final OrderProductOptionRepository orderProductOptionRepository;
    private final OrderRepository orderRepository;
    private final EntityManager em;

    public boolean createOrderSheet(OrderRequestDto request) {

        for (OrderRequestDto.OrderProductInfoDto productInfoDto : request.getProducts()) {
            Product findProduct = productRepository.findById(productInfoDto.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException(OrderErrorMessage.PRODUCT_NOT_FOUND_EXCEPTION));

            OrderProductInfo orderProductInfo = createOrderProductInfo(productInfoDto, findProduct);
            Order order = createAndSaveOrder(request);
            orderProductInfo.setOrder(order);

            OrderProductInfo savedOrderProductInfo = orderProductInfoRepository.save(orderProductInfo);
            saveOrderProductOption(productInfoDto, savedOrderProductInfo);
            orderProductInfoRepository.save(orderProductInfo);
        }

        return true;
    }

    private void saveOrderProductOption(OrderRequestDto.OrderProductInfoDto productInfoDto, OrderProductInfo orderProductInfo) {
        for (OrderRequestDto.OrderProductOptionDto optionDto : productInfoDto.getOptions()) {
            OrderProductOption orderProductOption = OrderProductOption.builder()
                    .orderProductInfo(orderProductInfo)
                    .name(optionDto.getOptionName())
                    .price(optionDto.getOptionPrice()).build();
            orderProductInfo.setOrderProductOption(orderProductOption);
            orderProductOptionRepository.save(orderProductOption);
        }
    }

    private OrderProductInfo createOrderProductInfo(OrderRequestDto.OrderProductInfoDto productInfoDto, Product findProduct) {
        OrderProductInfo orderProductInfo = OrderProductInfo.builder()
                .product(findProduct)
                .price(productInfoDto.getOriginPrice())
                .discountRate(productInfoDto.getDiscountRate())
                .amount(productInfoDto.getAmount())
                .deliveryFee(productInfoDto.getDeliveryFee()).build();
        return orderProductInfo;
    }

    private Order createAndSaveOrder(OrderRequestDto request) {
        User findUser = userAuthRepository.findByLoginId(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException(OrderErrorMessage.USER_NOT_FOUND_EXCEPTION))
                .getUser();

        Order order = Order.builder()
                .user(findUser)
                .state(OrderState.NONE)
                .totalPrice(request.getTotalPrice())
                .orderedAt(LocalDateTime.now()).build();
        orderRepository.createSequenceAndSave(order);
        return order;
    }
}
