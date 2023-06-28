package com.matsinger.barofishserver.order;

import com.matsinger.barofishserver.coupon.Coupon;
import com.matsinger.barofishserver.coupon.CouponService;
import com.matsinger.barofishserver.coupon.CouponType;
import com.matsinger.barofishserver.inquiry.InquiryType;
import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.notification.NotificationMessage;
import com.matsinger.barofishserver.notification.NotificationMessageType;
import com.matsinger.barofishserver.notification.NotificationService;
import com.matsinger.barofishserver.order.object.*;
import com.matsinger.barofishserver.payment.PaymentService;
import com.matsinger.barofishserver.product.ProductService;
import com.matsinger.barofishserver.product.object.OptionItem;
import com.matsinger.barofishserver.product.object.Product;
import com.matsinger.barofishserver.product.object.ProductListDto;
import com.matsinger.barofishserver.product.object.ProductState;
import com.matsinger.barofishserver.siteInfo.SiteInfoService;
import com.matsinger.barofishserver.siteInfo.SiteInformation;
import com.matsinger.barofishserver.user.PaymentMethodService;
import com.matsinger.barofishserver.user.object.DeliverPlace;
import com.matsinger.barofishserver.user.object.PaymentMethod;
import com.matsinger.barofishserver.user.object.UserInfo;
import com.matsinger.barofishserver.user.UserService;
import com.matsinger.barofishserver.utils.Common;
import com.matsinger.barofishserver.utils.CustomResponse;
import jakarta.persistence.criteria.Predicate;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/order")
public class OrderController {
    private final ProductService productService;
    private final OrderService orderService;
    private final UserService userService;
    private final CouponService couponService;
    private final PaymentService paymentService;
    private final NotificationService notificationService;
    private final SiteInfoService siteInfoService;
    private final PaymentMethodService paymentMethodService;
    private final JwtService jwt;
    private final Common utils;

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class PointRuleRes {
        Integer pointRate;
        Integer maxReviewPoint;
    }

