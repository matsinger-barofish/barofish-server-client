package com.matsinger.barofishserver.domain.order.application;

import com.matsinger.barofishserver.domain.basketProduct.application.BasketCommandService;
import com.matsinger.barofishserver.domain.coupon.application.CouponCommandService;
import com.matsinger.barofishserver.domain.coupon.application.CouponQueryService;
import com.matsinger.barofishserver.domain.coupon.application.CouponUserMapQueryService;
import com.matsinger.barofishserver.domain.coupon.domain.Coupon;
import com.matsinger.barofishserver.domain.notification.application.NotificationCommandService;
import com.matsinger.barofishserver.domain.order.domain.*;
import com.matsinger.barofishserver.domain.order.dto.*;
import com.matsinger.barofishserver.domain.order.orderprductinfo.application.OrderProductInfoCommandService;
import com.matsinger.barofishserver.domain.order.orderprductinfo.application.OrderProductInfoQueryService;
import com.matsinger.barofishserver.domain.order.orderprductinfo.domain.OrderProductInfo;
import com.matsinger.barofishserver.domain.order.orderprductinfo.domain.OrderProductState;
import com.matsinger.barofishserver.domain.order.orderprductinfo.repository.OrderProductInfoRepository;
import com.matsinger.barofishserver.domain.order.repository.OrderDeliverPlaceRepository;
import com.matsinger.barofishserver.domain.order.repository.OrderRepository;
import com.matsinger.barofishserver.domain.payment.application.PaymentService;
import com.matsinger.barofishserver.domain.payment.dto.KeyInPaymentReq;
import com.matsinger.barofishserver.domain.payment.dto.CancelPriceCalculator;
import com.matsinger.barofishserver.domain.payment.portone.application.PortOneCallbackService;
import com.matsinger.barofishserver.domain.product.application.ProductQueryService;
import com.matsinger.barofishserver.domain.product.difficultDeliverAddress.application.DifficultDeliverAddressQueryService;
import com.matsinger.barofishserver.domain.product.domain.Product;
import com.matsinger.barofishserver.domain.product.option.application.OptionQueryService;
import com.matsinger.barofishserver.domain.product.option.domain.Option;
import com.matsinger.barofishserver.domain.product.optionitem.application.OptionItemCommandService;
import com.matsinger.barofishserver.domain.product.optionitem.application.OptionItemQueryService;
import com.matsinger.barofishserver.domain.product.optionitem.domain.OptionItem;
import com.matsinger.barofishserver.domain.product.optionitem.repository.OptionItemRepository;
import com.matsinger.barofishserver.domain.product.repository.ProductRepository;
import com.matsinger.barofishserver.domain.store.application.StoreInfoQueryService;
import com.matsinger.barofishserver.domain.store.domain.ConditionalObject;
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
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
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

        return save(storeMap, order, orderDeliverPlace);
    }

    private void calculateDeliveryFee(StoreInfo storeInfo, List<OrderProductInfo> storeOrderProducts) {
        storeOrderProducts.forEach(v -> v.setDeliveryFee(0)); // 배송비 초기화
        if (storeInfo.isConditional()) {
            setSConditionalDeliveryFee(storeInfo, storeOrderProducts);
        }

        if (!storeInfo.isConditional()) {
            setPConditionalDeliveryFee(storeOrderProducts);
        }
    }

    private void setSConditionalDeliveryFee(StoreInfo storeInfo, List<OrderProductInfo> orderProductInfos) {
        int totalStoreProductPrice = orderProductInfos.stream().mapToInt(v -> v.getTotalProductPrice()).sum();
        if (storeInfo.meetConditions(totalStoreProductPrice)) {
        }
        if (!storeInfo.meetConditions(totalStoreProductPrice)) {
            setDeliveryFeeToMostExpensiveProduct(orderProductInfos);
        }
    }

    private void setPConditionalDeliveryFee(List<OrderProductInfo> pConditionProducts) {
        int[] productIds = pConditionProducts.stream()
                .mapToInt(v -> v.getProductId()).distinct().toArray();

        List<OrderProductInfo> deliveryFeeCalculatingGroup = new ArrayList<>();
        addProductsNeedToCalculateDeliveryFee(pConditionProducts, productIds, deliveryFeeCalculatingGroup);

        setDeliveryFeeToMostExpensiveProduct(deliveryFeeCalculatingGroup);
    }

    private void addProductsNeedToCalculateDeliveryFee(List<OrderProductInfo> pConditionProducts, int[] productIds, List<OrderProductInfo> deliveryFeeCalculatingGroup) {
        for (Integer productId : productIds) {
            Product product = productQueryService.findById(productId);
            List<OrderProductInfo> pConditionProduct = pConditionProducts.stream()
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

    private void validateProductStatesAndDeleteBasket(Integer userId, Product findedProduct) {
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

    @Transactional
    public void cancelOrderByUser(Integer userId, Integer orderProductInfoId, RequestCancelReq request) {
        OrderProductInfo tobeCanceled = orderProductInfoQueryService.findById(orderProductInfoId);
        Orders order = tobeCanceled.getOrder();
        List<OrderProductInfo> allOrderProducts = orderProductInfoRepository.findAllByOrderId(order.getId());
        validateRequest(userId, request, order);
        
        boolean isCouponUsed = false;
        if (order.isCouponUsed()) {
            cancelAll(allOrderProducts, order);
            isCouponUsed = true;
        }

        OptionItem optionItem = optionItemQueryService.findById(tobeCanceled.getOptionItemId());
        Option option = optionQueryService.findById(optionItem.getOptionId());
        Product product = productQueryService.findById(tobeCanceled.getProductId());
        StoreInfo storeInfo = storeInfoQueryService.findByStoreId(product.getStoreId());

        List<OrderProductInfo> cancelProduct = new ArrayList<>();
        if (!option.isNeeded()) {
            cancelProduct = List.of(tobeCanceled);
        }
        if (option.isNeeded()) {
            cancelProduct = extractSameOptions(option, allOrderProducts);
        }

        allOrderProducts.remove(cancelProduct);
        CancelPriceCalculator calculator = createCancelPriceCalculator(cancelProduct, product, allOrderProducts);
        removeProduct(product, storeInfo, allOrderProducts, calculator);
        validateAndSetCancelState(request, cancelProduct);
        cancel(order, calculator.getTotalCancelPrice(), calculator.getNonTaxablePrice());

        RestorePointAndCouponIfAllCanceled(allOrderProducts, isCouponUsed, order);
        orderProductInfoRepository.saveAll(allOrderProducts);
        orderRepository.save(order);
    }

    private void removeProduct(Product product,
                               StoreInfo storeInfo,
                               List<OrderProductInfo> orderProductInfos,
                               CancelPriceCalculator calculator) {
        if (orderProductInfos.isEmpty()) {
            calculator.setNewDeliveryFee(0);
            calculator.calculate();
            return;
        }

        if (storeInfo.isConditional()) {
            List<OrderProductInfo> sameStore = orderProductInfos.stream()
                    .filter(v -> v.getStoreId() == storeInfo.getId()).toList();
            calculator.setNewDeliveryFee(
                    cancelWhenConditional(storeInfo, sameStore)
            );
        }
        if (!storeInfo.isConditional()) {
            List<OrderProductInfo> sameProduct = orderProductInfos.stream()
                    .filter(v -> v.getProductId() == product.getId()).toList();
            if (product.isDeliveryTypeFree()) {
                sameProduct.forEach(v -> v.setDeliveryFee(0));

            }
            if (product.isDeliveryTypeFix()) {
                calculator.setNewDeliveryFee(
                        setDeliveryFeeToMostExpensiveProduct(sameProduct));
            }
            if (product.isDeliveryTypeFreeIfOver()) {
                calculator.setNewDeliveryFee(
                        cancelWhenConditional(product, sameProduct));
            }
        }
        calculator.calculate();
    }

    private void RestorePointAndCouponIfAllCanceled(List<OrderProductInfo> allOrderProducts, boolean isCouponUsed, Orders order) {
        boolean allCanceled= allOrderProducts.stream()
                .allMatch(v -> v.getState().equals(OrderProductState.CANCELED));
        if (allCanceled || isCouponUsed) {
            UserInfo userInfo = userInfoQueryService.findByUserId(order.getUserId());
            userInfo.addPoint(order.getUsedPoint());
            couponCommandService.unUseCoupon(order.getUsedCouponId(), userInfo.getUserId());
        }
    }

    private void cancelAll(List<OrderProductInfo> allOrderProducts, Orders order) {
        validateAndSwitchStateWhenAllCancel(allOrderProducts);
        cancelAllProducts(allOrderProducts, order);
        orderProductInfoRepository.saveAll(allOrderProducts);
    }

    private void cancel(Orders order,
                        Integer totalCancelPrice,
                        Integer taxFreePrice) {
        CancelData cancelData = new CancelData(
                order.getImpUid(),
                true,
                BigDecimal.valueOf(totalCancelPrice)
        );
        cancelData.setTax_free(BigDecimal.valueOf(taxFreePrice));
        if (order.getVbankRefundInfo() != null) {
            VBankRefundInfo refundInfo = order.getVbankRefundInfo();
            cancelData.setRefund_holder(refundInfo.getBankHolder());
            cancelData.setRefund_bank(refundInfo.getBankCode());
            cancelData.setRefund_account(refundInfo.getBankAccount());
        }

        sendPortOneCancelData(cancelData);
    }

    private void cancelAllProducts(List<OrderProductInfo> allOrderProducts, Orders order) {
        cancel(order,
                order.getTotalPrice(),
                allOrderProducts.stream().mapToInt(v -> v.getTaxFreeAmount()).sum()
        );
    }

    private void sendPortOneCancelData(CancelData cancelData) {
        IamportClient iamportClient = callbackService.getIamportClient();
        try {
            IamportResponse<Payment> cancelResult = iamportClient.cancelPaymentByImpUid(cancelData);
            if (cancelResult.getCode() != 0) {
                System.out.println(cancelResult.getMessage());
                throw new BusinessException("환불에 실패하였습니다.");
            }
        } catch (Exception e) {
            throw new BusinessException("환불에 실패하였습니다.");
        }
    }

    private void validateAndSetCancelState(RequestCancelReq request, List<OrderProductInfo> tobeCanceled) {
        for (OrderProductInfo cancelProduct : tobeCanceled) {
            cancelProduct.setCancelReason(request.getCancelReason());
            cancelProduct.setCancelReasonContent(request.getContent());
        }
        validateAndSwitchWhenPartialCancel(tobeCanceled);
    }

    private CancelPriceCalculator createCancelPriceCalculator(List<OrderProductInfo> tobeCanceled, Product product, List<OrderProductInfo> orderProductInfos) {
        int totalProductPrice = tobeCanceled.stream()
                .mapToInt(v -> v.getTotalProductPrice()).sum();
        int existingDeliveryFee = orderProductInfos.stream()
                .mapToInt(v -> v.getDeliveryFee()).sum();

        CancelPriceCalculator taxPriceDto = CancelPriceCalculator.builder()
                .isTaxFree(product.needTaxation())
                .existingDeliveryFee(existingDeliveryFee)
                .productPriceToBeCanceled(totalProductPrice)
                .build();
        return taxPriceDto;
    }

    @NotNull
    private List<OrderProductInfo> extractSameOptions(Option option, List<OrderProductInfo> orderProductInfos) {
        List<OrderProductInfo> sameOptions = new ArrayList<>();
        List<OptionItem> sameOptionItems = optionItemRepository.findAllByOptionId(option.getId());
        for (OrderProductInfo orderProductInfo : orderProductInfos) {
            for (OptionItem sameOption : sameOptionItems) {
                if (orderProductInfo.equalToOptionItemId(sameOption.getId())) {
                    sameOptions.add(orderProductInfo);
                }
            }
        }
        return sameOptions;
    }

    private int cancelWhenConditional(ConditionalObject conditionalObject, List<OrderProductInfo> sameConditional) {
        Integer newDeliveryFee = 0;
        int newTotalProductPrice = sameConditional.stream()
                .mapToInt(v -> v.getTotalProductPrice()).sum();
        if (conditionalObject.meetConditions(newTotalProductPrice)) {
            sameConditional.forEach(v -> v.setDeliveryFee(0));
            newDeliveryFee = 0;
        }
        if (!conditionalObject.meetConditions(newTotalProductPrice)) {
            newDeliveryFee = setDeliveryFeeToMostExpensiveProduct(sameConditional);
        }
        return newDeliveryFee;
    }

    private void validateAndSwitchWhenPartialCancel(List<OrderProductInfo> orderProductInfos) {
        for (OrderProductInfo toBeCanceled : orderProductInfos) {
            OrderProductState state = toBeCanceled.getState();
            if (state.equals(OrderProductState.WAIT_DEPOSIT)) {
                toBeCanceled.setState(OrderProductState.CANCELED);
                continue;
            }
            if (state.equals(OrderProductState.PAYMENT_DONE)) {
                toBeCanceled.setState(OrderProductState.CANCELED);
                continue;
            }
            if (state.equals(OrderProductState.PAYMENT_DONE) ||
                    state.equals(OrderProductState.DELIVERY_DONE) ||
                    state.equals(OrderProductState.EXCHANGE_REQUEST) ||
                    state.equals(OrderProductState.EXCHANGE_ACCEPT) ||
                    state.equals(OrderProductState.FINAL_CONFIRM) ||
                    state.equals(OrderProductState.REFUND_REQUEST) ||
                    state.equals(OrderProductState.REFUND_ACCEPT) ||
                    state.equals(OrderProductState.REFUND_DONE)) {
                throw new BusinessException("취소 불가능한 상태입니다.");
            }
            if (state.equals(OrderProductState.CANCEL_REQUEST)) {
                throw new BusinessException("이미 취소 요청된 상태입니다.");
            }
            if (state.equals(OrderProductState.CANCELED)) {
                throw new BusinessException("취소 완료된 상태입니다.");
            }
            if (state.equals(OrderProductState.DELIVERY_READY)) {
                throw new BusinessException("상품이 출고되어 취소가 불가능합니다.");
            }
        }
        throw new BusinessException("취소 불가능한 상태입니다.");
    }

    private void validateAndSwitchStateWhenAllCancel(List<OrderProductInfo> allOrderProducts) {
        if (allOrderProducts.stream().anyMatch(v -> {
            switch (v.getState()) {
                case ON_DELIVERY:
                case DELIVERY_DONE:
                case EXCHANGE_REQUEST:
                case EXCHANGE_ACCEPT:
                case FINAL_CONFIRM:
                case REFUND_REQUEST:
                case REFUND_ACCEPT:
                case REFUND_DONE:
                case CANCEL_REQUEST:
                case CANCELED:
                    return true;
                case WAIT_DEPOSIT:
                case PAYMENT_DONE:
                case DELIVERY_READY:
                default:
                    return false;
            }
        })) throw new BusinessException("취소 불가능한 상태입니다.");

        if (allOrderProducts.stream().anyMatch(v -> v.getState().equals(OrderProductState.WAIT_DEPOSIT))) {
            allOrderProducts.forEach(info -> info.setState(OrderProductState.CANCELED));
        }
        if (allOrderProducts.stream().anyMatch(v -> v.getState().equals(OrderProductState.DELIVERY_READY))) {
            allOrderProducts.forEach(info -> info.setState(OrderProductState.CANCEL_REQUEST));
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
