package com.matsinger.barofishserver.domain.order.application;

import com.matsinger.barofishserver.domain.basketProduct.application.BasketCommandService;
import com.matsinger.barofishserver.domain.coupon.application.CouponCommandService;
import com.matsinger.barofishserver.domain.coupon.application.CouponQueryService;
import com.matsinger.barofishserver.domain.coupon.application.CouponUserMapQueryService;
import com.matsinger.barofishserver.domain.coupon.domain.Coupon;
import com.matsinger.barofishserver.domain.notification.application.NotificationCommandService;
import com.matsinger.barofishserver.domain.notification.dto.NotificationMessage;
import com.matsinger.barofishserver.domain.notification.dto.NotificationMessageType;
import com.matsinger.barofishserver.domain.order.domain.*;
import com.matsinger.barofishserver.domain.order.dto.OrderProductReq;
import com.matsinger.barofishserver.domain.order.dto.OrderReq;
import com.matsinger.barofishserver.domain.order.dto.RequestCancelReq;
import com.matsinger.barofishserver.domain.order.dto.VBankRefundInfo;
import com.matsinger.barofishserver.domain.order.orderprductinfo.application.OrderProductInfoCommandService;
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
import com.matsinger.barofishserver.domain.product.option.application.OptionQueryService;
import com.matsinger.barofishserver.domain.product.optionitem.application.OptionItemCommandService;
import com.matsinger.barofishserver.domain.product.optionitem.application.OptionItemQueryService;
import com.matsinger.barofishserver.domain.product.optionitem.domain.OptionItem;
import com.matsinger.barofishserver.domain.product.optionitem.repository.OptionItemRepository;
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
import com.matsinger.barofishserver.domain.userinfo.repository.UserInfoRepository;
import com.matsinger.barofishserver.global.exception.BusinessException;
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
    private final OrderDeliverPlaceRepository orderDeliverPlaceRepository;
    private final OrderProductInfoQueryService orderProductInfoQueryService;
    private final OptionQueryService optionQueryService;
    private final OptionItemRepository optionItemRepository;
    private final CouponCommandService couponCommandService;
    private final PortOneCallbackService callbackService;
    private final UserInfoRepository userInfoRepository;
    private final PaymentRepository paymentRepository;

    @Transactional
    public String proceedOrder(OrderReq request, Integer userId) {
        if (request.getTotalPrice() == 0) {
            throw new RuntimeException("주문 에러 발생");
        }
        UserInfo userInfo = userInfoQueryService.findByUserId(userId);
        String orderId = orderQueryService.getOrderId();
        DeliverPlace deliverPlace = deliverPlaceQueryService.findById(request.getDeliverPlaceId());
        OrderDeliverPlace orderDeliverPlace = deliverPlace.toOrderDeliverPlace(orderId);

        Map<StoreInfo, List<OrderProductInfo>> storeMap = createStoreMap(request, userInfo, orderId);

        log.info("orderId = {}", orderId);
        log.info("totalOrderPrice = {}", request.getTotalPrice());

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
                    .max().getAsInt();
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

        return save(storeMap, order, orderDeliverPlace);
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
                .taxFreeAmount(product.getNeedTaxation() == false ? totalProductPrice : 0)
                .state(OrderProductState.WAIT_DEPOSIT)
                .build();


        StoreInfo storeInfo = storeInfoQueryService.findByStoreId(product.getStoreId());
        List<OrderProductInfo> orderProductInfos = storeMap.getOrDefault(storeInfo, new ArrayList<>());
        orderProductInfos.add(orderProductInfo);
        storeMap.put(storeInfo, orderProductInfos);
    }

    @Transactional
    public void cancelOrderByUser(Integer userId, Integer orderProductInfoId, RequestCancelReq request) {
        OrderProductInfo cancelRequestedProduct = orderProductInfoQueryService.findById(orderProductInfoId);
        Orders order = cancelRequestedProduct.getOrder();
        List<OrderProductInfo> allOrderProducts = orderProductInfoRepository.findAllByOrderId(order.getId());
        validateRequest(userId, request, order);

        if (order.isCouponUsed()) {
            CancelManager cancelManager = new CancelManager(
                    order, allOrderProducts, List.of());
            cancel(order, cancelManager, request);
        }

        Product product = productQueryService.findById(cancelRequestedProduct.getProductId());
        StoreInfo storeInfo = storeInfoQueryService.findByStoreId(product.getStoreId());

        if (order.isCouponUsed()) {
            List<OrderProductInfo> tobeCanceled = allOrderProducts.stream()
                    .filter(v -> v.getState() != OrderProductState.CANCELED)
                    .filter(v -> v.getStoreId() == storeInfo.getStoreId())
                    .toList();
            List<OrderProductInfo> notTobeCanceled = allOrderProducts.stream()
                    .filter(v -> v.getState() != OrderProductState.CANCELED)
                    .filter(v -> v.getStoreId() != storeInfo.getStoreId())
                    .toList();

            setCancelReason(request, tobeCanceled);

            CancelManager cancelManager = new CancelManager(
                    order, tobeCanceled, notTobeCanceled);

            cancel(order, cancelManager, request);
        }

        notificationCommandService.sendFcmToUser(order.getUserId(),
                NotificationMessageType.ORDER_CANCEL,
                NotificationMessage.builder()
                        .productName(product.getTitle())
                        .isCanceledByRegion(false)
                        .build());
    }

    private void cancel(Orders order,
                        CancelManager cancelManager,
                        RequestCancelReq request) {

        log.info("impUid = {}", order.getImpUid());
        log.info("totalCancelPrice = {}", cancelManager.getNonTaxablePriceTobeCanceled() + cancelManager.getTaxablePriceTobeCanceled());
        log.info("taxFreePrice = {}", cancelManager.getNonTaxablePriceTobeCanceled());

        CancelData cancelData = new CancelData(
                order.getImpUid(),
                true,
                BigDecimal.valueOf(cancelManager.getNonTaxablePriceTobeCanceled() + cancelManager.getTaxablePriceTobeCanceled())
        );
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
        setCancelReason(request, cancelManager.getTobeCanceled());
        cancelManager.validateStateAndSetCanceled();
        order.setTotalPrice(cancelManager.getOrderPriceAfterCancellation());
        order.setOriginTotalPrice(cancelManager.getProductAndDeliveryFee());

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
                System.out.println(cancelResult.getMessage());
                log.info(cancelResult.getMessage());
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
