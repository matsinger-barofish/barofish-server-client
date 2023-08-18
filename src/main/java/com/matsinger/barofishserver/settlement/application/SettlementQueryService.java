package com.matsinger.barofishserver.settlement.application;

import com.matsinger.barofishserver.coupon.application.CouponQueryService;
import com.matsinger.barofishserver.coupon.domain.Coupon;
import com.matsinger.barofishserver.deliver.application.DeliveryQueryService;
import com.matsinger.barofishserver.deliver.domain.DeliveryCompany;
import com.matsinger.barofishserver.deliver.repository.DeliveryCompanyRepository;
import com.matsinger.barofishserver.order.application.OrderQueryService;
import com.matsinger.barofishserver.order.application.OrderService;
import com.matsinger.barofishserver.order.domain.OrderDeliverPlace;
import com.matsinger.barofishserver.order.domain.Orders;
import com.matsinger.barofishserver.order.orderprductinfo.domain.OrderProductInfo;
import com.matsinger.barofishserver.order.orderprductinfo.repository.OrderProductInfoRepository;
import com.matsinger.barofishserver.order.repository.OrderRepository;
import com.matsinger.barofishserver.product.domain.Product;
import com.matsinger.barofishserver.product.option.application.OptionQueryService;
import com.matsinger.barofishserver.product.option.domain.Option;
import com.matsinger.barofishserver.product.optionitem.application.OptionItemQueryService;
import com.matsinger.barofishserver.product.optionitem.domain.OptionItem;
import com.matsinger.barofishserver.product.optionitem.repository.OptionItemRepository;
import com.matsinger.barofishserver.settlement.domain.Settlement;
import com.matsinger.barofishserver.settlement.domain.SettlementState;
import com.matsinger.barofishserver.settlement.dto.OrderSettlementExcelDto;
import com.matsinger.barofishserver.settlement.repository.SettlementRepository;
import com.matsinger.barofishserver.store.application.StoreService;
import com.matsinger.barofishserver.store.domain.StoreInfo;
import com.matsinger.barofishserver.user.application.UserQueryService;
import com.matsinger.barofishserver.user.domain.User;
import com.matsinger.barofishserver.userinfo.domain.UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class SettlementQueryService {
    private final SettlementRepository settlementRepository;
    private final StoreService storeService;
    private final OrderService orderService;
    private final OrderQueryService orderQueryService;
    private final OptionItemQueryService optionItemQueryService;
    private final OptionQueryService optionQueryService;
    private final CouponQueryService couponQueryService;
    private final UserQueryService userQueryService;
    private final DeliveryQueryService deliveryQueryService;

    public Settlement selectSettlement(Integer id) {
        return settlementRepository.findById(id).orElseThrow(() -> {
            throw new Error("정산 내역 정보를 찾을 수 없습니다.");
        });
    }

    public Page<Settlement> selectSettlementList(Specification<Settlement> spec, Pageable pageable) {
        return settlementRepository.findAll(spec, pageable);
    }

    public List<Settlement> selectSettlementListWithState(SettlementState state) {
        if (state == null) return settlementRepository.findAll();
        else return settlementRepository.findAllByState(state);
    }
    public Integer getSettlementAmount(Integer storeId, Boolean isSettled) {
        List<OrderProductInfo>
                infos =
                orderService.selectOrderProductInfoListWithStoreIdAndIsSettled(storeId, isSettled);
        return getSettlementAmount(infos, storeId);
    }
    public Integer getSettlementAmount(List<OrderProductInfo> infos, Integer storeId) {
        int totalPrice = 0;
        if (infos != null && infos.size() != 0) {
            StoreInfo storeInfo = storeService.selectStoreInfo(storeId);
            Float
                    settlementRate =
                    storeInfo.getSettlementRate() == null ||
                            storeInfo.getSettlementRate() == 100 ? null : storeInfo.getSettlementRate();
            for (OrderProductInfo info : infos) {
                int salePrice = info.getSettlePrice() != null ? info.getSettlePrice() : 0;
                int
                        price =
                        (settlementRate == null ? salePrice * info.getAmount() : (int) (salePrice * settlementRate /
                                100.) / 10 * 10);
                totalPrice += price;
            }
        }
        return totalPrice;
    }

    public Page<OrderSettlementExcelDto> createOrderSettlementResponse(Page<OrderProductInfo> request) {

        return  request.map(productInfo -> {
            Orders findOrder = orderQueryService.findById(productInfo.getOrderId());
            OptionItem findOptionItem = optionItemQueryService.findById(productInfo.getOptionItemId());
            Option findOption = optionQueryService.findById(findOptionItem.getOptionId());
            Product findProduct = productInfo.getProduct();
            StoreInfo findStoreInfo = findProduct.getStore().getStoreInfo();
            Coupon findCoupon = couponQueryService.findById(findOrder.getCouponId());
            OrderDeliverPlace findDeliverPlace = findOrder.getDeliverPlace();
            User findUser = userQueryService.findById(findOrder.getUserId());
            UserInfo findUserInfo = findUser.getUserInfo();

            int discountPrice = findOptionItem.getDiscountPrice();
            int quantity = productInfo.getAmount();
            int deliveryFee = productInfo.getDeliveryFee();

            int orderAmount = discountPrice * quantity + deliveryFee;

            int couponDiscount = findOrder.getCouponDiscount();
            Integer usePoint = findOrder.getUsePoint();
//            int finalSettlementAmount = orderAmount - couponDiscount - usePoint;
            double settlementRate = (double) findStoreInfo.getSettlementRate() / 100;

            double settlementAmount = (double) (discountPrice * quantity) * (1 - settlementRate);

            return OrderSettlementExcelDto.builder()
                    .productId(productInfo.getProductId())
                    .orderId(findOrder.getId())
                    .orderProductState(productInfo.getState())
                    .orderAt(findOrder.getOrderedAt())
                    .storeName(findProduct.getStore().getName())
                    .productName(findProduct.getTitle())
                    .optionName(findOptionItem.getName())
                    .needTaxation(findProduct.getNeedTaxation())
                    .purchasePrice(findOptionItem.getPurchasePrice())
                    .originPrice(findOptionItem.getOriginPrice())
                    .discountPrice(discountPrice)
                    .deliveryFee(deliveryFee)
                    .quantity(quantity)
                    .orderAmount(discountPrice * quantity + deliveryFee)
//                    .finalPaymentAmount(finalSettlementAmount)
                    .paymentMethod(findOrder.getPaymentWay())
                    .settlementRatio(settlementRate)
                    .couponName(findCoupon.getTitle())
                    .couponDiscount(findCoupon.getAmount())
                    .usePoint(findOrder.getUsePoint())
                    .settlementAmount(settlementAmount)
                    .finalSettlementAmount(settlementAmount + deliveryFee)
                    .settledAt(productInfo.getSettledAt())
                    .customerName(findDeliverPlace.getReceiverName())
                    .customerPhoneNumber(findDeliverPlace.getTel())
                    .customerEmail(findUserInfo.getEmail())
                    .customerAddress(findDeliverPlace.getAddress())
                    .deliveryMessage(findDeliverPlace.getDeliverMessage())
                    .deliveryCompany(findStoreInfo.getDeliveryCompany().getName())
                    .trackingNumber(productInfo.getInvoiceCode())
                    .build();
        });
    }
}
