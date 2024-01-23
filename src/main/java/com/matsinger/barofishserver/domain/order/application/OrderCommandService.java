package com.matsinger.barofishserver.domain.order.application;

import com.matsinger.barofishserver.domain.basketProduct.application.BasketCommandService;
import com.matsinger.barofishserver.domain.coupon.application.CouponCommandService;
import com.matsinger.barofishserver.domain.coupon.application.CouponQueryService;
import com.matsinger.barofishserver.domain.coupon.domain.Coupon;
import com.matsinger.barofishserver.domain.notification.application.NotificationCommandService;
import com.matsinger.barofishserver.domain.notification.dto.NotificationMessage;
import com.matsinger.barofishserver.domain.notification.dto.NotificationMessageType;
import com.matsinger.barofishserver.domain.order.domain.*;
import com.matsinger.barofishserver.domain.order.dto.*;
import com.matsinger.barofishserver.domain.order.orderprductinfo.application.OrderProductInfoQueryService;
import com.matsinger.barofishserver.domain.order.orderprductinfo.domain.OrderProductInfo;
import com.matsinger.barofishserver.domain.order.orderprductinfo.domain.OrderProductState;
import com.matsinger.barofishserver.domain.order.orderprductinfo.repository.OrderProductInfoRepository;
import com.matsinger.barofishserver.domain.order.repository.OrderDeliverPlaceRepository;
import com.matsinger.barofishserver.domain.order.repository.OrderRepository;
import com.matsinger.barofishserver.domain.payment.application.PaymentService;
import com.matsinger.barofishserver.domain.payment.domain.PaymentState;
import com.matsinger.barofishserver.domain.payment.domain.Payments;
import com.matsinger.barofishserver.domain.payment.dto.CancelManager;
import com.matsinger.barofishserver.domain.payment.dto.KeyInPaymentReq;
import com.matsinger.barofishserver.domain.payment.portone.application.PortOneCallbackService;
import com.matsinger.barofishserver.domain.payment.repository.PaymentRepository;
import com.matsinger.barofishserver.domain.product.application.ProductQueryService;
import com.matsinger.barofishserver.domain.product.difficultDeliverAddress.application.DifficultDeliverAddressQueryService;
import com.matsinger.barofishserver.domain.product.domain.Product;
import com.matsinger.barofishserver.domain.product.optionitem.application.OptionItemQueryService;
import com.matsinger.barofishserver.domain.product.optionitem.domain.OptionItem;
import com.matsinger.barofishserver.domain.store.application.StoreInfoQueryService;
import com.matsinger.barofishserver.domain.store.domain.StoreInfo;
import com.matsinger.barofishserver.domain.user.deliverplace.DeliverPlace;
import com.matsinger.barofishserver.domain.user.deliverplace.application.DeliverPlaceQueryService;
import com.matsinger.barofishserver.domain.user.paymentMethod.application.PaymentMethodService;
import com.matsinger.barofishserver.domain.user.paymentMethod.domain.PaymentMethod;
import com.matsinger.barofishserver.domain.userinfo.application.UserInfoQueryService;
import com.matsinger.barofishserver.domain.userinfo.domain.UserInfo;
import com.matsinger.barofishserver.domain.userinfo.repository.UserInfoRepository;
import com.matsinger.barofishserver.global.exception.BusinessException;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.utils.Common;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderCommandService {

    private final OrderService orderService;
    private final Common utils;
    private final ProductQueryService productQueryService;
    private final BasketCommandService basketCommandService;
    private final UserInfoQueryService userInfoQueryService;
    private final PaymentMethodService paymentMethodService;
    private final PaymentService paymentService;
    private final OrderRepository orderRepository;
    private final CouponQueryService couponQueryService;
    private final StoreInfoQueryService storeInfoQueryService;
    private final OptionItemQueryService optionItemQueryService;
    private final NotificationCommandService notificationCommandService;
    private final OrderQueryService orderQueryService;
    private final DifficultDeliverAddressQueryService difficultDeliverAddressQueryService;
    private final DeliverPlaceQueryService deliverPlaceQueryService;
    private final OrderProductInfoRepository orderProductInfoRepository;
    private final OrderDeliverPlaceRepository orderDeliverPlaceRepository;
    private final OrderProductInfoQueryService orderProductInfoQueryService;
    private final CouponCommandService couponCommandService;
    private final PortOneCallbackService callbackService;
    private final UserInfoRepository userInfoRepository;
    private final PaymentRepository paymentRepository;

    @Transactional
    public OrderResponse proceedOrder(OrderReq request, Integer userId) {
        if (request.getTotalPrice() == 0) {
            throw new RuntimeException("주문 에러 발생");
        }
        UserInfo userInfo = userInfoQueryService.findByUserId(userId);
        String orderId = orderQueryService.getOrderId();
        DeliverPlace deliverPlace = deliverPlaceQueryService.findById(request.getDeliverPlaceId());
        OrderDeliverPlace orderDeliverPlace = deliverPlace.toOrderDeliverPlace(orderId);
        if (orderDeliverPlace.getBcode().length() < 5) {
            throw new BusinessException("배송지에서 법정동코드가 누락되었습니다." + "\n" + "동일한 주소로 다시 배송지를 설정해주세요.");
        }

        Map<StoreInfo, List<OrderProductInfo>> storeMap = createStoreMap(request, userInfo, orderId);

        log.info("orderId = {}", orderId);
        log.info("totalOrderPrice = {}", request.getTotalPrice());

        int totalOrderDeliveryFee = 0;
        int totalOrderProductPrice = 0;
        int totalTaxFreePrice = 0;
        List<Integer> cannotDeliverProductIds = new ArrayList<>();
        boolean notIncludesCannotDeliverPlace = true;
        log.info("orderDeliverPlaceBcode = {}", orderDeliverPlace.getBcode());
        for (StoreInfo storeInfo : storeMap.keySet()) {

            List<OrderProductInfo> storeOrderProductInfos = storeMap.get(storeInfo);
            boolean canDeliver = validateDifficultDeliveryRegion(orderDeliverPlace, storeOrderProductInfos);
            log.info("canDeliver = {}", canDeliver);
            log.info("notIncludesCannotDeliverPlace = {}", notIncludesCannotDeliverPlace);
            if (!canDeliver) {
                cannotDeliverProductIds.addAll(
                        storeOrderProductInfos.stream()
                                .filter(v -> v.getState() == OrderProductState.DELIVERY_DIFFICULT)
                                .map(v -> v.getProductId()).distinct()
                                .toList()
                );
                notIncludesCannotDeliverPlace = false;
            }
            calculateDeliveryFee(storeInfo, storeOrderProductInfos);

            totalOrderProductPrice += storeOrderProductInfos.stream()
                    .mapToInt(v -> v.getTotalProductPrice())
                    .sum();
            totalOrderDeliveryFee += storeOrderProductInfos.stream()
                    .mapToInt(v -> v.getDeliveryFee())
                    .max().getAsInt();
            totalTaxFreePrice += storeOrderProductInfos.stream()
                    .mapToInt(v -> v.getTaxFreeAmount()).sum();
        }

        int totalOrderPriceMinusDeliveryFee = totalOrderProductPrice - totalOrderDeliveryFee;
        log.info("totalOrderProductPrice = {}", totalOrderProductPrice);
        int totalOrderPriceContainsDeliveryFee = totalOrderProductPrice + totalOrderDeliveryFee;

        validateCouponAndPoint(request, totalOrderProductPrice, userInfo);
        Integer finalOrderPrice = validateFinalPrice(request, totalOrderPriceContainsDeliveryFee);

        Orders order = Orders.builder()
                .id(orderId)
                .userId(userId)
                .paymentWay(request.getPaymentWay())
                .state(notIncludesCannotDeliverPlace ? OrderState.WAIT_DEPOSIT : OrderState.DELIVERY_DIFFICULT)
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

        if (notIncludesCannotDeliverPlace) {
            processKeyInPayment(request, orderId, totalTaxFreePrice);
        }
        if (!notIncludesCannotDeliverPlace) {
            ArrayList<OrderProductInfo> allOrderProduct = new ArrayList<>();
            for (StoreInfo storeInfo : storeMap.keySet()) {
                allOrderProduct.addAll(storeMap.get(storeInfo));
            }
//            log.info("cannotDeliverProductIds = {}", cannotDeliverProductIds.toString());
            allOrderProduct.stream()
                    .filter(v -> !cannotDeliverProductIds.contains(v.getProductId()))
                    .forEach(v -> v.setState(OrderProductState.CANCELED));
        }

        save(storeMap, order, orderDeliverPlace);

        log.info("notIncludesCannotDeliverPlace = {}", notIncludesCannotDeliverPlace);

        return OrderResponse.builder()
                .orderId(orderId)
                .canDeliver(notIncludesCannotDeliverPlace)
                .cannotDeliverProductIds(cannotDeliverProductIds)
                .build();
    }

    private void calculateDeliveryFee(StoreInfo storeInfo, List<OrderProductInfo> sameStoreProducts) {
        sameStoreProducts.forEach(v -> v.setDeliveryFee(0)); // 배송비 초기화
        if (storeInfo.isConditional()) {
            setSConditionalDeliveryFee(storeInfo, sameStoreProducts);
        }

        if (!storeInfo.isConditional()) {
            setPConditionalDeliveryFee(sameStoreProducts);
        }
    }

    private void setSConditionalDeliveryFee(StoreInfo storeInfo, List<OrderProductInfo> sameStoreProducts) {
        int totalStoreProductPrice = sameStoreProducts.stream().mapToInt(v -> v.getTotalProductPrice()).sum();
        if (storeInfo.meetConditions(totalStoreProductPrice)) {
        }
        if (!storeInfo.meetConditions(totalStoreProductPrice)) {
            setDeliveryFeeToMostExpensiveProduct(sameStoreProducts);
        }
    }

    private void setPConditionalDeliveryFee(List<OrderProductInfo> sameStoreProducts) {
        int[] productIds = sameStoreProducts.stream()
                .mapToInt(v -> v.getProductId()).distinct().toArray();

        List<OrderProductInfo> deliveryFeeCalculatingGroup = addProductsNeedToCalculateDeliveryFee(sameStoreProducts, productIds);
        setDeliveryFeeToMostExpensiveProduct(deliveryFeeCalculatingGroup);
    }

    private List<OrderProductInfo> addProductsNeedToCalculateDeliveryFee(List<OrderProductInfo> sameStoreProducts,
                                                                         int[] productIds) {
        List<OrderProductInfo> deliveryFeeCalculatingGroup = new ArrayList<>();
        for (Integer productId : productIds) {
            Product product = productQueryService.findById(productId);
            List<OrderProductInfo> pConditionProduct = sameStoreProducts.stream()
                    .filter(v -> v.getProductId() == product.getId()).toList();

            if (product.isDeliveryTypeFree()) {
            }
            if (product.isDeliveryTypeFix()) {
                deliveryFeeCalculatingGroup.addAll(pConditionProduct);
            }
            if (product.isDeliveryTypeFreeIfOver()) {
                int totalPrice = pConditionProduct.stream()
                        .mapToInt(v -> v.getTotalProductPrice()).sum();
                if (!product.meetConditions(totalPrice)) {
                    deliveryFeeCalculatingGroup.addAll(pConditionProduct);
                }
            }
        }
        return deliveryFeeCalculatingGroup;
    }

    private Integer setDeliveryFeeToMostExpensiveProduct(List<OrderProductInfo> targetProductInfos) {
        // 계산할 배송비가 없으면 그냥 return
        if (targetProductInfos.isEmpty()) {
            return 0;
        }

        targetProductInfos.forEach(v -> v.setDeliveryFee(0));
        int highestPrice = targetProductInfos.stream()
                .mapToInt(v -> v.getTotalProductPrice()).max().getAsInt();
        OrderProductInfo maxPriceOrderProduct = targetProductInfos.stream()
                .filter(v -> v.getTotalProductPrice() == highestPrice)
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
        return maxDeliveryFee;
    }

    private String save(Map<StoreInfo, List<OrderProductInfo>> storeMap, Orders order, OrderDeliverPlace orderDeliverPlace) {
        Orders savedOrder = orderRepository.save(order);

        List<OrderProductInfo> orderProductInfos =
                storeMap.values()
                        .stream().flatMap(Collection::stream)
                        .collect(Collectors.toList());
        orderProductInfoRepository.saveAll(orderProductInfos);
        orderDeliverPlaceRepository.save(orderDeliverPlace);
        return savedOrder.getId();
    }

    private void validateProductStates(Integer userId, Product findedProduct) {
        if (findedProduct.isPromotionEnd()) {
            basketCommandService.deleteBasket(findedProduct.getId(), userId);
            throw new BusinessException("프로모션 기간이 아닌 상품이 포함되어 있습니다.");
        }
        findedProduct.validateState();
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

    private boolean validateDifficultDeliveryRegion(OrderDeliverPlace orderDeliverPlace, List<OrderProductInfo> orderProductInfos) {
        int[] uniqueProductIds = orderProductInfos.stream()
                .mapToInt(v -> v.getProductId())
                .distinct().toArray();
        boolean canDeliver = true;
        for (int productId : uniqueProductIds) {
            canDeliver = difficultDeliverAddressQueryService
                    .canDeliver(productId, orderDeliverPlace);

            if (!canDeliver) {
                orderProductInfos.stream()
                        .filter(v -> v.getProductId() == productId)
                        .forEach(v -> v.setState(OrderProductState.DELIVERY_DIFFICULT));
            }
        }
        return canDeliver;
    }

    private Integer validateFinalPrice(OrderReq request, int totalOrderPriceContainsDeliveryFee) {
        int finalOrderPrice = totalOrderPriceContainsDeliveryFee - request.getCouponDiscountPrice() - request.getPoint();
        if (finalOrderPrice != request.getTotalPrice()) {
            log.info("finalOrderPrice = {}", finalOrderPrice);
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

            validateProductStates(userInfo.getUserId(), findedProduct);
            createStoreMap(orderId, orderProductReq, optionItem, findedProduct, storeMap);
        }
        return storeMap;
    }

    private void createStoreMap(String orderId, OrderProductReq orderProductReq, OptionItem optionItem, Product product, Map<StoreInfo, List<OrderProductInfo>> storeMap) {
        int totalProductPrice = optionItem.getDiscountPrice() * orderProductReq.getAmount();
        OrderProductInfo orderProductInfo = OrderProductInfo.builder()
                .orderId(orderId)
                .productId(product.getId())
                .storeId(product.getStoreId())
                .optionItemId(orderProductReq.getOptionId())
                .settlePrice(optionItem.getPurchasePrice())
                .originPrice(optionItem.getDiscountPrice())
                .price(totalProductPrice)
                .amount(orderProductReq.getAmount())
                .deliveryFeeType(product.getDeliverFeeType())
                .isSettled(false)
                .isTaxFree(!product.getNeedTaxation())
                .taxFreeAmount(!product.getNeedTaxation() ? totalProductPrice : 0)
                .state(OrderProductState.WAIT_DEPOSIT)
                .build();


        StoreInfo storeInfo = storeInfoQueryService.findByStoreId(product.getStoreId());
        List<OrderProductInfo> orderProductInfos = storeMap.getOrDefault(storeInfo, new ArrayList<>());
        orderProductInfos.add(orderProductInfo);
        storeMap.put(storeInfo, orderProductInfos);
    }

    @Transactional
    public void cancelOrder(TokenInfo tokenInfo, List<Integer> orderProductInfoIds, RequestCancelReq request) {

        List<OrderProductInfo> cancelRequested = orderProductInfoRepository.findAllById(orderProductInfoIds);
        List<Integer> uniqueStoreIds = cancelRequested.stream()
                .map(v -> v.getStoreId())
                .distinct()
                .toList();

        List<OrderProductInfo> storeOrderProducts = cancelRequested.stream()
                .filter(v -> uniqueStoreIds.contains(v.getStoreId()))
                .toList();

        if (tokenInfo.getType().equals(TokenAuthType.PARTNER)) {
            if (uniqueStoreIds.size() > 1) {
                throw new BusinessException("타 파트너사의 주문과 같이 있어 취소 불가합니다.");
            }
        }

        OrderProductInfo orderProductInfo = cancelRequested.get(0);
        Orders order = orderProductInfo.getOrder();

        TokenAuthType authType = tokenInfo.getType();

        if (authType.equals(TokenAuthType.USER)) {
            validateRequest(tokenInfo.getId(), request, order);
        }

        List<OrderProductInfo> allOrderProducts = orderProductInfoRepository.findAllByOrderId(order.getId());

        log.info("isCouponUsed = {}", order.isCouponUsed());
        int seq = 1;
        String firstProductTitle = null;
        for (OrderProductInfo cancelRequestedProduct : storeOrderProducts) {
            if (order.isCouponUsed() || order.getState().equals(OrderState.WAIT_DEPOSIT)) {
                log.info("couponUsedScope");
                CancelManager cancelManager = new CancelManager(
                        order, allOrderProducts, List.of());
                cancel(order, cancelManager, request, authType);
                break;
            }

            Product product = productQueryService.findById(cancelRequestedProduct.getProductId());
            if (seq == 1) {
                firstProductTitle = product.getTitle();
            }
            StoreInfo storeInfo = storeInfoQueryService.findByStoreId(product.getStoreId());

            if (!order.isCouponUsed()) {
                log.info("couponNotUsedScope");
                List<OrderProductInfo> tobeCanceled = allOrderProducts.stream()
                        .filter(v -> v.getState() != OrderProductState.CANCELED)
                        .filter(v -> v.getStoreId() == storeInfo.getStoreId())
                        .toList();
                List<OrderProductInfo> notTobeCanceled = allOrderProducts.stream()
                        .filter(v -> v.getState() != OrderProductState.CANCELED)
                        .filter(v -> v.getStoreId() != storeInfo.getStoreId())
                        .toList();

                CancelManager cancelManager = new CancelManager(
                        order, tobeCanceled, notTobeCanceled);

                cancel(order, cancelManager, request, authType);
            }
            seq++;
        }

        notificationCommandService.sendFcmToUser(
                order.getUserId(),
                convertType(authType),
                NotificationMessage.builder()
                        .productName(firstProductTitle)
                        .isCanceledByRegion(false)
                        .build()
        );
    }

    private NotificationMessageType convertType(TokenAuthType authType) {
        if (authType.equals(TokenAuthType.USER)) {
            return NotificationMessageType.ORDER_CANCEL;
        }
        if (authType.equals(TokenAuthType.PARTNER)) {
            return NotificationMessageType.CANCELED_BY_PARTNER;
        }
        if (authType.equals(TokenAuthType.ADMIN)) {
            return NotificationMessageType.CANCELED_BY_ADMIN;
        }
        throw new BusinessException("토큰 타입이 유효하지 않습니다." + "\n" + "다시 로그인해 주세요.");
    }

    private void cancel(Orders order,
                        CancelManager cancelManager,
                        RequestCancelReq request,
                        TokenAuthType authType) {
        OrderProductState state = null;
        if (authType.equals(TokenAuthType.PARTNER)) {
            state = OrderProductState.CANCELED_BY_PARTNER;
        }
        if (authType.equals(TokenAuthType.ADMIN)) {
            state = OrderProductState.CANCELED_BY_ADMIN;
        }
        if (authType.equals(TokenAuthType.USER)) {
            state = OrderProductState.CANCELED;
        }

        Integer cancelPrice = null;
        if (order.getState().equals(OrderState.WAIT_DEPOSIT)) {
            List<OrderProductInfo> orderProductInfos = orderProductInfoQueryService.findAllByOrderId(order.getId());
            OrderProductState finalState = state;
            orderProductInfos.forEach(v -> v.setState(finalState));
            orderProductInfoRepository.saveAll(cancelManager.getAllOrderProducts());
            orderRepository.save(order);
            return;
        }
        if (cancelManager.allCanceled()) {
            cancelPrice = cancelManager.getAllCancelPrice();
        }
        if (!cancelManager.allCanceled()) {
            cancelPrice = cancelManager.getPartialCancelPrice();
        }
        log.info("isAllCanceled = {}", cancelManager.allCanceled());
        log.info("cancelPrice send to portOne = {}", cancelPrice);
        CancelData cancelData = new CancelData(
                order.getImpUid(),
                true,
                BigDecimal.valueOf(cancelPrice)
        );
//        log.info("impUid = {}", order.getImpUid());
//        log.info("totalCancelPrice = {}", cancelPrice);
//        log.info("taxFreePrice = {}", cancelManager.getNonTaxablePriceTobeCanceled());
        cancelData.setTax_free(BigDecimal.valueOf(cancelManager.getNonTaxablePriceTobeCanceled()));
        setVbankRefundInfo(order, cancelData);
        sendPortOneCancelData(cancelData);

        if (cancelManager.allCanceled()) {
            Payments payment = paymentRepository.findFirstByImpUid(order.getImpUid());
            payment.setStatus(PaymentState.CANCELED);
            order.setState(OrderState.CANCELED);

            couponCommandService.unUseCoupon(order.getCouponId(), order.getUserId());
            UserInfo userInfo = userInfoQueryService.findByUserId(order.getUserId());
            userInfo.addPoint(order.getUsedPoint());

            paymentRepository.save(payment);
            userInfoRepository.save(userInfo);
        }
        if (!cancelManager.allCanceled()) {
            // 부분취소일 때만 주문에 가격 설정. 전체 취소할 때 가격 설정하면 전체취소 주문 가격이 0원이 됨
            order.setTotalPrice(cancelManager.getOrderPriceAfterCancellation());
            order.setOriginTotalPrice(cancelManager.getProductAndDeliveryFee());
        }
        setCancelReason(request, cancelManager.getTobeCanceled());

        orderProductInfoRepository.saveAll(cancelManager.getAllOrderProducts());
        orderRepository.save(order);
    }

    private static void setVbankRefundInfo(Orders order, CancelData cancelData) {
        if (order.getVbankRefundInfo() != null) {
            VBankRefundInfo refundInfo = order.getVbankRefundInfo();
            cancelData.setRefund_holder(refundInfo.getBankHolder());
            cancelData.setRefund_bank(refundInfo.getBankCode());
            cancelData.setRefund_account(refundInfo.getBankAccount());
        }
    }

    private void sendPortOneCancelData(CancelData cancelData) {
        IamportClient iamportClient = callbackService.getIamportClient();
        try {
            IamportResponse<Payment> cancelResult = iamportClient.cancelPaymentByImpUid(cancelData);
            if (cancelResult.getCode() != 0) {
                log.error("포트원 환불 실패 메시지 = {}", cancelResult.getMessage());
                log.error("포트원 환불 실패 코드 = {}", cancelResult.getCode());
                throw new BusinessException("환불에 실패하였습니다.");
            }
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }

    private void setCancelReason(RequestCancelReq request, List<OrderProductInfo> tobeCanceled) {
        for (OrderProductInfo cancelProduct : tobeCanceled) {
            cancelProduct.setCancelReason(request.getCancelReason());
            cancelProduct.setCancelReasonContent(request.getContent());
        }
    }

    @Nullable
    private String validateRequest(Integer userId, RequestCancelReq data, Orders order) {
        if (userId != order.getUserId()) {
            throw new BusinessException("타인의 주문 내역입니다.");
        }
        if (data.getCancelReason() == null) {
            throw new BusinessException("취소/환불 사유를 선택해주세요.");
        }
        String content = null;
        if (data.getContent() != null) {
            content = utils.validateString(data.getContent(), 1000L, "사유");
        }
        return content;
    }
}
