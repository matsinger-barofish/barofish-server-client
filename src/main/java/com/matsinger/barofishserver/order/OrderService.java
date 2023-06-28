package com.matsinger.barofishserver.order;

import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.order.object.*;
import com.matsinger.barofishserver.order.repository.OrderDeliverPlaceRepository;
import com.matsinger.barofishserver.order.repository.OrderProductInfoRepository;
import com.matsinger.barofishserver.order.repository.OrderProductOptionRepository;
import com.matsinger.barofishserver.order.repository.OrderRepository;
import com.matsinger.barofishserver.payment.PaymentService;
import com.matsinger.barofishserver.product.ProductService;
import com.matsinger.barofishserver.product.object.Option;
import com.matsinger.barofishserver.product.object.OptionItem;
import com.matsinger.barofishserver.product.object.Product;
import com.matsinger.barofishserver.review.ReviewService;
import com.matsinger.barofishserver.store.StoreService;
import com.matsinger.barofishserver.store.object.StoreDeliverFeeType;
import com.matsinger.barofishserver.store.object.StoreInfo;
import com.matsinger.barofishserver.user.UserService;
import com.matsinger.barofishserver.user.object.UserInfoDto;
import com.siot.IamportRestClient.exception.IamportResponseException;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
    private final UserService userService;
    private final StoreService storeService;
    private final OrderDeliverPlaceRepository deliverPlaceRepository;
    private final ReviewService reviewService;
    private final PaymentService paymentService;


    public OrderDto convert2Dto(Orders order, Integer storeId, Specification<OrderProductInfo> productSpec) {
        OrderDeliverPlace deliverPlace = selectDeliverPlace(order.getId());
        if (productSpec != null) {
            productSpec = productSpec.and((root, query, builder) -> {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(builder.equal(root.get("orderId"), order.getId()));
                return builder.and(predicates.toArray(new Predicate[0]));
            });
        }
        List<OrderProductInfo>
                infos =
                productSpec == null ? selectOrderProductInfoListWithOrderId(order.getId()) : selectOrderProductInfoList(
                        productSpec);
        List<OrderProductDto> orderProductDtos = infos.stream().map(opi -> {
            OrderProductOption option = optionRepository.findFirstByOrderProductId(opi.getId());
            Product product = productService.selectProduct(opi.getProductId());
            StoreInfo storeInfo = storeService.selectStoreInfo(product.getStoreId());
            if (storeId != null && storeId != storeInfo.getStoreId()) return null;
            OptionItem optionItem = productService.selectOptionItem(opi.getOptionItemId());
            Boolean isWritten = reviewService.checkReviewWritten(order.getUserId(), product.getId(), order.getId());
            return OrderProductDto.builder().id(opi.getId()).storeId(storeInfo.getStoreId()).optionItem(optionItem.convert2Dto()).product(
                    productService.selectProduct(opi.getProductId()).convert2ListDto()).optionName(option ==
                    null ? null : option.getName()).amount(opi.getAmount()).state(opi.getState()).price(opi.getPrice()).storeName(
                    storeInfo.getName()).storeProfile(storeInfo.getProfileImage()).isReviewWritten(isWritten).deliverFee(
                    storeInfo.getDeliverFee()).deliverFeeType(storeInfo.getDeliverFeeType()).minOrderPrice(storeInfo.getMinOrderPrice()).build();
        }).filter(v -> v != null).toList();

        UserInfoDto userInfoDto = userService.selectUserInfo(order.getUserId()).convert2Dto();
        OrderDto
                dto =
                OrderDto.builder().id(order.getId()).orderedAt(order.getOrderedAt()).user(userInfoDto).totalAmount(order.getTotalPrice()).deliverPlace(
                        deliverPlace.convert2Dto()).productInfos(orderProductDtos).couponDiscount(order.getCouponDiscount()).usePoint(
                        order.getUsePoint()).ordererName(order.getOrdererName()).ordererTel(order.getOrdererTel()).build();
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

    public Page<Orders> selectOrderLitByAdmin(PageRequest pageRequest, Specification<Orders> spec) {
        return orderRepository.findAll(spec, pageRequest);
    }

    public Page<Orders> selectOrderList(Integer userId, PageRequest pageRequest) {
        Page<Orders>
                orders =
                orderRepository.findAllByStateNotInAndUserId(Arrays.asList(OrderState.CANCELED,
                        OrderState.REFUND_DONE,
                        OrderState.WAIT_DEPOSIT), userId, pageRequest);
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

    public List<OrderProductInfo> selectOrderProductInfoList(Specification<OrderProductInfo> spec) {
        return infoRepository.findAll(spec);
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

    public void requestCancelOrderProduct(Integer orderProductInfoId) throws IamportResponseException, IOException {
        OrderProductInfo info = infoRepository.findById(orderProductInfoId).orElseThrow(() -> {
            throw new Error("주문 상품 정보를 찾을 수 없습니다.");
        });
        switch (info.getState()) {
            case WAIT_DEPOSIT:
            case PAYMENT_DONE:
                cancelOrderedProduct(orderProductInfoId);
                break;
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

            default:
                info.setState(OrderProductState.CANCEL_REQUEST);
                updateOrderProductInfo(new ArrayList<>(Arrays.asList(info)));
        }
    }

    public Integer calculateCancelOrderPrice(Integer orderProductInfoId) {
        OrderProductInfo info = infoRepository.findById(orderProductInfoId).orElseThrow(() -> {
            throw new Error("주문 상품 정보를 찾을 수 없습니다.");
        });
        Product product = productService.selectProduct(info.getProductId());
        List<OrderProductInfo> infos = selectOrderProductInfoListWithOrderId(info.getOrderId()).stream().filter(v -> {
            Product p = productService.selectProduct(v.getProductId());
            if (product.getStoreId() != p.getStoreId()) return false;
            else return true;
        }).toList();
        Integer price = info.getPrice() * info.getAmount();
        StoreInfo storeInfo = storeService.selectStoreInfo(product.getStoreId());
        Integer orderPrice = infos.stream().mapToInt(v -> v.getPrice() * v.getAmount()).sum();
        Integer paidDeliverFee = storeService.getDeliverFee(storeInfo, orderPrice);
        if ((info.getState().equals(OrderProductState.PAYMENT_DONE) ||
                info.getState().equals(OrderProductState.DELIVERY_READY) ||
                info.getState().equals(OrderProductState.CANCEL_REQUEST))) {

        } else {
            if (storeInfo.getDeliverFeeType().equals(StoreDeliverFeeType.FREE_IF_OVER)) {
                if (paidDeliverFee - price != 0 && paidDeliverFee - price < storeInfo.getMinOrderPrice()) {
                    price -= storeInfo.getDeliverFee();
                }
            } else if (storeInfo.getDeliverFeeType().equals(StoreDeliverFeeType.FREE)) {

            } else {
                if (infos.stream().allMatch(v -> v.getId() == info.getId() ||
                        v.getState().equals(OrderProductState.CANCELED))) {

                }
            }
        }
//                        .stream().filter(v -> {
//                    if (v.getId() != info.getId()) return true;
//                    return false;
//                }).toList();


        return price;
    }

    public void cancelOrderedProduct(Integer orderProductInfoId) throws IamportResponseException, IOException {
        OrderProductInfo info = infoRepository.findById(orderProductInfoId).orElseThrow(() -> {
            throw new Error("주문 상품 정보를 찾을 수 없습니다.");
        });
        if (info.getState().equals(OrderProductState.CANCELED)) throw new Error("이미 취소된 상품입니다.");
        OrderProductOption option = optionRepository.findFirstByOrderProductId(orderProductInfoId);
        Orders order = orderRepository.findById(info.getOrderId()).orElseThrow(() -> {
            throw new Error("주문 정보를 찾을 수 없습니다.");
        });
        List<OrderProductInfo> infos = selectOrderProductInfoListWithOrderId(info.getOrderId());
        Integer price = (info.getPrice() + (option != null ? option.getPrice() : 0)) * info.getAmount();

//        if (infos.stream().allMatch(opi -> opi.getState().equals(OrderProductState.CANCELED) ||
//                opi.getId() == info.getId())) {
//            price -=
//                    (order.getUsePoint() != null ? order.getUsePoint() : 0) +
//                            (order.getCouponDiscount() != null ? order.getCouponDiscount() : 0);
//        }
        paymentService.cancelPayment(order.getImpUid(), price);
        info.setState(OrderProductState.CANCELED);
        infoRepository.save(info);
    }

    public List<OrderProductInfo> selectOrderProductInfoWithState(List<OrderProductState> states) {
        return infoRepository.findAllByStateIn(states);
    }

    public Integer getProductPrice(Product product, Integer OptionItemId, Integer amount, Integer deliverBoxPerAmount) {
        OptionItem optionItem = productService.selectOptionItem(OptionItemId);
        Integer totalPrice = optionItem.getDiscountPrice() * amount;
        Integer
                deliverFee =
                optionItem.getDeliverFee() *
                        (deliverBoxPerAmount != null ? (int) (amount / deliverBoxPerAmount) + 1 : 1);
        return totalPrice;
    }

    public Integer getProductDeliveryFee(Product product, Integer OptionItemId, Integer amount) {
        OptionItem optionItem = productService.selectOptionItem(OptionItemId);
        Integer
                deliverFee =
                optionItem.getDeliverFee() *
                        (optionItem.getDeliverBoxPerAmount() != null ? (int) (amount /
                                optionItem.getDeliverBoxPerAmount()) + 1 : 1);
        return deliverFee;
    }

    public List<OrderProductInfo> selectOrderProductInfoWithIds(List<Integer> ids) {
        return infoRepository.findAllByIdIn(ids);
    }

    public Page<OrderProductInfo> selectOrderProductInfoList(Specification<OrderProductInfo> spec, Pageable pageable) {
        return infoRepository.findAll(spec, pageable);
    }

    public List<OrderProductInfo> updateOrderProductInfos(List<OrderProductInfo> infos) {
        return infoRepository.saveAll(infos);
    }

    public OrderProductInfoDto convert2InfoDto(OrderProductInfo info) {
        return OrderProductInfoDto.builder().id(info.getId()).orderId(info.getOrderId()).productId(info.getProductId()).optionItemId(
                info.getOptionItemId()).state(info.getState()).settlePrice(info.getSettlePrice()).price(info.getPrice()).amount(
                info.getAmount()).deliveryFee(info.getDeliveryFee()).cancelReasonContent(info.getCancelReasonContent()).cancelReason(
                info.getCancelReason()).deliverCompanyCode(info.getDeliverCompanyCode()).invoiceCode(info.getInvoiceCode()).isSettled(
                info.getIsSettled()).settledAt(info.getSettledAt()).product(productService.convert2ListDto(info.getProduct())).build();
    }

    public Integer calculateTotalPrice(OrderController.OrderReq data) {
        List<Product> products = new ArrayList<>();
        Integer totalPrice = data.getProducts().stream().mapToInt(v -> {
            Product product = productService.selectProduct(v.getProductId());
            products.add(product);
            return getProductPrice(product, v.getOptionId(), v.getAmount(), null);
        }).sum();

        Integer deliverFee = products.stream().map(v -> v.getStoreId()).distinct().mapToInt(v -> {
            StoreInfo storeInfo = storeService.selectStoreInfo(v);
            Integer StorePrice = products.stream().filter(p -> p.getStoreId() == v).mapToInt(v1 -> {
                for (OrderController.OrderProductReq d : data.getProducts()) {
                    if (d.productId == v1.getId()) {
                        Product product = productService.selectProduct(d.productId);
                        return getProductPrice(v1, d.getOptionId(), d.getAmount(), product.getDeliverBoxPerAmount());
                    }
                }
                return 0;
            }).sum();
            return StorePrice += storeService.getDeliverFee(storeInfo, StorePrice);
        }).sum();

        return totalPrice + deliverFee -
                (data.getCouponDiscountPrice() != null ? data.getCouponDiscountPrice() : 0) -
                (data.getPoint() != null ? data.getPoint() : 0);
    }

    public List<OrderProductInfo> selectOrderProductInfoListWithIds(List<Integer> ids) {
        return infoRepository.findAllByIdIn(ids);
    }

    public List<OrderProductInfo> selectOrderProductInfoListWithStoreIdAndIsSettled(Integer storeId,
                                                                                    Boolean isSettled) {
        return infoRepository.findAllByProduct_StoreIdAndIsSettled(storeId, isSettled);
    }
}
