package com.matsinger.barofishserver.order;

import com.matsinger.barofishserver.order.object.*;
import com.matsinger.barofishserver.order.repository.OrderDeliverPlaceRepository;
import com.matsinger.barofishserver.order.repository.OrderProductInfoRepository;
import com.matsinger.barofishserver.order.repository.OrderProductOptionRepository;
import com.matsinger.barofishserver.order.repository.OrderRepository;
import com.matsinger.barofishserver.product.ProductService;
import com.matsinger.barofishserver.user.object.DeliverPlace;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderProductInfoRepository infoRepository;
    private final OrderProductOptionRepository optionRepository;
    private final ProductService productService;
    private final OrderDeliverPlaceRepository deliverPlaceRepository;

    public OrderDto convert2Dto(Orders order) {
        OrderDeliverPlace deliverPlace = selectDeliverPlace(order.getId());
        List<OrderProductDto>
                orderProductDtos =
                selectOrderProductInfoListWithOrderId(order.getId()).stream().map(opi -> {
                    return OrderProductDto.builder().product(productService.selectProduct(opi.getProductId()).convert2ListDto()).deliveryFee(
                            opi.getDeliveryFee()).amount(opi.getAmount()).state(opi.getState()).price(opi.getPrice()).build();
                }).toList();
        OrderDto dto = OrderDto.builder().id(order.getId()).orderedAt(order.getOrderedAt())

                               .build();
        return dto;
    }

    public OrderDeliverPlace selectDeliverPlace(String orderId) {
        return deliverPlaceRepository.findById(orderId).orElseThrow(() -> {
            throw new Error("주문 배송지 정보를 찾을 수 없습니다.");
        });
    }

    public Orders orderProduct(Orders orders, List<OrderProductInfo> infos, OrderDeliverPlace deliverPlace) {
        Orders order = orderRepository.save(orders);
        deliverPlaceRepository.save(deliverPlace);
        infoRepository.saveAll(infos);
        return order;
    }

    public String getOrderId() {
        return orderRepository.selectOrderId().get("id").toString();
    }

    public Orders selectOrder(String id) {
        return orderRepository.findById(id).orElseThrow(() -> {
            throw new Error("주문 정보를 찾을 수 없습니다.");
        });
    }

    public Orders updateOrder(Orders order) {
        return orderRepository.save(order);
    }

    public List<Orders> selectOrderList(Integer userId) {
        List<Orders>
                orders =
                orderRepository.findAllByStateNotInAndUserId(Arrays.asList(OrderState.CANCELED, OrderState.REFUND_DONE),
                        userId);
        return orders;
    }

    public List<Orders> selectCanceledOrderList(Integer userId) {
        List<Orders>
                orders =
                orderRepository.findAllByStateInAndUserId(Arrays.asList(OrderState.CANCELED, OrderState.REFUND_DONE),
                        userId);
        return orders;
    }

    public List<Orders> selectOrderList() {
        return orderRepository.findAll();
    }

    public List<OrderProductInfo> selectOrderProductInfoListWithOrderId(String orderId) {
        return infoRepository.findAllByOrderId(orderId);
    }

    public OrderProductInfo selectOrderProductInfo(Integer orderProductInfoId) {
        return infoRepository.findById(orderProductInfoId).orElseThrow(() -> {
            throw new Error("주문 상품 정보를 찾을 수 없습니다.");
        });
    }

    public void updateOrderProductInfo(List<OrderProductInfo> infos) {
        infoRepository.saveAll(infos);
    }

    public void requestCancelOrderProduct(Integer orderProductInfoId) {
        OrderProductInfo info = infoRepository.findById(orderProductInfoId).orElseThrow(() -> {
            throw new Error("주문 상품 정보를 찾을 수 없습니다.");
        });
        switch (info.getState()) {
            case DELIVERY_READY:
            case ON_DELIVERY:
            case DELIVERY_DONE:
            case EXCHANGE_REQUEST:
            case EXCHANGE_ACCEPT:
            case FINAL_CONFIRM:
            case REFUND_REQUEST:
            case REFUND_ACCEPT:
            case REFUND_DONE:
                throw new Error("취소 불가능한 상태입니다.");
            case CANCEL_REQUEST:
                throw new Error("이미 취소 요청된 상태입니다.");
            case CANCELED:
                throw new Error("취소 완료된 상태입니다.");
            case WAIT_DEPOSIT:
            case PAYMENT_DONE:
                cancelOrderedProduct(orderProductInfoId);
            default:
                info.setState(OrderProductState.CANCEL_REQUEST);
                updateOrderProductInfo(new ArrayList<>(Arrays.asList(info)));
        }
    }

    public void cancelOrderedProduct(Integer orderProductInfoId) {
        OrderProductInfo info = infoRepository.findById(orderProductInfoId).orElseThrow(() -> {
            throw new Error("주문 상품 정보를 찾을 수 없습니다.");
        });
        OrderProductOption option = optionRepository.findFirstByOrderProductId(orderProductInfoId);
        Integer price = (info.getPrice() + option.getPrice()) * info.getAmount();
    }

}
