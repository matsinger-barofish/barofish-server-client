package com.matsinger.barofishserver.order.application;

import com.matsinger.barofishserver.coupon.application.CouponCommandService;
import com.matsinger.barofishserver.coupon.application.CouponQueryService;
import com.matsinger.barofishserver.coupon.domain.Coupon;
import com.matsinger.barofishserver.deliver.domain.DeliveryCompany;
import com.matsinger.barofishserver.deliver.repository.DeliveryCompanyRepository;
import com.matsinger.barofishserver.notification.application.NotificationCommandService;
import com.matsinger.barofishserver.notification.dto.NotificationMessage;
import com.matsinger.barofishserver.notification.dto.NotificationMessageType;
import com.matsinger.barofishserver.order.api.OrderController;
import com.matsinger.barofishserver.order.domain.OrderDeliverPlace;
import com.matsinger.barofishserver.order.domain.OrderPaymentWay;
import com.matsinger.barofishserver.order.domain.OrderState;
import com.matsinger.barofishserver.order.domain.Orders;
import com.matsinger.barofishserver.order.dto.OrderDto;
import com.matsinger.barofishserver.order.dto.OrderProductReq;
import com.matsinger.barofishserver.order.dto.OrderReq;
import com.matsinger.barofishserver.order.orderprductinfo.domain.OrderCancelReason;
import com.matsinger.barofishserver.order.orderprductinfo.domain.OrderProductInfo;
import com.matsinger.barofishserver.order.orderprductinfo.domain.OrderProductOption;
import com.matsinger.barofishserver.order.orderprductinfo.domain.OrderProductState;
import com.matsinger.barofishserver.order.orderprductinfo.dto.OrderProductDto;
import com.matsinger.barofishserver.order.orderprductinfo.dto.OrderProductInfoDto;
import com.matsinger.barofishserver.order.repository.OrderDeliverPlaceRepository;
import com.matsinger.barofishserver.order.orderprductinfo.repository.OrderProductInfoRepository;
import com.matsinger.barofishserver.order.orderprductinfo.repository.OrderProductOptionRepository;
import com.matsinger.barofishserver.order.repository.OrderRepository;
import com.matsinger.barofishserver.payment.application.PaymentService;
import com.matsinger.barofishserver.payment.domain.Payments;
import com.matsinger.barofishserver.product.application.ProductService;
import com.matsinger.barofishserver.product.difficultDeliverAddress.application.DifficultDeliverAddressQueryService;
import com.matsinger.barofishserver.product.difficultDeliverAddress.domain.DifficultDeliverAddress;
import com.matsinger.barofishserver.product.optionitem.domain.OptionItem;
import com.matsinger.barofishserver.product.domain.Product;
import com.matsinger.barofishserver.review.application.ReviewQueryService;
import com.matsinger.barofishserver.store.application.StoreService;
import com.matsinger.barofishserver.store.domain.StoreDeliverFeeType;
import com.matsinger.barofishserver.store.domain.StoreInfo;
import com.matsinger.barofishserver.user.application.UserCommandService;
import com.matsinger.barofishserver.userinfo.domain.UserInfo;
import com.matsinger.barofishserver.userinfo.dto.UserInfoDto;
import com.siot.IamportRestClient.exception.IamportResponseException;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
@Slf4j
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderProductInfoRepository infoRepository;
    private final OrderProductOptionRepository optionRepository;
    private final OrderDeliverPlaceRepository deliverPlaceRepository;
    private final DeliveryCompanyRepository deliveryCompanyRepository;
    private final ProductService productService;
    private final UserCommandService userService;
    private final StoreService storeService;
    private final ReviewQueryService reviewQueryService;
    private final PaymentService paymentService;
    private final DifficultDeliverAddressQueryService difficultDeliverAddressQueryService;
    private final CouponQueryService couponQueryService;
    private final NotificationCommandService notificationCommandService;
    private final CouponCommandService couponCommandService;

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
//            OrderProductOption option = optionRepository.findFirstByOrderProductId(opi.getId());
            Product product = productService.selectProduct(opi.getProductId());
            StoreInfo storeInfo = storeService.selectStoreInfo(product.getStoreId());
            if (storeId != null && storeId != storeInfo.getStoreId()) return null;
            OptionItem optionItem = productService.selectOptionItem(opi.getOptionItemId());
            Optional<DeliveryCompany>
                    deliveryCompany =
                    opi.getDeliverCompanyCode() !=
                            null ? deliveryCompanyRepository.findById(opi.getDeliverCompanyCode()) : Optional.empty();
            Boolean isWritten = reviewQueryService.checkReviewWritten(order.getUserId(), product.getId(), opi.getId());
            return OrderProductDto.builder().id(opi.getId()).storeId(storeInfo.getStoreId()).optionItem(optionItem.convert2Dto()).product(
                    productService.convert2ListDto(productService.selectProduct(opi.getProductId()))).optionName(
                    optionItem.getName()).amount(opi.getAmount()).state(opi.getState()).price(opi.getPrice()).storeName(
                    storeInfo.getName()).storeProfile(storeInfo.getProfileImage()).deliverFee(getProductDeliveryFee(
                    product,
                    optionItem.getId(),
                    opi.getAmount())).deliverCompany(deliveryCompany.map(DeliveryCompany::getName).orElse(null)).invoiceCode(
                    opi.getInvoiceCode()).cancelReason(opi.getCancelReason()).cancelReasonContent(opi.getCancelReasonContent()).isReviewWritten(
                    isWritten).deliverFeeType(storeInfo.getDeliverFeeType()).minOrderPrice(storeInfo.getMinOrderPrice()).finalConfirmedAt(
                    opi.getFinalConfirmedAt()).build();
        }).filter(Objects::nonNull).toList();
        String couponName = null;
        if (order.getCouponId() != null) {
            Coupon coupon = couponQueryService.selectCoupon(order.getCouponId());
            couponName = coupon.getTitle();
        }
        UserInfoDto userInfoDto = userService.selectUserInfo(order.getUserId()).convert2Dto();
        return OrderDto.builder().id(order.getId()).orderedAt(order.getOrderedAt()).user(userInfoDto).totalAmount(order.getTotalPrice()).deliverPlace(
                deliverPlace.convert2Dto()).paymentWay(order.getPaymentWay()).productInfos(orderProductDtos).couponDiscount(
                order.getCouponDiscount()).usePoint(order.getUsePoint()).ordererName(order.getOrdererName()).ordererTel(
                order.getOrdererTel()).couponName(couponName).build();
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

    public void updateOrder(Orders order) {
        orderRepository.save(order);
    }

    public Page<Orders> selectOrderLitByAdmin(PageRequest pageRequest, Specification<Orders> spec) {
        return orderRepository.findAll(spec, pageRequest);
    }

    public Page<Orders> selectOrderList(Integer userId, PageRequest pageRequest) {
        Specification<Orders> spec = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.equal(root.get("userId"), userId));
            Join<Orders, OrderProductInfo> t = root.join("productInfos", JoinType.INNER);
            predicates.add(builder.isNotNull(t.get("id")));
            predicates.add(builder.or(builder.notEqual(t.get("state"), OrderProductState.WAIT_DEPOSIT),
                    builder.equal(root.get("paymentWay"), OrderPaymentWay.DEPOSIT)));
            return builder.and(predicates.toArray(new Predicate[0]));
        };
