package com.matsinger.barofishserver.domain.order.application;

import com.matsinger.barofishserver.domain.basketProduct.application.BasketCommandService;
import com.matsinger.barofishserver.domain.coupon.application.CouponCommandService;
import com.matsinger.barofishserver.domain.coupon.domain.Coupon;
import com.matsinger.barofishserver.domain.order.domain.BankCode;
import com.matsinger.barofishserver.domain.order.domain.OrderPaymentWay;
import com.matsinger.barofishserver.domain.order.domain.OrderState;
import com.matsinger.barofishserver.domain.order.domain.Orders;
import com.matsinger.barofishserver.domain.order.dto.OrderProductReq;
import com.matsinger.barofishserver.domain.order.dto.OrderReq;
import com.matsinger.barofishserver.domain.order.dto.PriceCalculator;
import com.matsinger.barofishserver.domain.order.dto.VBankRefundInfo;
import com.matsinger.barofishserver.domain.order.orderprductinfo.domain.OrderProductInfo;
import com.matsinger.barofishserver.domain.order.orderprductinfo.domain.OrderProductState;
import com.matsinger.barofishserver.domain.product.application.ProductQueryService;
import com.matsinger.barofishserver.domain.product.difficultDeliverAddress.domain.DifficultDeliverAddress;
import com.matsinger.barofishserver.domain.product.difficultDeliverAddress.repository.DifficultDeliverAddressRepository;
import com.matsinger.barofishserver.domain.product.domain.Product;
import com.matsinger.barofishserver.domain.product.optionitem.application.OptionItemCommandService;
import com.matsinger.barofishserver.domain.product.optionitem.domain.OptionItem;
import com.matsinger.barofishserver.domain.store.domain.StoreInfo;
import com.matsinger.barofishserver.domain.user.deliverplace.DeliverPlace;
import com.matsinger.barofishserver.domain.user.deliverplace.application.DeliverPlaceService;
import com.matsinger.barofishserver.domain.userinfo.application.UserInfoQueryService;
import com.matsinger.barofishserver.domain.userinfo.domain.UserInfo;
import com.matsinger.barofishserver.global.exception.BusinessException;
import com.matsinger.barofishserver.utils.Common;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final DeliverPlaceService deliverPlaceService;
    private final DifficultDeliverAddressRepository difficultDeliverAddressRepository;
    private final CouponCommandService couponCommandService;
    private final OptionItemCommandService optionItemCommandService;
    private final BankCodeQueryService bankCodeQueryService;

    @Transactional
    public void 중(Integer userId, OrderReq request) {
        UserInfo findedUser = userInfoQueryService.findById(userId);

        Map<StoreInfo, List<OrderProductReq>> storeProductsMap = combineProductsWithStore(request);
        String orderId = orderService.getOrderId();
        PriceCalculator priceCalculator = new PriceCalculator();
        for (StoreInfo storeInfo : storeProductsMap.keySet()) {

            for (OrderProductReq productReq : storeProductsMap.get(storeInfo)) {
                Product findedProduct = productQueryService.findById(productReq.getProductId());
                validateProductStates(userId, findedProduct);

                Integer productAmountToOrder = productReq.getAmount();
                OptionItem optionItem = optionItemCommandService.reduceQuantity(
                        productReq.getOptionId(), productAmountToOrder);

                OrderProductInfo orderProductInfo = createOrderProduct(productAmountToOrder, orderId, optionItem, findedProduct);
                int deliveryFeeWhenDeliveryTypeFix = calculateDeliveryFeeWhenDeliveryTypeFix(productAmountToOrder, findedProduct, orderProductInfo);
                priceCalculator.addDeliveryFee(deliveryFeeWhenDeliveryTypeFix);

                if (findedProduct.isDeliveryTypeFreeIfOver()) {
                    priceCalculator.addConditionalProduct(orderProductInfo);
                }

                priceCalculator.addToTotalProducts(orderProductInfo);
                priceCalculator.addToTotalProductPrice(optionItem.getDiscountPrice() * productAmountToOrder);
            }

            priceCalculator.setConditionalShippingPrice(storeInfo.getMinOrderPrice());
            priceCalculator.tearDownConditionalProductInfo();
        }

        int pointToUse = request.getPoint();
        findedUser.usePoint(pointToUse);
        Coupon coupon = useCoupon(userId, request.getCouponId(), priceCalculator.getTotalProductPrice());

        validateOrderPrice(pointToUse, coupon, priceCalculator.getTotalOrderPrice());

        DeliverPlace deliverPlace = deliverPlaceService.selectDeliverPlace(request.getDeliverPlaceId());
        List<OrderProductInfo> orderProductInfos = priceCalculator.getTotalProducts();
        validateIfDifficultDeliverRegion(orderProductInfos, deliverPlace);

        VBankRefundInfo vBankRefundInfo = validateVbankRefundInfo(request);

        Orders.builder()
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
                .build();

        deliverPlace.toOrderDeliverPlace(orderId);

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

    private Coupon useCoupon(Integer userId, Integer couponId, Integer minOrderPrice) {
        Coupon coupon = null;
        if (couponId != null) {
            coupon = couponCommandService.useCoupon(userId, couponId, minOrderPrice);
        }
        return coupon;
    }

    private void validateIfDifficultDeliverRegion(List<OrderProductInfo> orderProductInfos, DeliverPlace deliverPlace) {
        for (OrderProductInfo orderProductInfo : orderProductInfos) {
            List<String> difficultDeliveryBcodes = difficultDeliverAddressRepository
                    .findAllByProductId(orderProductInfo.getProductId())
                    .stream().map(DifficultDeliverAddress::getBcode).toList();

            if (isDeliveryPlaceContainsDifficultDeliveryRegion(difficultDeliveryBcodes, deliverPlace)) {
                throw new BusinessException("배송지에 배송 불가능한 상품이 포함돼 있습니다.");
            }
        }
    }

    private boolean isDeliveryPlaceContainsDifficultDeliveryRegion(List<String> difficultDeliveryBcodes, DeliverPlace deliverPlace) {
        return difficultDeliveryBcodes.stream().anyMatch(
                bcode -> bcode.length() >= 5 &&
                        bcode.substring(0, 5).equals(deliverPlace.getBcode().substring(0, 5)));
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
                                ? findedOptionItem.getDiscountPrice() * productAmountToOrder
                                : 0
                )
                .isTaxFree(!product.getNeedTaxation())
                .build();
    }
}
