package com.matsinger.barofishserver.domain.order.application;

import com.matsinger.barofishserver.domain.basketProduct.application.BasketCommandService;
import com.matsinger.barofishserver.domain.coupon.application.CouponQueryService;
import com.matsinger.barofishserver.domain.coupon.domain.Coupon;
import com.matsinger.barofishserver.domain.order.domain.BankCode;
import com.matsinger.barofishserver.domain.order.domain.OrderPaymentWay;
import com.matsinger.barofishserver.domain.order.domain.OrderState;
import com.matsinger.barofishserver.domain.order.domain.Orders;
import com.matsinger.barofishserver.domain.order.dto.*;
import com.matsinger.barofishserver.domain.order.orderprductinfo.application.OrderProductInfoCommandService;
import com.matsinger.barofishserver.domain.order.orderprductinfo.domain.OrderProductInfo;
import com.matsinger.barofishserver.domain.order.orderprductinfo.domain.OrderProductState;
import com.matsinger.barofishserver.domain.order.repository.OrderRepository;
import com.matsinger.barofishserver.domain.payment.application.PaymentService;
import com.matsinger.barofishserver.domain.payment.dto.KeyInPaymentReq;
import com.matsinger.barofishserver.domain.product.application.ProductQueryService;
import com.matsinger.barofishserver.domain.product.domain.Product;
import com.matsinger.barofishserver.domain.product.optionitem.application.OptionItemCommandService;
import com.matsinger.barofishserver.domain.product.optionitem.application.OptionItemQueryService;
import com.matsinger.barofishserver.domain.product.optionitem.domain.OptionItem;
import com.matsinger.barofishserver.domain.product.repository.ProductRepository;
import com.matsinger.barofishserver.domain.store.application.StoreInfoQueryService;
import com.matsinger.barofishserver.domain.store.domain.StoreInfo;
import com.matsinger.barofishserver.domain.user.paymentMethod.application.PaymentMethodCommandService;
import com.matsinger.barofishserver.domain.user.paymentMethod.application.PaymentMethodService;
import com.matsinger.barofishserver.domain.user.paymentMethod.domain.PaymentMethod;
import com.matsinger.barofishserver.domain.userinfo.application.UserInfoQueryService;
import com.matsinger.barofishserver.domain.userinfo.domain.UserInfo;
import com.matsinger.barofishserver.global.exception.BusinessException;
import com.matsinger.barofishserver.utils.Common;
import com.siot.IamportRestClient.exception.IamportResponseException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Transactional
    public void order(Integer userId, OrderReq request) throws IamportResponseException, IOException {
        UserInfo findedUser = userInfoQueryService.findById(userId);
        validateRequestInfo(request);

        Map<StoreInfo, List<OrderProductReq>> storeProductsMap = combineProductsWithStore(request);
        String orderId = orderService.getOrderId();
        PriceCalculator priceCalculator = new PriceCalculator();
        for (StoreInfo storeInfo : storeProductsMap.keySet()) {

            for (OrderProductReq productReq : storeProductsMap.get(storeInfo)) {
                Product findedProduct = productQueryService.findById(productReq.getProductId());
                validateProductStates(userId, findedProduct);

                Integer productAmountToOrder = productReq.getAmount();
                OptionItem optionItem = optionItemQueryService.findById(productReq.getOptionId());
                optionItem.validateQuantity(productAmountToOrder);

                OrderProductInfo orderProductInfo = createOrderProduct(productAmountToOrder, orderId, optionItem, findedProduct);
                int deliveryFeeWhenDeliveryTypeFix = calculateDeliveryFeeWhenDeliveryTypeFix(productAmountToOrder, findedProduct, orderProductInfo);
                priceCalculator.addDeliveryFee(deliveryFeeWhenDeliveryTypeFix);

                if (findedProduct.isDeliveryTypeFreeIfOver()) {
                    priceCalculator.addConditionalProduct(orderProductInfo);
                }

                priceCalculator.addToTotalProducts(orderProductInfo);
                priceCalculator.addToTotalProductPrice(optionItem.getDiscountPrice() * productAmountToOrder);
                if (findedProduct.getNeedTaxation() == false) {
                    priceCalculator.addTotalTaxFeePrice(
                            optionItem.getDiscountPrice() * orderProductInfo.getAmount());
                }
            }

            priceCalculator.setConditionalShippingPrice(storeInfo.getMinOrderPrice());
            priceCalculator.tearDownConditionalProductInfo();
        }

        int pointToUse = request.getPoint();
        findedUser.validatePoint(pointToUse);
        Coupon coupon = validateCoupon(request.getCouponId(), priceCalculator.getTotalProductPrice());
        validateOrderPrice(pointToUse, coupon, priceCalculator.getTotalOrderPrice());

        VBankRefundInfo vBankRefundInfo = validateVbankRefundInfo(request);

        if (request.getPaymentWay().equals(OrderPaymentWay.KEY_IN)) {
            PaymentMethod paymentMethod = paymentMethodService.selectPaymentMethod(request.getPaymentMethodId());
            OrderProductInfo orderProductInfo = priceCalculator.getTotalProducts().get(0);
            String titleOfFirstProductToOrder = orderProductInfo.getProduct().getTitle();
            Boolean isSuccess = paymentService.processKeyInPayment(KeyInPaymentReq.builder()
                    .orderId(orderId)
                    .paymentMethod(paymentMethod)
                    .order_name(titleOfFirstProductToOrder)
                    .taxFree(priceCalculator.getTotalTaxFreePrice())
                    .total_amount(priceCalculator.getTotalOrderPrice())
                    .taxFree(priceCalculator.getTotalTaxFreePrice())
                    .build());
            if (!isSuccess) {
                throw new BusinessException("결제에 실패하였습니다.");
            }
        }

        Orders savedOrder = orderRepository.save(Orders.builder()
                .id(orderId)
                .userId(userId)
                .paymentWay(request.getPaymentWay())
                .state(OrderState.WAIT_DEPOSIT)
                .couponId(coupon != null ? coupon.getId() : null)
                .orderedAt(Timestamp.valueOf(LocalDateTime.now()))
                .totalPrice(priceCalculator.getTotalOrderPrice())
                .usePoint(pointToUse)
                .couponDiscount(coupon != null ? coupon.getAmount() : null)
                .ordererName(request.getName())
                .ordererTel(request.getTel())
                .bankHolder(vBankRefundInfo != null ? vBankRefundInfo.getBankHolder() : null)
                .bankCode(vBankRefundInfo != null ? vBankRefundInfo.getBankCode() : null)
                .bankName(vBankRefundInfo != null ? vBankRefundInfo.getBankName() : null)
                .bankAccount(vBankRefundInfo != null ? vBankRefundInfo.getBankAccount() : null)
                .originTotalPrice(priceCalculator.getTotalProductPrice()) // 총 상품 가격만 책정. 수정할 것
                .build());
        orderDeliverPlaceCommandService.save(priceCalculator.getTotalProducts(), request.getDeliverPlaceId());
        orderProductInfoCommandService.saveAll(priceCalculator.getTotalProducts());

        savedOrder.toDto();
    }

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

    private Coupon validateCoupon(Integer couponId, Integer minOrderPrice) {
        Coupon coupon = null;
        if (couponId != null) {
            coupon = couponQueryService.validateCoupon(couponId, minOrderPrice);
        }
        return coupon;
    }

    private void validateOrderPrice(Integer pointToUse, Coupon coupon, Integer totalOrderPrice) {
        int couponDiscount = coupon != null ? coupon.getAmount() : 0;
        int appliedDiscountPrice = totalOrderPrice - couponDiscount - pointToUse;

        if (pointToUse != appliedDiscountPrice) {
            throw new BusinessException("총 금액을 확인해주세요.");
        }
    }

    private int calculateDeliveryFeeWhenDeliveryTypeFix(Integer productAmountToOrder,
                                                        Product findedProduct,
                                                        OrderProductInfo orderProductInfo) {
        if (findedProduct.isDeliveryTypeFix()) {
            Integer numberOfItemsPerBox = findedProduct.getDeliverBoxPerAmount();
            int boxCountsToBeDelivered = (int) Math.ceil(productAmountToOrder / numberOfItemsPerBox);

            orderProductInfo.setDeliveryFee(boxCountsToBeDelivered * findedProduct.getDeliverFee());
            return boxCountsToBeDelivered * findedProduct.getDeliverFee();
        }
        return 0;
    }

    private void validateProductStates(Integer userId, Product findedProduct) {
        if (findedProduct.isPromotionEnd()) {
            basketCommandService.deleteBasket(findedProduct.getId(), userId);
            throw new BusinessException("프로모션 기간이 아닌 상품이 포함되어 있습니다.");
        }
        findedProduct.validateState();
    }

    @NotNull
    private Map<StoreInfo, List<OrderProductReq>> combineProductsWithStore(OrderReq request) {
        Map<StoreInfo, List<OrderProductReq>> combinedStoreMap = new HashMap<>();
        for (OrderProductReq productReq : request.getProducts()) {
            Product findedProduct = productQueryService.findById(productReq.getProductId());
            StoreInfo storeInfo = findedProduct.getStore().getStoreInfo();

            List<OrderProductReq> existingArrays = combinedStoreMap.getOrDefault(storeInfo, new ArrayList<>());
            existingArrays.add(productReq);
            combinedStoreMap.put(storeInfo, existingArrays);
        }
        return combinedStoreMap;
    }

    private OrderProductInfo createOrderProduct(Integer productAmountToOrder,
                                                String orderId,
                                                OptionItem findedOptionItem,
                                                Product product) {
        return OrderProductInfo.builder()
                .orderId(orderId)
                .optionItemId(findedOptionItem.getId())
                .productId(product.getId())
                .state(OrderProductState.WAIT_DEPOSIT)
                .settlePrice(findedOptionItem.getPurchasePrice() * productAmountToOrder)
                .originPrice(findedOptionItem.getDiscountPrice())
                .price(findedOptionItem.getDiscountPrice() * productAmountToOrder)
                .amount(productAmountToOrder)
                .isSettled(false)
                .deliveryFee(0)
                .taxFreeAmount(
                        product.getNeedTaxation() == false
                                ? findedOptionItem.getDiscountPrice()
                                : 0
                )
                .isTaxFree(!product.getNeedTaxation())
                .build();
    }
}
