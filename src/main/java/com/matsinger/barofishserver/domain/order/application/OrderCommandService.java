package com.matsinger.barofishserver.domain.order.application;

import com.matsinger.barofishserver.domain.basketProduct.application.BasketCommandService;
import com.matsinger.barofishserver.domain.coupon.application.CouponQueryService;
import com.matsinger.barofishserver.domain.coupon.application.CouponUserMapQueryService;
import com.matsinger.barofishserver.domain.coupon.domain.Coupon;
import com.matsinger.barofishserver.domain.notification.application.NotificationCommandService;
import com.matsinger.barofishserver.domain.order.domain.*;
import com.matsinger.barofishserver.domain.order.dto.OrderProductReq;
import com.matsinger.barofishserver.domain.order.dto.OrderReq;
import com.matsinger.barofishserver.domain.order.dto.VBankRefundInfo;
import com.matsinger.barofishserver.domain.order.orderprductinfo.application.OrderProductInfoCommandService;
import com.matsinger.barofishserver.domain.order.orderprductinfo.domain.OrderProductInfo;
import com.matsinger.barofishserver.domain.order.orderprductinfo.domain.OrderProductState;
import com.matsinger.barofishserver.domain.order.orderprductinfo.repository.OrderProductInfoRepository;
import com.matsinger.barofishserver.domain.order.repository.OrderRepository;
import com.matsinger.barofishserver.domain.payment.application.PaymentService;
import com.matsinger.barofishserver.domain.payment.dto.KeyInPaymentReq;
import com.matsinger.barofishserver.domain.product.application.ProductQueryService;
import com.matsinger.barofishserver.domain.product.difficultDeliverAddress.application.DifficultDeliverAddressQueryService;
import com.matsinger.barofishserver.domain.product.domain.Product;
import com.matsinger.barofishserver.domain.product.optionitem.application.OptionItemCommandService;
import com.matsinger.barofishserver.domain.product.optionitem.application.OptionItemQueryService;
import com.matsinger.barofishserver.domain.product.optionitem.domain.OptionItem;
import com.matsinger.barofishserver.domain.product.repository.ProductRepository;
import com.matsinger.barofishserver.domain.store.application.StoreInfoQueryService;
import com.matsinger.barofishserver.domain.store.domain.StoreInfo;
import com.matsinger.barofishserver.domain.user.deliverplace.DeliverPlace;
import com.matsinger.barofishserver.domain.user.deliverplace.application.DeliverPlaceQueryService;
import com.matsinger.barofishserver.domain.user.deliverplace.repository.DeliverPlaceRepository;
import com.matsinger.barofishserver.domain.user.paymentMethod.application.PaymentMethodCommandService;
import com.matsinger.barofishserver.domain.user.paymentMethod.application.PaymentMethodService;
import com.matsinger.barofishserver.domain.user.paymentMethod.domain.PaymentMethod;
import com.matsinger.barofishserver.domain.userinfo.application.UserInfoQueryService;
import com.matsinger.barofishserver.domain.userinfo.domain.UserInfo;
import com.matsinger.barofishserver.global.exception.BusinessException;
import com.matsinger.barofishserver.utils.Common;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderCommandService {

    private final OrderService orderService;
    private final Common utils;
    private final ProductQueryService productQueryService;
    private final BasketCommandService basketCommandService;
    private final UserInfoQueryService userInfoQueryService;
    private final OptionItemCommandService optionItemCommandService;
    private final BankCodeQueryService bankCodeQueryService;
    private final PaymentMethodService paymentMethodService;
    private final PaymentService paymentService;
    private final OrderRepository orderRepository;
    private final OrderDeliverPlaceCommandService orderDeliverPlaceCommandService;
    private final OrderProductInfoCommandService orderProductInfoCommandService;
    private final CouponQueryService couponQueryService;
    private final StoreInfoQueryService storeInfoQueryService;
    private final ProductRepository productRepository;
    private final PaymentMethodCommandService paymentMethodCommandService;
    private final OptionItemQueryService optionItemQueryService;
    private final NotificationCommandService notificationCommandService;
    private final OrderQueryService orderQueryService;
    private final CouponUserMapQueryService couponUserMapQueryService;
    private final DeliverPlaceRepository deliverPlaceRepository;
    private final DifficultDeliverAddressQueryService difficultDeliverAddressQueryService;
    private final DeliverPlaceQueryService deliverPlaceQueryService;
    private final OrderProductInfoRepository orderProductInfoRepository;

    private void validateRequestInfo(OrderReq request) {
        utils.validateString(request.getName(), 20L, "주문자 이름");
        utils.validateString(request.getTel(), 11L, "주문자 연락처");
    }

    @Nullable
    private VBankRefundInfo validateVbankRefundInfo(OrderReq request) {
        VBankRefundInfo vBankRefundInfo = null;
        if (request.getPaymentWay().equals(OrderPaymentWay.VIRTUAL_ACCOUNT)) {
            if (request.getVbankRefundInfo() == null)
                throw new BusinessException("가상계좌 환불 정보를 입력해주세요.");

            BankCode bankCode = bankCodeQueryService.findByBankCode(request.getVbankRefundInfo().getBankCodeId());
            String bankHolder = utils.validateString(request.getVbankRefundInfo().getBankHolder(), 20L, "환불 예금주명");
            String bankAccount = request.getVbankRefundInfo().getBankAccount().replaceAll("-", "");
            bankAccount = utils.validateString(bankAccount, 30L, "환불 계좌번호");
            vBankRefundInfo = VBankRefundInfo.builder()
                    .bankHolder(bankHolder)
                    .bankCode(bankCode.getCode())
                    .bankName(bankCode.getName())
                    .bankAccount(bankAccount)
                    .build();
        }
        return vBankRefundInfo;
    }

    private void validateProductStatesAndDeleteBasket(Integer userId, Product findedProduct) {
        if (findedProduct.isPromotionEnd()) {
            basketCommandService.deleteBasket(findedProduct.getId(), userId);
            throw new BusinessException("프로모션 기간이 아닌 상품이 포함되어 있습니다.");
        }
        findedProduct.validateState();
    }

    @Transactional
    public void proceedOrder(OrderReq request, Integer userId) {
        if (request.getTotalPrice() == 0) {
            throw new RuntimeException("주문 에러 발생");
        }
        UserInfo userInfo = userInfoQueryService.findByUserId(userId);
        String orderId = orderQueryService.getOrderId();
        DeliverPlace deliverPlace = deliverPlaceQueryService.findById(request.getDeliverPlaceId());
        OrderDeliverPlace orderDeliverPlace = deliverPlace.toOrderDeliverPlace(orderId);

        Map<StoreInfo, List<OrderProductInfo>> storeMap = createStoreMap(request, userInfo, orderId);

        int totalOrderDeliveryFee = 0;
        int totalOrderProductPrice = 0;
        int totalTaxFreePrice = 0;
        for (StoreInfo storeInfo : storeMap.keySet()) {

            List<OrderProductInfo> storeOrderProductInfos = storeMap.get(storeInfo);
            validateDifficultDeliveryRegion(orderDeliverPlace, storeOrderProductInfos);

            calculateDeliveryFee(storeInfo, storeOrderProductInfos);

            totalOrderProductPrice += storeOrderProductInfos.stream()
                    .mapToInt(v -> v.getTotalProductPrice())
                    .sum();
            totalOrderDeliveryFee += storeOrderProductInfos.stream()
                    .mapToInt(v -> v.getDeliveryFee())
                    .sum();
            totalTaxFreePrice += storeOrderProductInfos.stream()
                    .mapToInt(v -> v.getTaxFreeAmount()).sum();
        }

        int totalOrderPriceMinusDeliveryFee = totalOrderProductPrice - totalOrderDeliveryFee;
        int totalOrderPriceContainsDeliveryFee = totalOrderProductPrice + totalOrderDeliveryFee;

        validateCouponAndPoint(request, totalOrderPriceMinusDeliveryFee, userInfo);
        Integer finalOrderPrice = validateFinalPrice(request, totalOrderPriceContainsDeliveryFee);

        Orders order = Orders.builder()
                .id(orderId)
                .userId(userId)
                .paymentWay(request.getPaymentWay())
                .state(OrderState.WAIT_DEPOSIT)
                .couponId(request.getCouponId() != null ? request.getCouponId() : null)
                .orderedAt(utils.now())
                .totalPrice(finalOrderPrice)
                .usePoint(request.getPoint())
                .couponDiscount(request.getCouponDiscountPrice())
                .ordererName(request.getName())
                .ordererTel(request.getTel())
                .originTotalPrice(totalOrderProductPrice)
                .build();

        setVbankInfo(request, order);
        processKeyInPayment(request, orderId, totalTaxFreePrice);

        save(storeMap, order);
    }

    private void save(Map<StoreInfo, List<OrderProductInfo>> storeMap, Orders order) {
        orderRepository.save(order);

        List<OrderProductInfo> orderProductInfos =
                storeMap.values()
                        .stream().flatMap(Collection::stream)
                        .collect(Collectors.toList());
        orderProductInfoRepository.saveAll(orderProductInfos);
    }

    private void validateCouponAndPoint(OrderReq request, int totalOrderPriceMinusDeliveryFee, UserInfo userInfo) {
        if (request.getCouponId() != null) {
            Coupon coupon = couponQueryService.findById(request.getCouponId());
            coupon.isAvailable(totalOrderPriceMinusDeliveryFee);
        }
        userInfo.validatePoint(request.getPoint());
    }

    private void processKeyInPayment(OrderReq request, String orderId, int totalTaxFreePrice) {
        if (request.getPaymentWay().equals(OrderPaymentWay.KEY_IN)) {
            PaymentMethod paymentMethod = paymentMethodService.selectPaymentMethod(request.getPaymentMethodId());
            Product product = productQueryService.findById(request.getProducts().get(0).getProductId());
            KeyInPaymentReq
                    req =
                    KeyInPaymentReq.builder()
                            .paymentMethod(paymentMethod)
                            .order_name(request.getName())
                            .orderId(orderId)
                            .total_amount(request.getTotalPrice())
                            .order_name(product.getTitle())
                            .taxFree(totalTaxFreePrice)
                            .build();
            try {
                Boolean keyInResult = paymentService.processKeyInPayment(req);
                if (!keyInResult) {
                    throw new BusinessException("결제에 실패하였습니다.");
                }
            } catch (Exception e) {
                throw new BusinessException("결제에 실패하였습니다.");
            }
        }
    }

    private void calculateDeliveryFee(StoreInfo storeInfo, List<OrderProductInfo> orderProductInfos) {
        orderProductInfos.forEach(v -> v.setDeliveryFee(0)); // 배송비 초기화
        if (storeInfo.isConditional()) {
            setSConditionalDeliveryFee(storeInfo, orderProductInfos);
        }

        if (!storeInfo.isConditional()) {
            setPConditionalDeliveryFee(orderProductInfos);
        }
    }

    private void setSConditionalDeliveryFee(StoreInfo storeInfo, List<OrderProductInfo> orderProductInfos) {
        int totalStoreProductPrice = orderProductInfos.stream().mapToInt(v -> v.getTotalProductPrice()).sum();
        if (storeInfo.meetConditions(totalStoreProductPrice)) {
        }
        if (!storeInfo.meetConditions(totalStoreProductPrice)) {
            int maxProductPrice = orderProductInfos.stream().mapToInt(v -> v.getTotalProductPrice()).max().getAsInt();
            OrderProductInfo maxPriceOrderProduct = orderProductInfos.stream()
                    .filter(v -> v.getTotalProductPrice() == maxProductPrice).findFirst().get();
            maxPriceOrderProduct.setDeliveryFee(storeInfo.getDeliveryFee());
        }
    }

    private void setPConditionalDeliveryFee(List<OrderProductInfo> orderProductInfos) {
        int[] productIds = orderProductInfos.stream()
                .mapToInt(v -> v.getProductId()).distinct().toArray();

        for (Integer productId : productIds) {
            Product product = productQueryService.findById(productId);

            if (product.isDeliveryTypeFree()) {
            }
            if (product.isDeliveryTypeFreeIfOver()) {
                setIfOverDeliveryFee(product, orderProductInfos);
            }
        }
    }

    private void setIfOverDeliveryFee(Product targetProduct, List<OrderProductInfo> orderProductInfos) {
        List<OrderProductInfo> targetProductInfos = orderProductInfos.stream()
                .filter(v -> v.getProductId() == targetProduct.getId()).toList();
        int totalPrice = targetProductInfos.stream()
                .mapToInt(v -> v.getTotalProductPrice()).sum();
        if (targetProduct.meetConditions(totalPrice)) {
        }
        if (!targetProduct.meetConditions(totalPrice)) {
            int maxProductPrice = targetProductInfos.stream()
                    .mapToInt(v -> v.getTotalProductPrice()).max().getAsInt();
            OrderProductInfo maxPriceOrderProduct = targetProductInfos.stream()
                    .filter(v -> v.getTotalProductPrice() == maxProductPrice)
                    .findFirst().get();

            int[] targetProductIds = targetProductInfos.stream().mapToInt(v -> v.getProductId()).toArray();
            int maxDeliveryFee = 0;
            for (int targetProductId : targetProductIds) {
                Product product = productQueryService.findById(targetProductId);
                if (product.getDeliverFee() > maxDeliveryFee) {
                    maxDeliveryFee = product.getDeliverFee();
                }
            }
            maxPriceOrderProduct.setDeliveryFee(maxDeliveryFee);
        }
    }

    public void setVbankInfo(OrderReq request, Orders orders) {
        if (request.getPaymentWay().equals(OrderPaymentWay.VIRTUAL_ACCOUNT)) {
            if (request.getVbankRefundInfo() == null)
                throw new BusinessException("가상계좌 환불 정보를 입력해주세요.");
            if (request.getVbankRefundInfo().getBankCodeId() == null)
                throw new BusinessException("은행 코드 아이디를 입력해주세요.");
            BankCode bankCode = orderService.selectBankCode(request.getVbankRefundInfo().getBankCodeId());
            String bankHolder = utils.validateString(request.getVbankRefundInfo().getBankHolder(), 20L, "환불 예금주명");
            String bankAccount = request.getVbankRefundInfo().getBankAccount().replaceAll("-", "");
            bankAccount = utils.validateString(bankAccount, 30L, "환불 계좌번호");

            orders.setVbankRefundInfo(bankCode.getCode(), bankHolder, bankCode.getName(), bankAccount);
        }
    }

    private void validateDifficultDeliveryRegion(OrderDeliverPlace orderDeliverPlace, List<OrderProductInfo> orderProductInfos) {
        int[] uniqueProductIds = orderProductInfos.stream()
                .mapToInt(v -> v.getProductId())
                .distinct().toArray();
        for (int productId : uniqueProductIds) {
            difficultDeliverAddressQueryService
                    .validateDifficultDeliveryRegion(productId, orderDeliverPlace);
        }
    }

    private Integer validateFinalPrice(OrderReq request, int totalOrderPriceContainsDeliveryFee) {
        int finalOrderPrice = totalOrderPriceContainsDeliveryFee - request.getCouponDiscountPrice() - request.getPoint();
        if (finalOrderPrice != request.getTotalPrice()) {
            throw new BusinessException("총 금액을 확인해주세요.");
        }
        return finalOrderPrice;
    }



    private Map<StoreInfo, List<OrderProductInfo>> createStoreMap(OrderReq request,
                                                                  UserInfo userInfo,
                                                                  String orderId) {
        Map<StoreInfo, List<OrderProductInfo>> storeMap = new HashMap<>();
        for (OrderProductReq orderProductReq : request.getProducts()) {
            Product findedProduct = productQueryService.findById(orderProductReq.getProductId());
            OptionItem optionItem = optionItemQueryService.findById(orderProductReq.getOptionId());

            validateProductStatesAndDeleteBasket(userInfo.getUserId(), findedProduct);
            createStoreMap(orderId, orderProductReq, optionItem, findedProduct, storeMap);
        }
        return storeMap;
    }

    private void createStoreMap(String orderId, OrderProductReq orderProductReq, OptionItem optionItem, Product findedProduct, Map<StoreInfo, List<OrderProductInfo>> storeMap) {
        int totalProductPrice = optionItem.getDiscountPrice() * orderProductReq.getAmount();
        OrderProductInfo orderProductInfo = OrderProductInfo.builder()
                .orderId(orderId)
                .productId(findedProduct.getId())
                .optionItemId(orderProductReq.getOptionId())
                .settlePrice(optionItem.getPurchasePrice())
                .originPrice(optionItem.getDiscountPrice())
                .price(totalProductPrice)
                .amount(orderProductReq.getAmount())
                .deliveryFeeType(findedProduct.getDeliverFeeType())
                .isSettled(false)
                .isTaxFree(!findedProduct.getNeedTaxation())
                .taxFreeAmount(findedProduct.getNeedTaxation() == false ? totalProductPrice : 0)
                .state(OrderProductState.WAIT_DEPOSIT)
                .build();


        StoreInfo storeInfo = storeInfoQueryService.findByStoreId(findedProduct.getStoreId());
        List<OrderProductInfo> orderProductInfos = storeMap.getOrDefault(storeInfo, new ArrayList<>());
        orderProductInfos.add(orderProductInfo);
        storeMap.put(storeInfo, orderProductInfos);
    }
}
