package com.matsinger.barofishserver.order;

import com.matsinger.barofishserver.coupon.Coupon;
import com.matsinger.barofishserver.coupon.CouponService;
import com.matsinger.barofishserver.coupon.CouponType;
import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.order.object.*;
import com.matsinger.barofishserver.product.ProductService;
import com.matsinger.barofishserver.product.object.OptionItem;
import com.matsinger.barofishserver.product.object.Product;
import com.matsinger.barofishserver.product.object.ProductListDto;
import com.matsinger.barofishserver.user.object.DeliverPlace;
import com.matsinger.barofishserver.user.object.UserInfo;
import com.matsinger.barofishserver.user.UserService;
import com.matsinger.barofishserver.utils.Common;
import com.matsinger.barofishserver.utils.CustomResponse;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/order")
public class OrderController {
    private final ProductService productService;
    private final OrderService orderService;
    private final UserService userService;
    private final CouponService couponService;

    private final JwtService jwt;
    private final Common utils;

    @GetMapping("/payment-check/{id}")
    public ResponseEntity<CustomResponse<Boolean>> checkPaymentDone(@PathVariable("id") String id,
                                                                    @RequestHeader(value = "Authorization") Optional<String> auth) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        Optional<TokenInfo>
                tokenInfo =
                jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER, TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Orders order = orderService.selectOrder(id);
            if (!tokenInfo.get().getType().equals(TokenAuthType.ADMIN) && tokenInfo.get().getId() != order.getUserId())
                return res.throwError("접근 권한이 없습니다.", "NOT_ALLOWED");
            res.setData(Optional.of(order.getState().equals(OrderState.PAYMENT_DONE)));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomResponse<OrderDto>> selectOrder(@PathVariable("id") String id,
                                                                @RequestHeader(value = "Authorization") Optional<String> auth) {
        CustomResponse<OrderDto> res = new CustomResponse<>();
        Optional<TokenInfo>
                tokenInfo =
                jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER, TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Orders order = orderService.selectOrder(id);
            if (!tokenInfo.get().getType().equals(TokenAuthType.ADMIN) && tokenInfo.get().getId() != order.getUserId())
                return res.throwError("접근 권한이 없습니다.", "NOT_ALLOWED");
            UserInfo userInfo = userService.selectUserInfo(order.getUserId());
            res.setData(Optional.ofNullable(orderService.convert2Dto(order)));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/management")
    public ResponseEntity<CustomResponse<List<OrderDto>>> selectOrderListManage(@RequestHeader(value = "Authorization") Optional<String> auth) {
        CustomResponse<List<OrderDto>> res = new CustomResponse<>();
        Optional<TokenInfo>
                tokenInfo =
                jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.PARTNER, TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            List<OrderDto> orders = orderService.selectOrderList(tokenInfo.get().getId()).stream().map(o -> {
                return orderService.convert2Dto(o);
            }).toList();
            res.setData(Optional.ofNullable(orders));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/list")
    public ResponseEntity<CustomResponse<List<OrderDto>>> selectOrderList(@RequestHeader(value = "Authorization") Optional<String> auth) {
        CustomResponse<List<OrderDto>> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            List<OrderDto> orders = orderService.selectOrderList(tokenInfo.get().getId()).stream().map(o -> {
                return orderService.convert2Dto(o);
            }).toList();
            ;
            res.setData(Optional.ofNullable(orders));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/cancel-list")
    public ResponseEntity<CustomResponse<List<OrderDto>>> selectCanceledOrderList(@RequestHeader(value = "Authorization") Optional<String> auth) {
        CustomResponse<List<OrderDto>> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            List<OrderDto> orders = orderService.selectCanceledOrderList(tokenInfo.get().getId()).stream().map(o -> {
                return orderService.convert2Dto(o);
            }).toList();
            ;
            res.setData(Optional.ofNullable(orders));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }


    @Getter
    @NoArgsConstructor
    @ToString
    static class OrderProductReq {
        Integer productId;
        Integer optionId;
        Integer amount;
    }

    @Getter
    @NoArgsConstructor
    static class OrderReq {
        private String name;
        private String tel;
        private Integer couponId;
        private Integer point;
        private Integer totalPrice;
        private List<OrderProductReq> products;
        private Integer deliverPlaceId;
    }

    @PostMapping("")
    public ResponseEntity<CustomResponse<OrderDto>> orderProduct(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                 @RequestBody OrderReq data) {
        CustomResponse<OrderDto> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Integer userId = tokenInfo.get().getId();
            UserInfo userInfo = userService.selectUserInfo(userId);
            String name = utils.validateString(data.getName(), 20L, "주문자 이름");
            String tel = utils.validateString(data.getTel(), 11L, "주문자 연락처");
            Coupon coupon = null;
            if (data.getCouponId() != null) coupon = couponService.selectCoupon(data.getCouponId());
            if (data.point != null && userInfo.getPoint() < data.point)
                return res.throwError("보유한 적립금보다 많은 적립금입니다.", "INPUT_CHECK_REQUIRED");
            int totalPrice = 0;
            List<OrderProductInfo> infos = new ArrayList<>();
            String orderId = orderService.getOrderId();
            for (OrderProductReq productReq : data.getProducts()) {
                Product product = productService.selectProduct(productReq.getProductId());
                OptionItem
                        option =
                        productReq.getOptionId() !=
                                null ? productService.selectOptionItem(productReq.getOptionId()) : null;
                Integer
                        price =
                        ((int) Math.floor(product.getOriginPrice() * ((100 - product.getDiscountRate())) / 100) +
                                (option != null ? option.getPrice() : 0)) * productReq.getAmount() +
                                product.getDeliveryFee();
                totalPrice += price;
                infos.add(OrderProductInfo.builder().orderId(orderId).productId(productReq.getProductId()).state(
                        OrderProductState.WAIT_DEPOSIT).price(price).amount(productReq.getAmount()).deliveryFee(product.getDeliveryFee()).build());
            }
            if (coupon != null) {
                if (totalPrice < coupon.getMinPrice())
                    return res.throwError("쿠폰 최소 금액에 맞지 않습니다.", "INPUT_CHECK_REQUIRED");
                couponService.checkValidCoupon(coupon.getId(), userId);
                totalPrice =
                        totalPrice -
                                (coupon.getType().equals(CouponType.RATE) ? (int) Math.floor(totalPrice *
                                        coupon.getAmount() / 100) : coupon.getAmount()) -
                                (data.point != null ? data.point : 0);
            }
            if (totalPrice != data.getTotalPrice()) return res.throwError("총 금액을 확인해주세요.", "INPUT_CHECK_REQUIRED");
            if (data.deliverPlaceId == null) return res.throwError("배송지를 입력해주세요.", "INPUT_CHECK_REQUIRED");
            DeliverPlace deliverPlace = userService.selectDeliverPlace(data.deliverPlaceId);
            OrderDeliverPlace
                    orderDeliverPlace =
                    OrderDeliverPlace.builder().orderId(orderId).name(deliverPlace.getName()).receiverName(deliverPlace.getReceiverName()).tel(
                            deliverPlace.getTel()).address(deliverPlace.getAddress()).addressDetail(deliverPlace.getAddressDetail()).deliverMessage(
                            deliverPlace.getDeliverMessage()).postalCode(deliverPlace.getPostalCode()).build();
            Orders
                    order =
                    Orders.builder().id(orderId).userId(tokenInfo.get().getId()).state(OrderState.WAIT_DEPOSIT).orderedAt(
                            utils.now()).totalPrice(totalPrice).ordererName(name).ordererTel(tel).build();

            Orders result = orderService.orderProduct(order, infos, orderDeliverPlace);
            if (data.point != null) {
                userInfo.setPoint(userInfo.getPoint() - data.point);
                userService.updateUserInfo(userInfo);
            }
            res.setData(Optional.ofNullable(orderService.convert2Dto(result)));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/recommend/{ids}")
    public ResponseEntity<CustomResponse<List<ProductListDto>>> selectProductOtherCustomerBuy(@PathVariable("ids") String ids) {
        CustomResponse<List<ProductListDto>> res = new CustomResponse<>();
        try {
            List<Product> products = productService.selectProductOtherCustomerBuy(utils.str2IntList(ids));
            List<ProductListDto> productListDtos = products.stream().map(product -> {
                return product.convert2ListDto();
            }).toList();
            res.setData(Optional.of(productListDtos));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    //PROCESS
    // 결제 취소
    @Getter
    @NoArgsConstructor
    private static class RequestCancelReq {
        private OrderCancelReason cancelReason;
        private String content;
    }

    @PostMapping("/cancel/{orderProductInfoId}")
    public ResponseEntity<CustomResponse<Boolean>> cancelOrderByUser(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                     @PathVariable("orderProductInfoId") Integer orderProductInfoId,
                                                                     @RequestPart(value = "data") RequestCancelReq data) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            OrderProductInfo info = orderService.selectOrderProductInfo(orderProductInfoId);
            if (data.getCancelReason() == null) return res.throwError("취소/환불 사유를 선택해주세요.", "INPUT_CHECK_REQUIRED");
            String content = utils.validateString(data.getContent(), 1000L, "사유");
            info.setCancelReason(data.cancelReason);
            info.setCancelReasonContent(content);
            orderService.cancelOrderedProduct(info.getId());
            res.setData(Optional.of(true));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @PostMapping("/cancel/{orderProductInfoId}/partner")
    public ResponseEntity<CustomResponse<Boolean>> cancelOrderByPartner(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                        @PathVariable("orderProductInfoId") Integer orderProductInfoId) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.PARTNER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            res.setData(Optional.of(true));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    //결제 취소 거절
    @PostMapping("/cancel/{orderProductInfoId}/reject")
    public ResponseEntity<CustomResponse<Boolean>> rejectCancelOrder(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                     @PathVariable("orderProductInfoId") Integer orderProductInfoId) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.PARTNER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            OrderProductInfo info = orderService.selectOrderProductInfo(orderProductInfoId);
            if (!info.getState().equals(OrderProductState.CANCEL_REQUEST))
                return res.throwError("취소 신청된 상품이 아닙니다.", "INPUT_CHECK_REQUIRED");
            info.setState(OrderProductState.CANCELED);
            orderService.cancelOrderedProduct(info.getId());
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    //발송 처리
    @Getter
    @NoArgsConstructor
    private static class ProcessDeliverStartReq {
        private String deliverCompanyCode;
        private String invoice;
    }

    @PostMapping("/process-deliver/{orderProductInfoId}")
    public ResponseEntity<CustomResponse<Boolean>> processDeliverStart(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                       @PathVariable("orderProductInfoId") Integer orderProductInfoId,
                                                                       @RequestPart(value = "data") ProcessDeliverStartReq data) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.PARTNER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            OrderProductInfo info = orderService.selectOrderProductInfo(orderProductInfoId);
            if (data.deliverCompanyCode == null) return res.throwError("택배사 코드를 입력해주세요.", "INPUT_CHECK_REQUIRED");
            if (data.invoice == null) return res.throwError("운송장 번호를 입력해주세요.", "INPUT_CHECK_REQUIRED");
            info.setDeliverCompanyCode(data.deliverCompanyCode);
            info.setInvoiceCode(data.invoice);
            orderService.updateOrderProductInfo(new ArrayList<>(Arrays.asList(info)));
            res.setData(Optional.of(true));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    // 교환 신청
    @PostMapping("/change/{orderProductInfoId}")
    public ResponseEntity<CustomResponse<Boolean>> requestChangeProduct(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                        @PathVariable("orderProductInfoId") Integer orderProductInfoId) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            OrderProductInfo info = orderService.selectOrderProductInfo(orderProductInfoId);
            if (!info.getState().equals(OrderProductState.DELIVERY_DONE))
                return res.throwError("교환 요청 가능한 상품이 아닙니다.", "INPUT_CHECK_REQUIRED");
            info.setState(OrderProductState.EXCHANGE_REQUEST);
            orderService.updateOrderProductInfo(new ArrayList<>(Arrays.asList(info)));
            res.setData(Optional.of(true));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    // 교환 거절
    @PostMapping("/change/{orderProductInfoId}/reject")
    public ResponseEntity<CustomResponse<Boolean>> rejectChangeProduct(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                       @PathVariable("orderProductInfoId") Integer orderProductInfoId) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.PARTNER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    //구매 확정
    @PostMapping("/confirm/{orderProductInfoId}")
    public ResponseEntity<CustomResponse<Boolean>> confirmOrderProduct(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                       @PathVariable("orderProductInfoId") Integer orderProductInfoId) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    //반품 요청
    @PostMapping("/refund/{orderProductInfoId}/request")
    public ResponseEntity<CustomResponse<Boolean>> requestRefundOrderProduct(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                             @PathVariable("orderProductInfoId") Integer orderProductInfoId) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    //반품 요청 거절
    @PostMapping("/refund/{orderProductInfoId}/reject")
    public ResponseEntity<CustomResponse<Boolean>> rejectRefundOrderProduct(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                            @PathVariable("orderProductInfoId") Integer orderProductInfoId) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.PARTNER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    //반품 요청 거절
    @PostMapping("/refund/{orderProductInfoId}/confirm")
    public ResponseEntity<CustomResponse<Boolean>> confirmRefundOrderProduct(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                             @PathVariable("orderProductInfoId") Integer orderProductInfoId) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.PARTNER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    //반품 완료
    @PostMapping("/refund/{orderProductInfoId}/done")
    public ResponseEntity<CustomResponse<Boolean>> doneRefundOrderProduct(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                          @PathVariable("orderProductInfoId") Integer orderProductInfoId) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.PARTNER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }
}