//        Page<Orders> orders = orderRepository.findAllByUserId(userId, pageRequest);
        return orderRepository.findAll(spec, pageRequest);
    }

    public List<Orders> selectCanceledOrderList(Integer userId) {
        Specification<Orders> spec = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.equal(root.get("userId"), userId));
            Join<Orders, OrderProductInfo> t = root.join("productInfos", JoinType.INNER);
            predicates.add(builder.and(t.get("state").in(OrderProductState.CANCELED,
                    OrderProductState.REFUND_DONE,
                    OrderProductState.CANCEL_REQUEST,
                    OrderProductState.REFUND_REQUEST,
                    OrderProductState.REFUND_ACCEPT)));
            predicates.add(builder.isNotNull(t.get("id")));
            return builder.and(predicates.toArray(new Predicate[0]));
        };
//        Page<Orders> orders = orderRepository.findAllByUserId(userId, pageRequest);
        return orderRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "id"));
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

    public void requestCancelOrderProduct(Integer orderProductInfoId) throws Exception {
        OrderProductInfo info = infoRepository.findById(orderProductInfoId).orElseThrow(() -> {
            try {
                throw new Exception("주문 상품 정보를 찾을 수 없습니다.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        switch (info.getState()) {
            case WAIT_DEPOSIT:
            case PAYMENT_DONE:
                cancelOrderedProduct(orderProductInfoId);
                break;
            case ON_DELIVERY:
            case DELIVERY_DONE:
            case EXCHANGE_REQUEST:
            case EXCHANGE_ACCEPT:
            case FINAL_CONFIRM:
            case REFUND_REQUEST:
            case REFUND_ACCEPT:
            case REFUND_DONE:
                throw new Exception("취소 불가능한 상태입니다.");
            case CANCEL_REQUEST:
                throw new Exception("이미 취소 요청된 상태입니다.");
            case CANCELED:
                throw new Exception("취소 완료된 상태입니다.");
            case DELIVERY_READY:
            default:
                info.setState(OrderProductState.CANCEL_REQUEST);
                updateOrderProductInfo(new ArrayList<>(List.of(info)));
        }
    }

    public Integer calculateCancelOrderPrice(Integer orderProductInfoId) {
        OrderProductInfo info = infoRepository.findById(orderProductInfoId).orElseThrow(() -> {
            try {
                throw new Exception("주문 상품 정보를 찾을 수 없습니다.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        Product product = productService.findById(info.getProductId());
        List<OrderProductInfo> infos = selectOrderProductInfoListWithOrderId(info.getOrderId()).stream().filter(v -> {
            Product p = productService.selectProduct(v.getProductId());
            return product.getStoreId() == p.getStoreId();
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

    public void cancelOrderedProduct(Integer orderProductInfoId) throws Exception {
        OrderProductInfo info = infoRepository.findById(orderProductInfoId).orElseThrow(() -> {
            throw new Error("주문 상품 정보를 찾을 수 없습니다.");
        });
        if (info.getState().equals(OrderProductState.CANCELED)) throw new Exception("이미 취소된 상품입니다.");
        OrderProductOption option = optionRepository.findFirstByOrderProductId(orderProductInfoId);
        Orders order = orderRepository.findById(info.getOrderId()).orElseThrow(() -> {
            try {
                throw new Exception("주문 정보를 찾을 수 없습니다.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        int
                deliveryFee =
                info.getState().equals(OrderProductState.WAIT_DEPOSIT) ||
                        info.getState().equals(OrderProductState.DELIVERY_READY) ||
                        info.getState().equals(OrderProductState.PAYMENT_DONE) ? info.getDeliveryFee() : 0;

//        Integer price = (info.getPrice() + (option != null ? option.getPrice() : 0)) * info.getAmount() + deliveryFee;
        Integer price = getCancelPrice(order, List.of(info));
        int taxFreeAmount = getTaxFreeAmount(order, List.of(info));
        paymentService.cancelPayment(order.getImpUid(), price, taxFreeAmount);
        info.setState(OrderProductState.CANCELED);
        infoRepository.save(info);
        Integer returnPoint = checkReturnPoint(order);
        returnCouponIfAllCanceled(order);
        if (returnPoint != null) returnPoint(order.getUserId(), returnPoint);
    }

    public int getCancelPrice(Orders order, List<OrderProductInfo> infos) throws Exception {
        List<OrderProductInfo> orderProductInfos = selectOrderProductInfoListWithOrderId(order.getId());
        int deliveryFee = infos.stream().mapToInt(info -> {
            if (info.getState().equals(OrderProductState.WAIT_DEPOSIT) ||
                    info.getState().equals(OrderProductState.DELIVERY_READY) ||
                    info.getState().equals(OrderProductState.PAYMENT_DONE)) return info.getDeliveryFee();
            else return 0;
        }).sum();
        int
                orderedPrice =
                orderProductInfos.stream().filter(v -> !v.getState().equals(OrderProductState.CANCELED)).mapToInt(
                        OrderProductInfo::getPrice).sum();
        AtomicInteger cancelPrice = new AtomicInteger();
        orderProductInfos.forEach(v -> {
            if (infos.stream().anyMatch(info -> info.getId() == v.getId())) {
                cancelPrice.addAndGet(v.getPrice());
                v.setState(OrderProductState.CANCELED);
            }
        });
//        System.out.println("orderPrice | " + orderedPrice);
//        System.out.println("deliveryFee | " + deliveryFee);
//        System.out.println("cancelPrice | " + cancelPrice.get());
        Coupon coupon = order.getCouponId() != null ? couponQueryService.selectCoupon(order.getCouponId()) : null;
        int couponDiscount = 0;
        if (coupon != null && coupon.getMinPrice() > orderedPrice - cancelPrice.get())
            couponDiscount = order.getCouponDiscount();
        Integer point = checkReturnPoint(order);
//        System.out.println("point | " + point);
//        System.out.println("couponDiscount | " + couponDiscount);
        return cancelPrice.get() + deliveryFee - couponDiscount - (point != null ? point : 0);
    }

    public Integer checkReturnPoint(Orders order) {
        List<OrderProductInfo> infos = infoRepository.findAllByOrderId(order.getId());
        if (infos.stream().allMatch(v -> v.getState().equals(OrderProductState.CANCELED))) return order.getUsePoint();
        else return null;
    }

    public void returnCouponIfAllCanceled(Orders order) {
        List<OrderProductInfo> infos = infoRepository.findAllByOrderId(order.getId());
        boolean
                allCanceled =
                order.getCouponId() != null &&
                        infos.stream().allMatch(v -> v.getState().equals(OrderProductState.CANCELED));
        if (allCanceled) couponCommandService.unUseCoupon(order.getCouponId(), order.getUserId());
    }

    public void returnPoint(Integer userId, Integer returnPoint) {
        UserInfo userInfo = userService.selectUserInfo(userId);
        userInfo.setPoint(userInfo.getPoint() + returnPoint);
        userService.updateUserInfo(userInfo);
    }

    public List<OrderProductInfo> selectOrderProductInfoWithState(List<OrderProductState> states) {
        return infoRepository.findAllByStateIn(states);
    }

    public Integer getProductPrice(Product product, Integer OptionItemId, Integer amount) {
        OptionItem optionItem = productService.selectOptionItem(OptionItemId);
        Integer totalPrice = optionItem.getDiscountPrice() * amount;
        return totalPrice;
    }

    public Integer getProductDeliveryFee(Product product, Integer OptionItemId, Integer amount) {
        OptionItem optionItem = productService.selectOptionItem(OptionItemId);
        if (product.getDeliverBoxPerAmount() != null && product.getDeliverBoxPerAmount() != 0) {
            return product.getDeliveryFee() * ((amount / product.getDeliverBoxPerAmount()) + 1);
        } else {
            StoreInfo storeInfo = storeService.selectStoreInfo(product.getStoreId());
            if (storeInfo.getDeliverFeeType().equals(StoreDeliverFeeType.FREE)) return 0;
            else if (storeInfo.getDeliverFeeType().equals(StoreDeliverFeeType.FIX)) return storeInfo.getDeliverFee();
            else return optionItem.getDiscountPrice() * amount >
                        storeInfo.getMinOrderPrice() ? 0 : storeInfo.getDeliverFee();
        }
    }

    public List<OrderProductInfo> selectOrderProductInfoWithIds(List<Integer> ids) {
        return infoRepository.findAllByIdIn(ids);
    }

    public Page<OrderProductInfo> selectOrderProductInfoList(Specification<OrderProductInfo> spec, Pageable pageable) {
        return infoRepository.findAll(spec, pageable);
    }

    public void updateOrderProductInfos(List<OrderProductInfo> infos) {
        infoRepository.saveAll(infos);
    }

    public OrderProductInfoDto convert2InfoDto(OrderProductInfo info) {
        Orders order = selectOrder(info.getOrderId());
        OrderDto orderDto = convert2Dto(order, null, null);
        orderDto.setProductInfos(null);
        Product product = productService.selectProduct(info.getProductId());
        StoreInfo storeInfo = storeService.selectStoreInfo(product.getStoreId());
        OptionItem optionItem = productService.selectOptionItem(info.getOptionItemId());
        Optional<DeliveryCompany>
                deliveryCompany =
                info.getDeliverCompanyCode() !=
                        null ? deliveryCompanyRepository.findById(info.getDeliverCompanyCode()) : Optional.empty();

        return OrderProductInfoDto.builder().id(info.getId()).orderId(info.getOrderId()).productId(info.getProductId()).optionItemId(
                info.getOptionItemId()).optionItem(optionItem.convert2Dto()).state(info.getState()).settlePrice(info.getSettlePrice()).price(
                info.getPrice()).amount(info.getAmount()).deliveryFee(info.getDeliveryFee()).cancelReasonContent(info.getCancelReasonContent()).cancelReason(
                info.getCancelReason()).deliverCompanyCode(info.getDeliverCompanyCode()).invoiceCode(info.getInvoiceCode()).isSettled(
                info.getIsSettled()).settledAt(info.getSettledAt()).product(productService.convert2ListDto(info.getProduct())).order(
                orderDto).settlementRate(storeInfo.getSettlementRate()).deliverFeeType(storeInfo.getDeliverFeeType()).deliverCompany(
                deliveryCompany.map(DeliveryCompany::getName).orElse(null)).build();
    }

    public Integer calculateTotalPrice(OrderReq data) {
        List<Product> products = new ArrayList<>();
        Integer totalPrice = data.getProducts().stream().mapToInt(v -> {
            Product product = productService.findById(v.getProductId());
            products.add(product);
            return getProductPrice(product, v.getOptionId(), v.getAmount());
        }).sum();

        Integer deliverFee = products.stream().map(Product::getStoreId).distinct().mapToInt(v -> {
            StoreInfo storeInfo = storeService.selectStoreInfo(v);
            Integer storePrice = products.stream().filter(p -> p.getStoreId() == v).mapToInt(v1 -> {
                for (OrderProductReq d : data.getProducts()) {
                    if (d.getProductId() == v1.getId()) {
                        Product product = productService.selectProduct(d.getProductId());
                        return getProductPrice(v1, d.getOptionId(), d.getAmount());
                    }
                }
                return 0;
            }).sum();
            storePrice += storeService.getDeliverFee(storeInfo, storePrice);
            return storePrice;
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

    public boolean checkProductCanDeliver(OrderDeliverPlace orderDeliverPlace, OrderProductInfo orderProductInfo) {
        List<String>
                difficultDeliverBcode =
                difficultDeliverAddressQueryService.selectDifficultDeliverAddressWithProductId(orderProductInfo.getProductId()).stream().map(
                        DifficultDeliverAddress::getBcode).toList();
        return difficultDeliverBcode.stream().noneMatch(v -> v.substring(0,
                5).equals(orderDeliverPlace.getBcode().substring(0, 5)));
    }

    public void processOrderZeroAmount(Orders order) {
        List<OrderProductInfo> infos = selectOrderProductInfoListWithOrderId(order.getId());
        infos.forEach(info -> {
            if (!checkProductCanDeliver(order.getDeliverPlace(), info)) {
                int cancelPrice = 0;
                try {
                    cancelPrice = getCancelPrice(order, List.of(info));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                info.setCancelReasonContent("배송 불가 지역");
                info.setCancelReason(OrderCancelReason.ORDER_FAULT);
                info.setState(OrderProductState.CANCELED);
                notificationCommandService.sendFcmToUser(order.getUserId(),
                        NotificationMessageType.ORDER_CANCEL,
                        NotificationMessage.builder().productName(info.getProduct().getTitle()).build());
            } else {
                OptionItem optionItem = productService.selectOptionItem(info.getOptionItemId());
                if (optionItem.getAmount() != null) optionItem.setAmount(optionItem.getAmount() - info.getAmount());
                productService.addOptionItem(optionItem);
                info.setState(OrderProductState.PAYMENT_DONE);
                notificationCommandService.sendFcmToUser(order.getUserId(),
                        NotificationMessageType.PAYMENT_DONE,
                        NotificationMessage.builder().productName(info.getProduct().getTitle()).build());
            }
        });
        order.setState(OrderState.PAYMENT_DONE);
        updateOrderProductInfo(infos);
        updateOrder(order);
        UserInfo userInfo = userService.selectUserInfo(order.getUserId());
        userInfo.setPoint(userInfo.getPoint() - order.getUsePoint());
        userService.updateUserInfo(userInfo);
        couponCommandService.useCoupon(order.getCouponId(), order.getUserId());
    }

    public int getTaxFreeAmount(Orders order, List<OrderProductInfo> infos) {
        infos = infos != null ? infos : selectOrderProductInfoListWithOrderId(order.getId());
        int taxFreeAmount = 0;
        int
                discountAmount =
                (order.getCouponDiscount() != null ? order.getCouponDiscount() : 0) +
                        (order.getUsePoint() != null ? order.getUsePoint() : 0);
        if (discountAmount != 0) {
            int totalOriginPrice = infos.stream().mapToInt(OrderProductInfo::getPrice).sum();
            List<Integer>
                    discountedPrices =
                    infos.stream().map(v -> (int) Math.round((v.getPrice() *
                            (v.getPrice() / (float) totalOriginPrice)) / 10.0) * 10).toList();
            if (discountedPrices.size() > 1) {
                int sum = discountedPrices.subList(0, discountedPrices.size() - 2).stream().mapToInt(v -> v).sum();
                discountedPrices.set(discountedPrices.size() - 1, totalOriginPrice - sum);
            }
            for (int i = 0; i < infos.size(); i++) {
                Product product = productService.selectProduct(infos.get(i).getProductId());
                if (!product.getNeedTaxation()) {
                    taxFreeAmount += discountedPrices.get(i);
                }
            }
            return taxFreeAmount;
        } else {
            taxFreeAmount = infos.stream().mapToInt(v -> {
                Product product = productService.selectProduct(v.getProductId());
                if (!product.getNeedTaxation()) {
                    return v.getPrice();
                }
                return 0;
            }).sum();
            return taxFreeAmount;
        }
    }
}