    @GetMapping("/point-rule")
    public ResponseEntity<CustomResponse<PointRuleRes>> selectPointRule(@RequestHeader(value = "Authorization") Optional<String> auth) {
        CustomResponse<PointRuleRes> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Integer userId = tokenInfo.get().getId();
            UserInfo userInfo = userService.selectUserInfo(userId);
            SiteInformation siteInfo = siteInfoService.selectSiteInfo("INT_REVIEW_POINT_TEXT");
            Integer maxReviewPoint = Integer.parseInt(siteInfo.getContent());
            res.setData(Optional.ofNullable(PointRuleRes.builder().maxReviewPoint(maxReviewPoint).pointRate(userInfo.getGrade().getPointRate()).build()));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

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
                jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER, TokenAuthType.ADMIN, TokenAuthType.PARTNER),
                        auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Orders order = orderService.selectOrder(id);
            if (!tokenInfo.get().getType().equals(TokenAuthType.ADMIN)) {
                if (tokenInfo.get().getType().equals(TokenAuthType.USER) &&
                        tokenInfo.get().getId() != order.getUserId())
                    return res.throwError("접근 권한이 없습니다.", "NOT_ALLOWED");
            }
            UserInfo userInfo = userService.selectUserInfo(order.getUserId());
            res.setData(Optional.ofNullable(orderService.convert2Dto(order, null, null)));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/management")
    public ResponseEntity<CustomResponse<Page<OrderDto>>> selectOrderListManage(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                                @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                                                                @RequestParam(value = "take", required = false, defaultValue = "10") Integer take,
                                                                                @RequestParam(value = "orderby", required = false, defaultValue = "id") OrderOrderBy orderBy,
                                                                                @RequestParam(value = "sortType", required = false, defaultValue = "DESC") Sort.Direction sort,
                                                                                @RequestParam(value = "ordererName", required = false) String ordererName,
                                                                                @RequestParam(value = "id", required = false) String id,
                                                                                @RequestParam(value = "state", required = false) String state,
                                                                                @RequestParam(value = "email", required = false) String email,
                                                                                @RequestParam(value = "phone", required = false) String phone,
                                                                                @RequestParam(value = "receiverName", required = false) String receiverName,
                                                                                @RequestParam(value = "address", required = false) String address,
                                                                                @RequestParam(value = "postalCode", required = false) String postalCode,
                                                                                @RequestParam(value = "orderAtS", required = false) Timestamp orderAtS,
                                                                                @RequestParam(value = "orderAtE", required = false) Timestamp orderAtE) {
        CustomResponse<Page<OrderDto>> res = new CustomResponse<>();
        Optional<TokenInfo>
                tokenInfo =
                jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.PARTNER, TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Specification<Orders> spec = (root, query, builder) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (ordererName != null) predicates.add(builder.like(root.get("ordererName"), "%" + ordererName + "%"));
                if (id != null) predicates.add(builder.like(root.get("id"), "%" + id + "%"));
                if (email != null)
                    predicates.add(builder.like(root.get("user").get("userInfo").get("email"), "%" + email + "%"));
                if (phone != null) predicates.add(builder.like(root.get("ordererTel"), "%" + phone + "%"));
                if (receiverName != null) predicates.add(builder.like(root.get("deliverPlace").get("receiverName"),
                        "%" + receiverName + "%"));
                if (address != null)
                    predicates.add(builder.like(root.get("deliverPlace").get("address"), "%" + address + "%"));
                if (postalCode != null)
                    predicates.add(builder.like(root.get("deliverPlace").get("postalCode"), "%" + postalCode + "%"));
                if (orderAtS != null) predicates.add(builder.greaterThan(root.get("orderedAt"), orderAtS));
                if (orderAtE != null) predicates.add(builder.lessThan(root.get("orderedAt"), orderAtE));
//                if (tokenInfo.get().getType().equals(TokenAuthType.PARTNER))
//                    predicates.add(builder.)
//                    predicates.add(builder.equal(root.get("storeId"), tokenInfo.get().getId()))
                return builder.and(predicates.toArray(new Predicate[0]));
            };
            Specification<OrderProductInfo> productSpec = (root, query, builder) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (state != null)
                    predicates.add(builder.and(root.get("type").in(Arrays.stream(state.split(",")).map(OrderProductState::valueOf).toList())));
                if (tokenInfo.get().getType().equals(TokenAuthType.PARTNER))
                    predicates.add(builder.equal(root.get("product").get("storeId"), tokenInfo.get().getId()));
                return builder.and(predicates.toArray(new Predicate[0]));
            };
            PageRequest pageRequest = PageRequest.of(page, take, Sort.by(sort, orderBy.label));
            Page<OrderDto> orders = orderService.selectOrderLitByAdmin(pageRequest, spec).map(o -> {
                return orderService.convert2Dto(o,
                        tokenInfo.get().getType().equals(TokenAuthType.PARTNER) ? tokenInfo.get().getId() : null,
                        productSpec);
            });
            res.setData(Optional.ofNullable(orders));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/management/user/{userId}")
    public ResponseEntity<CustomResponse<List<OrderDto>>> selectOrderListWithUserId(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                                    @PathVariable("userId") Integer userId,
                                                                                    @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                                                                    @RequestParam(value = "take", required = false, defaultValue = "10") Integer take) {
        CustomResponse<List<OrderDto>> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            PageRequest pageRequest = PageRequest.of(page, take, Sort.by(Sort.Direction.DESC, "orderedAt"));
            List<OrderDto> orders = orderService.selectOrderList(userId, pageRequest).stream().map(o -> {
                return orderService.convert2Dto(o, null, null);
            }).toList();
            res.setData(Optional.of(orders));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/list")
    public ResponseEntity<CustomResponse<List<OrderDto>>> selectOrderList(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                          @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                                                          @RequestParam(value = "take", required = false, defaultValue = "10") Integer take) {
        CustomResponse<List<OrderDto>> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            PageRequest pageRequest = PageRequest.of(page, take, Sort.by(Sort.Direction.DESC, "orderedAt"));
            List<OrderDto>
                    orders =
                    orderService.selectOrderList(tokenInfo.get().getId(), pageRequest).stream().map(o -> {
                        return orderService.convert2Dto(o, null, null);
                    }).toList();

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
                return orderService.convert2Dto(o, null, null);
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
        private OrderPaymentWay paymentWay;
        private Integer point;
        private Integer totalPrice;
        private Integer couponDiscountPrice;
        private List<OrderProductReq> products;

        private Integer deliverPlaceId;
        private Integer paymentMethodId;
    }

    @PostMapping("")
    public ResponseEntity<CustomResponse<OrderDto>> orderProduct(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                 @RequestBody OrderReq data) {
        CustomResponse<OrderDto> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Integer userId = tokenInfo.get().getId();
            String orderId = orderService.getOrderId();
            String name = utils.validateString(data.getName(), 20L, "주문자 이름");
            String tel = utils.validateString(data.getTel(), 11L, "주문자 연락처");
            int totalPrice = data.totalPrice;
            if (data.paymentWay.equals(OrderPaymentWay.KEY_IN)) {
                PaymentMethod paymentMethod = paymentMethodService.selectPaymentMethod(data.paymentMethodId);
//                PaymentService.KeyInPaymentReq
//                        req =
//                        PaymentService.KeyInPaymentReq.builder().paymentMethod(paymentMethod).order_name(name).orderId(
//                                orderId).total_amount(totalPrice).build();
//                Boolean keyInResult = paymentService.processKeyInPayment(req);
//                if (!keyInResult) return res.throwError("결제에 실패하였습니다.", "NOT_ALLOWED");
            }
            UserInfo userInfo = userService.selectUserInfo(userId);

            Coupon coupon = null;
            if (data.getCouponId() != null) coupon = couponService.selectCoupon(data.getCouponId());
            if (data.point != null && userInfo.getPoint() < data.point)
                return res.throwError("보유한 적립금보다 많은 적립금입니다.", "INPUT_CHECK_REQUIRED");
            List<OrderProductInfo> infos = new ArrayList<>();
            List<OptionItem> optionItems = new ArrayList<>();
            for (OrderProductReq productReq : data.getProducts()) {
                Product product = productService.selectProduct(productReq.getProductId());
                OptionItem optionItem = productService.selectOptionItem(productReq.getOptionId());
                if (optionItem.getDeliverBoxPerAmount() != null &&
                        optionItem.getMaxAvailableAmount() < productReq.amount)
                    return res.throwError("최대 주문 수량을 초과하였습니다.", "INPUT_CHECK_REQUIRED");
                optionItem.reduceAmount(productReq.getAmount());
                int
                        price =
                        orderService.getProductPrice(product,
                                productReq.getOptionId(),
                                productReq.getAmount(),
                                product.getDeliverBoxPerAmount());
                Integer
                        deliveryFee =
                        orderService.getProductDeliveryFee(product, productReq.getOptionId(), productReq.getAmount());
                optionItems.add(productService.selectOptionItem(productReq.getOptionId()));
                infos.add(OrderProductInfo.builder().optionItemId(optionItem.getId()).orderId(orderId).productId(
                        productReq.getProductId()).state(OrderProductState.WAIT_DEPOSIT).settlePrice(optionItem.getPurchasePrice()).price(
                        price).amount(productReq.getAmount()).isSettled(false).deliveryFee(deliveryFee).build());
            }
            if (coupon != null) {
                if (totalPrice < coupon.getMinPrice())
                    return res.throwError("쿠폰 최소 금액에 맞지 않습니다.", "INPUT_CHECK_REQUIRED");
                couponService.checkValidCoupon(coupon.getId(), userId);

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
                    Orders.builder().id(orderId).userId(tokenInfo.get().getId()).paymentWay(data.paymentWay).state(
                            OrderState.WAIT_DEPOSIT).couponId(data.couponId).orderedAt(utils.now()).totalPrice(data.getTotalPrice()).usePoint(
                            data.point).couponDiscount(data.couponDiscountPrice).ordererName(name).ordererTel(tel).build();

            Orders result = orderService.orderProduct(order, infos, orderDeliverPlace);
            if (data.point != null) {
                userInfo.setPoint(userInfo.getPoint() - data.point);
                userService.updateUserInfo(userInfo);
            }
            if (coupon != null) {
                couponService.useCoupon(coupon.getId(), userId);
            }
            res.setData(Optional.ofNullable(orderService.convert2Dto(result, null, null)));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/recommend")
    public ResponseEntity<CustomResponse<List<ProductListDto>>> selectProductOtherCustomerBuy(@RequestParam(value = "ids") String ids) {
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
            Orders order = orderService.selectOrder(info.getOrderId());
            if (tokenInfo.get().getId() != order.getUserId()) return res.throwError("타인의 주문 내역입니다.", "NOT_ALLOWED");
            if (data.getCancelReason() == null) return res.throwError("취소/환불 사유를 선택해주세요.", "INPUT_CHECK_REQUIRED");
            String content = utils.validateString(data.getContent(), 1000L, "사유");
            info.setCancelReason(data.cancelReason);
            info.setCancelReasonContent(content);
            orderService.requestCancelOrderProduct(info.getId());
            orderService.updateOrderProductInfo(new ArrayList<>(Arrays.asList(info)));
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
        Optional<TokenInfo>
                tokenInfo =
                jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.PARTNER, TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            OrderProductInfo info = orderService.selectOrderProductInfo(orderProductInfoId);
            orderService.cancelOrderedProduct(orderProductInfoId);
            res.setData(Optional.of(true));
            Orders order = orderService.selectOrder(info.getOrderId());
            Product product = productService.selectProduct(info.getProductId());
            info.setState(OrderProductState.CANCELED);
            orderService.updateOrderProductInfo(new ArrayList<>(Arrays.asList(info)));
            notificationService.sendFcmToUser(order.getUserId(),
                    NotificationMessageType.ORDER_CANCEL,
                    NotificationMessage.builder().productName(product.getTitle()).build());
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
        Optional<TokenInfo>
                tokenInfo =
                jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.PARTNER, TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            OrderProductInfo info = orderService.selectOrderProductInfo(orderProductInfoId);
            if (!info.getState().equals(OrderProductState.CANCEL_REQUEST))
                return res.throwError("취소 신청된 상품이 아닙니다.", "INPUT_CHECK_REQUIRED");
            info.setState(OrderProductState.DELIVERY_READY);
            orderService.cancelOrderedProduct(info.getId());
            Orders order = orderService.selectOrder(info.getOrderId());
            Product product = productService.selectProduct(info.getProductId());
            notificationService.sendFcmToUser(order.getUserId(),
                    NotificationMessageType.CANCEL_REJECT,
                    NotificationMessage.builder().productName(product.getTitle()).build());
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @PostMapping("/cancel/{orderProductInfoId}/confirm")
    public ResponseEntity<CustomResponse<Boolean>> confirmCancelOrder(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                      @PathVariable("orderProductInfoId") Integer orderProductInfoId) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        Optional<TokenInfo>
                tokenInfo =
                jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.PARTNER, TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            OrderProductInfo info = orderService.selectOrderProductInfo(orderProductInfoId);
            if (!info.getState().equals(OrderProductState.CANCEL_REQUEST))
                return res.throwError("취소 신청된 상품이 아닙니다.", "INPUT_CHECK_REQUIRED");
            info.setState(OrderProductState.CANCELED);
            orderService.cancelOrderedProduct(orderProductInfoId);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @PostMapping("/deliver-ready/{orderProductInfoId}")
    public ResponseEntity<CustomResponse<Boolean>> deliverReady(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                @PathVariable("orderProductInfoId") Integer orderProductInfoId) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.PARTNER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            OrderProductInfo info = orderService.selectOrderProductInfo(orderProductInfoId);
            info.setState(OrderProductState.DELIVERY_READY);
            orderService.updateOrderProductInfo(new ArrayList<>(Arrays.asList(info)));
            res.setData(Optional.of(true));
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
        Optional<TokenInfo>
                tokenInfo =
                jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.PARTNER, TokenAuthType.ADMIN), auth);
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
    @Getter
    @NoArgsConstructor
    private static class RequestChangeProduct {
        private OrderCancelReason cancelReason;
        private String reasonContent;
    }

    @PostMapping("/change/{orderProductInfoId}")
    public ResponseEntity<CustomResponse<Boolean>> requestChangeProduct(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                        @PathVariable("orderProductInfoId") Integer orderProductInfoId,
                                                                        @RequestPart(value = "data") RequestChangeProduct data) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            OrderProductInfo info = orderService.selectOrderProductInfo(orderProductInfoId);
            Orders order = orderService.selectOrder(info.getOrderId());
            if (tokenInfo.get().getId() != order.getUserId()) return res.throwError("타인의 주문 내역입니다.", "NOT_ALLOWED");
            if (!info.getState().equals(OrderProductState.DELIVERY_DONE))
                return res.throwError("교환 요청 가능한 상품이 아닙니다.", "INPUT_CHECK_REQUIRED");
            if (data.reasonContent == null) return res.throwError("교환 사유를 입력해주세요.", "INPUT_CHECK_REQUIRED");
            String content = data.reasonContent.trim();
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
        Optional<TokenInfo>
                tokenInfo =
                jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.PARTNER, TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            OrderProductInfo info = orderService.selectOrderProductInfo(orderProductInfoId);
            if (!info.getState().equals(OrderProductState.EXCHANGE_REQUEST))
                return res.throwError("교환 요청된 주문이 아닙니다.", "INPUT_CHECK_REQUIRED");
            info.setState(OrderProductState.DELIVERY_DONE);
            orderService.updateOrderProductInfo(new ArrayList<>(Arrays.asList(info)));
            Orders order = orderService.selectOrder(info.getOrderId());
            Product product = productService.selectProduct(info.getProductId());
            notificationService.sendFcmToUser(order.getUserId(),
                    NotificationMessageType.EXCHANGE_REJECT,
                    NotificationMessage.builder().productName(product.getTitle()).build());
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    // 교환 확인
    @PostMapping("/change/{orderProductInfoId}/confirm")
    public ResponseEntity<CustomResponse<Boolean>> confirmChangeProduct(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                        @PathVariable("orderProductInfoId") Integer orderProductInfoId) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        Optional<TokenInfo>
                tokenInfo =
                jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.PARTNER, TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            OrderProductInfo info = orderService.selectOrderProductInfo(orderProductInfoId);
            if (!info.getState().equals(OrderProductState.EXCHANGE_REQUEST))
                return res.throwError("교환 요청된 주문이 아닙니다.", "INPUT_CHECK_REQUIRED");
            info.setState(OrderProductState.EXCHANGE_ACCEPT);
            orderService.updateOrderProductInfo(new ArrayList<>(Arrays.asList(info)));
            Orders order = orderService.selectOrder(info.getOrderId());
            Product product = productService.selectProduct(info.getProductId());
            notificationService.sendFcmToUser(order.getUserId(),
                    NotificationMessageType.EXCHANGE_ACCEPT,
                    NotificationMessage.builder().productName(product.getTitle()).build());
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
            OrderProductInfo info = orderService.selectOrderProductInfo(orderProductInfoId);
            Orders order = orderService.selectOrder(info.getOrderId());
            if (tokenInfo.get().getId() != order.getUserId()) return res.throwError("타인의 주문 내역입니다.", "NOT_ALLOWED");
            if (!info.getState().equals(OrderProductState.DELIVERY_DONE))
                return res.throwError("배송 완료 후 처리 가능합니다.", "INPUT_CHECK_REQUIRED");
            info.setState(OrderProductState.FINAL_CONFIRM);
            orderService.updateOrderProductInfo(new ArrayList<>(Arrays.asList(info)));
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
            OrderProductInfo info = orderService.selectOrderProductInfo(orderProductInfoId);
            Orders order = orderService.selectOrder(info.getOrderId());
            if (tokenInfo.get().getId() != order.getUserId()) return res.throwError("타인의 주문 내역입니다.", "NOT_ALLOWED");
            info.setState(OrderProductState.REFUND_REQUEST);
            orderService.updateOrderProductInfo(new ArrayList<>(Arrays.asList(info)));
            res.setData(Optional.of(true));
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
        Optional<TokenInfo>
                tokenInfo =
                jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.PARTNER, TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            OrderProductInfo info = orderService.selectOrderProductInfo(orderProductInfoId);
            Orders order = orderService.selectOrder(info.getOrderId());
            Product product = productService.selectProduct(info.getProductId());
            notificationService.sendFcmToUser(order.getUserId(),
                    NotificationMessageType.REFUND_REJECT,
                    NotificationMessage.builder().productName(product.getTitle()).build());
            info.setState(OrderProductState.DELIVERY_DONE);
            orderService.updateOrderProductInfo(new ArrayList<>(Arrays.asList(info)));
            res.setData(Optional.of(true));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    //반품 요청 확인
    @PostMapping("/refund/{orderProductInfoId}/confirm")
    public ResponseEntity<CustomResponse<Boolean>> confirmRefundOrderProduct(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                             @PathVariable("orderProductInfoId") Integer orderProductInfoId) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        Optional<TokenInfo>
                tokenInfo =
                jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.PARTNER, TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            OrderProductInfo info = orderService.selectOrderProductInfo(orderProductInfoId);
            Orders order = orderService.selectOrder(info.getOrderId());
            Product product = productService.selectProduct(info.getProductId());
            notificationService.sendFcmToUser(order.getUserId(),
                    NotificationMessageType.REFUND_ACCEPT,
                    NotificationMessage.builder().orderedAt(order.getOrderedAt()).productName(product.getTitle()).build());
            info.setState(OrderProductState.REFUND_ACCEPT);
            orderService.updateOrderProductInfo(new ArrayList<>(Arrays.asList(info)));
            res.setData(Optional.of(true));
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
        Optional<TokenInfo>
                tokenInfo =
                jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.PARTNER, TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            OrderProductInfo info = orderService.selectOrderProductInfo(orderProductInfoId);
            Orders order = orderService.selectOrder(info.getOrderId());
            Product product = productService.selectProduct(info.getProductId());
            info.setState(OrderProductState.REFUND_DONE);
            orderService.updateOrderProductInfo(new ArrayList<>(Arrays.asList(info)));
            res.setData(Optional.of(true));
            notificationService.sendFcmToUser(order.getUserId(),
                    NotificationMessageType.REFUND_DONE,
                    NotificationMessage.builder().productName(product.getTitle()).build());
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }
}

