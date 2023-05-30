package com.matsinger.barofishserver.order.service;

import com.matsinger.barofishserver.order.*;
import com.matsinger.barofishserver.order.dto.request.OrderReqProductInfoDto;
import com.matsinger.barofishserver.order.dto.request.OrderReqProductOptionDto;
import com.matsinger.barofishserver.order.dto.request.OrderRequestDto;
import com.matsinger.barofishserver.order.exception.OrderBusinessException;
import com.matsinger.barofishserver.order.exception.OrderErrorMessage;
import com.matsinger.barofishserver.order.repository.OrderProductInfoRepository;
import com.matsinger.barofishserver.order.repository.OrderProductOptionRepository;
import com.matsinger.barofishserver.order.repository.OrderRepository;
import com.matsinger.barofishserver.order.repository.OrderStoreInfoRepository;
import com.matsinger.barofishserver.product.*;
import com.matsinger.barofishserver.store.Store;
import com.matsinger.barofishserver.store.StoreRepository;
import com.matsinger.barofishserver.user.User;
import com.matsinger.barofishserver.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderCommandService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OptionRepository optionRepository;
    private final OrderProductInfoRepository orderProductInfoRepository;
    private final OrderProductOptionRepository orderProductOptionRepository;
    private final OrderStoreInfoRepository orderStoreInfoRepository;
    private final OrderRepository orderRepository;
    private final StoreRepository storeRepository;

    public String createOrder(OrderRequestDto request) {
        User findUser = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new OrderBusinessException(OrderErrorMessage.USER_NOT_FOUND_EXCEPTION));

        Order createdOrder = Order.builder()
                .user(findUser)
                .state(OrderState.WAIT_DEPOSIT)
                .orderedAt(LocalDateTime.now()).build();

        OrderStoresAndPriceDto storesAndPriceDto = createStores(request, createdOrder);
        createdOrder.setTotalPrice(storesAndPriceDto.getStorePriceSum());

        Order findOrder = orderRepository.createSequence(createdOrder)
                .orElseThrow(() -> new OrderBusinessException(OrderErrorMessage.ORDER_SAVE_FAIL_EXCEPTION));

        orderRepository.save(findOrder);

        return findOrder.getId();
    }

    private OrderStoresAndPriceDto createStores(OrderRequestDto request, Order order) {
        Map<Integer, List<OrderReqProductInfoDto>> storeMap = createStoreMap(request);
        List<OrderStoreInfo> stores = new ArrayList<>();
        int storePriceSum = 0;

        for (Map.Entry<Integer, List<OrderReqProductInfoDto>> entry : storeMap.entrySet()) {
            Store findStore = storeRepository.findById(entry.getKey())
                    .orElseThrow(() -> new OrderBusinessException(OrderErrorMessage.STORE_NOT_FOUND_EXCEPTION));

            OrderStoreInfo createdStoreInfo = OrderStoreInfo.builder()
                    .store(findStore).build();

            OrderProductsAndPriceDto productsAndPriceDto = createProducts(entry.getValue(), createdStoreInfo);
            int productPriceSum = productsAndPriceDto.getGetProductPriceSum();
            createdStoreInfo.setPrice(productPriceSum);
            storePriceSum += productPriceSum;

            createdStoreInfo.setOrder(order);
//            orderStoreInfoRepository.save(createdStoreInfo);
            stores.add(createdStoreInfo);
        }
        return new OrderStoresAndPriceDto(storePriceSum, stores);
    }

    /**
     * param: request
     * return: 상점 아이디, 상점 프로덕트 리스트를 가진 맵
     * 상점을 기준으로 총 가격, 택배비 등을 산정하기 위함
     */
    private Map<Integer, List<OrderReqProductInfoDto>> createStoreMap(OrderRequestDto request) {
        List<OrderReqProductInfoDto> products = request.getProducts();

        Map<Integer, List<OrderReqProductInfoDto>> storeMap = new HashMap<>();
        for (OrderReqProductInfoDto product : products) {

            List<OrderReqProductInfoDto> existingProducts = storeMap.getOrDefault(product.getStoreId(), new ArrayList<>());
            existingProducts.add(product);
            storeMap.put(product.getStoreId(), existingProducts);
        }
        return storeMap;
    }

    private OrderProductsAndPriceDto createProducts(List<OrderReqProductInfoDto> requestProducts, OrderStoreInfo storeInfo) {
        List<OrderProductInfo> products = new ArrayList<>();
        int productPriceSum = 0;

        for (OrderReqProductInfoDto requestProduct : requestProducts) {
            Product findProduct = productRepository.findById(requestProduct.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException(OrderErrorMessage.PRODUCT_NOT_FOUND_EXCEPTION));

            // price는 option에서 산정
            OrderProductInfo createdProduct = OrderProductInfo.builder()
                    .product(findProduct)
                    .state(OrderState.WAIT_DEPOSIT)
                    .discountRate(findProduct.getDiscountRate())
                    .deliveryFee(findProduct.getDeliveryFee()).build();

            OrderProductOptionsAndPriceDto optionsAndPriceDto = createOptions(requestProduct, createdProduct);
            int productFinalPrice = createdProduct.getPrice() + optionsAndPriceDto.getOptionPriceSum();

            // TODO: 현재는 product의 discountRate 반영 안됨
            createdProduct.setOrderStoreInfo(storeInfo);
            createdProduct.setPrice(productFinalPrice);
            productPriceSum += productFinalPrice;

//            orderProductInfoRepository.save(createdProduct);
            products.add(createdProduct);
        }
        return new OrderProductsAndPriceDto(productPriceSum, products);
    }

    private OrderProductOptionsAndPriceDto createOptions(OrderReqProductInfoDto requestProduct, OrderProductInfo productInfo) {
        List<OrderProductOption> options = new ArrayList<>();
        int optionPriceSum = 0;

        for (OrderReqProductOptionDto requestOption : requestProduct.getOptions()) {

            Option findOption = optionRepository.findById(requestOption.getOptionId())
                    .orElseThrow(() -> new IllegalArgumentException(OrderErrorMessage.OPTION_NOT_FOUND_EXCEPTION));

            // orderProduceOption 가격 산정하기
            int optionPrice = findOption.getPrice();
            int optionCount = requestOption.getAmount();
            double discountRate = findOption.getDiscountRate();

            double discountPrice = (optionPrice * optionCount) * discountRate;
            int roundedDiscountPrice = (int) Math.round(discountPrice * 10) / 10;
            int totalPrice = (optionPrice * optionCount) - roundedDiscountPrice;
            optionPriceSum += totalPrice;

            OrderProductOption createdOption = OrderProductOption.builder()
                    .name(findOption.getName())
                    .price(totalPrice)
                    .amount(optionCount)
                    .discountRate(findOption.getDiscountRate())
                    .option(findOption).build();

            createdOption.setOrderProductInfo(productInfo);

//            orderProductOptionRepository.save(createdOption);

            options.add(createdOption);
        }
        return new OrderProductOptionsAndPriceDto(optionPriceSum, options);
    }
}
