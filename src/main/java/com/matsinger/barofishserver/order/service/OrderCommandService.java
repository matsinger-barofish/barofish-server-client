package com.matsinger.barofishserver.order.service;

import com.matsinger.barofishserver.order.Order;
import com.matsinger.barofishserver.order.OrderProductInfo;
import com.matsinger.barofishserver.order.OrderProductOption;
import com.matsinger.barofishserver.order.OrderState;
import com.matsinger.barofishserver.order.dto.OrderProductInfoDto;
import com.matsinger.barofishserver.order.dto.OrderProductOptionDto;
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

    public String createOrderSheet(OrderRequestDto request) {
        Order order = null;

        for (OrderProductInfoDto productInfoDto : request.getProducts()) {
            Product findProduct = productRepository.findById(productInfoDto.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException(OrderErrorMessage.PRODUCT_NOT_FOUND_EXCEPTION));

            OrderProductInfo orderProductInfo = createOrderProductInfo(productInfoDto, findProduct);
            order = createAndSaveOrder(request);
            orderProductInfo.setOrder(order);

            OrderProductInfo savedOrderProductInfo = orderProductInfoRepository.save(orderProductInfo);
            saveOrderProductOption(productInfoDto, savedOrderProductInfo);
            orderProductInfoRepository.save(orderProductInfo);
        }
        if (order == null) {
            throw new IllegalArgumentException("주문서가 생성되지 않았습니다.");
        }

        return order.getId();
    }

    private void saveOrderProductOption(OrderProductInfoDto productInfoDto, OrderProductInfo orderProductInfo) {
        for (OrderProductOptionDto optionDto : productInfoDto.getOptions()) {
            OrderProductOption orderProductOption = OrderProductOption.builder()
                    .orderProductInfo(orderProductInfo)
                    .name(optionDto.getOptionName())
                    .amount(optionDto.getAmount())
                    .price(optionDto.getOptionPrice()).build();
            orderProductInfo.setOrderProductOption(orderProductOption);
            orderProductOptionRepository.save(orderProductOption);
        }
    }

    private OrderProductInfo createOrderProductInfo(OrderProductInfoDto productInfoDto, Product findProduct) {
        return OrderProductInfo.builder()
                .product(findProduct)
                .price(productInfoDto.getOriginPrice())
                .discountRate(productInfoDto.getDiscountRate())
                .amount(productInfoDto.getAmount())
                .state(OrderState.WAIT_DEPOSIT)
                .deliveryFee(productInfoDto.getDeliveryFee()).build();
    }

    private Order createAndSaveOrder(OrderRequestDto request) {
        User findUser = userAuthRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException(OrderErrorMessage.USER_NOT_FOUND_EXCEPTION))
                .getUser();

        Order order = Order.builder()
                .user(findUser)
                .state(OrderState.WAIT_DEPOSIT)
                .totalPrice(request.getTotalPrice())
                .orderedAt(LocalDateTime.now()).build();
        orderRepository.createSequenceAndSave(order);
        return order;
    }
}
