package com.matsinger.barofishserver.order.api;

import com.matsinger.barofishserver.admin.log.application.AdminLogCommandService;
import com.matsinger.barofishserver.admin.log.application.AdminLogQueryService;
import com.matsinger.barofishserver.admin.log.domain.AdminLog;
import com.matsinger.barofishserver.admin.log.domain.AdminLogType;
import com.matsinger.barofishserver.coupon.application.CouponCommandService;
import com.matsinger.barofishserver.coupon.application.CouponQueryService;
import com.matsinger.barofishserver.coupon.domain.Coupon;
import com.matsinger.barofishserver.deliver.application.DeliverService;
import com.matsinger.barofishserver.deliver.domain.Deliver;
import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.notification.application.NotificationCommandService;
import com.matsinger.barofishserver.notification.dto.NotificationMessage;
import com.matsinger.barofishserver.notification.dto.NotificationMessageType;
import com.matsinger.barofishserver.order.application.OrderService;
import com.matsinger.barofishserver.order.domain.*;
import com.matsinger.barofishserver.order.dto.*;
import com.matsinger.barofishserver.order.orderprductinfo.domain.OrderProductInfo;
import com.matsinger.barofishserver.order.orderprductinfo.domain.OrderProductState;
import com.matsinger.barofishserver.payment.application.PaymentService;
import com.matsinger.barofishserver.payment.dto.KeyInPaymentReq;
import com.matsinger.barofishserver.product.application.ProductService;
import com.matsinger.barofishserver.product.domain.ProductState;
import com.matsinger.barofishserver.product.optionitem.domain.OptionItem;
import com.matsinger.barofishserver.product.domain.Product;
import com.matsinger.barofishserver.product.dto.ProductListDto;
import com.matsinger.barofishserver.siteInfo.application.SiteInfoQueryService;
import com.matsinger.barofishserver.siteInfo.domain.SiteInformation;
import com.matsinger.barofishserver.store.application.StoreService;
import com.matsinger.barofishserver.store.domain.StoreDeliverFeeType;
import com.matsinger.barofishserver.store.domain.StoreInfo;
import com.matsinger.barofishserver.user.paymentMethod.application.PaymentMethodService;
import com.matsinger.barofishserver.user.deliverplace.DeliverPlace;
import com.matsinger.barofishserver.user.paymentMethod.domain.PaymentMethod;
import com.matsinger.barofishserver.userinfo.domain.UserInfo;
import com.matsinger.barofishserver.user.application.UserCommandService;
import com.matsinger.barofishserver.utils.Common;
import com.matsinger.barofishserver.utils.CustomResponse;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
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
    private final UserCommandService userService;
    private final CouponQueryService couponQueryService;
    private final CouponCommandService couponCommandService;
    private final PaymentService paymentService;
    private final NotificationCommandService notificationCommandService;
    private final SiteInfoQueryService siteInfoQueryService;
    private final PaymentMethodService paymentMethodService;
    private final AdminLogCommandService adminLogCommandService;
    private final AdminLogQueryService adminLogQueryService;
    private final DeliverService deliverService;
    private final StoreService storeService;
    private final JwtService jwt;
    private final Common utils;


    @GetMapping("/point-rule")
    public ResponseEntity<CustomResponse<PointRuleRes>> selectPointRule(@RequestHeader(value = "Authorization") Optional<String> auth) {
        CustomResponse<PointRuleRes> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Integer userId = tokenInfo.get().getId();
            UserInfo userInfo = userService.selectUserInfo(userId);
            SiteInformation siteInfo = siteInfoQueryService.selectSiteInfo("INT_REVIEW_POINT_TEXT");
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
            res.setData(Optional.ofNullable(orderService.convert2Dto(order,
                    tokenInfo.get().getType().equals(TokenAuthType.PARTNER) ? tokenInfo.get().getId() : null,
                    null)));
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

                // query.groupBy(root.get("id"));
                query.distinct(true);
                if (state != null) {
                    predicates.add(root.get("productInfos").get("state").in(Arrays.stream(state.split(",")).map(
                            OrderProductState::valueOf).toList()));
                    Join<Orders, OrderProductInfo> t = root.join("productInfos", JoinType.INNER);
                    predicates.add(builder.isNotNull(t.get("id")));
                    predicates.add(builder.or(builder.notEqual(t.get("state"), OrderProductState.WAIT_DEPOSIT),
                            builder.and(root.get("paymentWay").in(List.of(OrderPaymentWay.DEPOSIT,
                                    OrderPaymentWay.VIRTUAL_ACCOUNT)))));
                    if (tokenInfo.get().getType().equals(TokenAuthType.PARTNER)) {
                        predicates.add(builder.equal(t.get("product").get("storeId"), tokenInfo.get().getId()));
                    }
                }
                // predicates.add(builder.isNotNull(root.join("productInfos").get("id")));
                // if (tokenInfo.get().getType().equals(TokenAuthType.PARTNER))
                // predicates.add(builder.)
                // predicates.add(builder.equal(root.get("storeId"), tokenInfo.get().getId()))
                return builder.and(predicates.toArray(new Predicate[0]));
            };
            Specification<OrderProductInfo> productSpec = (root, query, builder) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (state != null) {
                    predicates.add(root.get("state").in(Arrays.stream(state.split(",")).map(OrderProductState::valueOf).toList()));
                }
                if (tokenInfo.get().getType().equals(TokenAuthType.PARTNER))
                    predicates.add(builder.equal(root.get("product").get("storeId"), tokenInfo.get().getId()));

                return builder.and(predicates.toArray(new Predicate[0]));
            };
            PageRequest pageRequest = PageRequest.of(page, take, Sort.by(sort, orderBy.label));
            Page<OrderDto>
                    orders =
                    orderService.selectOrderLitByAdmin(pageRequest, spec).map(o -> orderService.convert2Dto(o,
                            tokenInfo.get().getType().equals(TokenAuthType.PARTNER) ? tokenInfo.get().getId() : null,
                            productSpec));
            res.setData(Optional.of(orders));
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
            List<OrderDto>
                    orders =
                    orderService.selectOrderList(userId, pageRequest).stream().map(o -> orderService.convert2Dto(o,
                            null,
                            null)).toList();
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
                    orderService.selectOrderList(tokenInfo.get().getId(),
                            pageRequest).stream().map(o -> orderService.convert2Dto(o, null, null)).toList();

            res.setData(Optional.of(orders));
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
            List<OrderDto>
                    orders =
                    orderService.selectCanceledOrderList(tokenInfo.get().getId()).stream().map(o -> orderService.convert2Dto(
                            o,
                            null,
                            null)).toList();
            res.setData(Optional.of(orders));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/bank-code/list")
    public ResponseEntity<CustomResponse<List<BankCode>>> selectBankCodeList() {
        CustomResponse<List<BankCode>> res = new CustomResponse<>();
        try {
            List<BankCode> bankCodes = orderService.selectBankCodeList();
            res.setData(Optional.ofNullable(bankCodes));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
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
            UserInfo userInfo = userService.selectUserInfo(userId);
            VBankRefundInfo vBankRefundInfo = null;
            if (data.getPaymentWay().equals(OrderPaymentWay.VIRTUAL_ACCOUNT)) {
                if (data.getVbankRefundInfo() == null)
                    return res.throwError("가상계좌 환불 정보를 입력해주세요.", "INPUT_CHECK_REQUIRED");
                if (data.getVbankRefundInfo().getBankCodeId() == null)
                    return res.throwError("은행 코드 아이디를 입력해주세요.", "INPUT_CHECK_REQUIRED");
                BankCode bankCode = orderService.selectBankCode(data.getVbankRefundInfo().getBankCodeId());
                String bankHolder = utils.validateString(data.getVbankRefundInfo().getBankHolder(), 20L, "환불 예금주명");
                String bankAccount = data.getVbankRefundInfo().getBankAccount().replaceAll("-", "");
                bankAccount = utils.validateString(bankAccount, 30L, "환불 계좌번호");
                vBankRefundInfo =
                        VBankRefundInfo.builder().bankHolder(bankHolder).bankCode(bankCode.getCode()).bankName(bankCode.getName()).bankAccount(
                                bankAccount).build();
            }
            Coupon coupon = null;
            if (data.getCouponId() != null) coupon = couponQueryService.selectCoupon(data.getCouponId());
            if (data.getPoint() != null && userInfo.getPoint() < data.getPoint())
                return res.throwError("보유한 적립금보다 많은 적립금입니다.", "INPUT_CHECK_REQUIRED");
            List<OrderProductInfo> infos = new ArrayList<>();
            List<OptionItem> optionItems = new ArrayList<>();
            int taxFreeAmount = 0;
            for (OrderProductReq productReq : data.getProducts()) {
                Product product = productService.selectProduct(productReq.getProductId());
                StoreInfo storeInfo = storeService.selectStoreInfo(product.getStoreId());
                if (!product.getState().equals(ProductState.ACTIVE)) {
                    if (product.getState().equals(ProductState.SOLD_OUT))
                        return res.throwError("품절된 상품입니다.", "NOT_ALLOWED");
                    if (product.getState().equals(ProductState.DELETED))
                        return res.throwError("주문 불가능한 상품입니다.", "NOT_ALLOWED");
                    if (product.getState().equals(ProductState.INACTIVE))
                        return res.throwError("주문 불가능한 상품입니다.", "NOT_ALLOWED");
                }

                OptionItem optionItem = productService.selectOptionItem(productReq.getOptionItemId());
                if (optionItem.getDeliverBoxPerAmount() != null &&
                        optionItem.getMaxAvailableAmount() < productReq.getAmount())
                    return res.throwError("최대 주문 수량을 초과하였습니다.", "INPUT_CHECK_REQUIRED");
                optionItem.reduceAmount(productReq.getAmount());
                int price = orderService.getProductPrice(product, productReq.getOptionItemId(), productReq.getAmount());
                if (!product.getNeedTaxation()) {
                    taxFreeAmount += price;
                }
                Integer
                        deliveryFee =
                        orderService.getProductDeliveryFee(product, productReq.getOptionItemId(), productReq.getAmount());
                optionItems.add(productService.selectOptionItem(productReq.getOptionItemId()));
                infos.add(OrderProductInfo.builder().optionItemId(optionItem.getId()).orderId(orderId).productId(
                        productReq.getProductId()).state(OrderProductState.WAIT_DEPOSIT).settlePrice(storeInfo.getSettlementRate() !=
                        null ? (int) ((storeInfo.getSettlementRate() / 100.) *
                        optionItem.getPurchasePrice()) : optionItem.getPurchasePrice()).price(price).amount(productReq.getAmount()).isSettled(
                        false).deliveryFee(deliveryFee).build());
            }
            infos.forEach(i -> {
                int storeTotalPrice = infos.stream().filter(v -> {
                    Product productA = productService.selectProduct(v.getProductId());
                    Product productB = productService.selectProduct(i.getProductId());
                    return productA.getStoreId() == productB.getStoreId();
                }).mapToInt(OrderProductInfo::getPrice).sum();
                Product p = productService.selectProduct(i.getProductId());
                StoreInfo storeInfo = storeService.selectStoreInfo(p.getStoreId());
                if (storeInfo.getDeliverFeeType().equals(StoreDeliverFeeType.FREE_IF_OVER) &&
                        storeTotalPrice > (storeInfo.getMinOrderPrice() != null ? storeInfo.getMinOrderPrice() : 0)) {
                    i.setDeliveryFee(0);
                }
            });
            if (coupon != null) {
                if (data.getTotalPrice() < coupon.getMinPrice())
                    return res.throwError("쿠폰 최소 금액에 맞지 않습니다.", "INPUT_CHECK_REQUIRED");
                couponQueryService.checkValidCoupon(coupon.getId(), userId);

            }
            if (!Objects.equals(data.getTotalPrice(), data.getTotalPrice()))
                return res.throwError("총 금액을 확인해주세요.", "INPUT_CHECK_REQUIRED");
            if (data.getDeliverPlaceId() == null) return res.throwError("배송지를 입력해주세요.", "INPUT_CHECK_REQUIRED");
            DeliverPlace deliverPlace = userService.selectDeliverPlace(data.getDeliverPlaceId());
            OrderDeliverPlace
                    orderDeliverPlace =
                    OrderDeliverPlace.builder().orderId(orderId).name(deliverPlace.getName()).receiverName(deliverPlace.getReceiverName()).tel(
                            deliverPlace.getTel()).address(deliverPlace.getAddress()).addressDetail(deliverPlace.getAddressDetail()).deliverMessage(
                            deliverPlace.getDeliverMessage()).postalCode(deliverPlace.getPostalCode()).bcode(
                            deliverPlace.getBcode()).build();
            Orders
                    order =
                    Orders.builder().id(orderId).userId(tokenInfo.get().getId()).paymentWay(data.getPaymentWay()).state(
                            OrderState.WAIT_DEPOSIT).couponId(data.getCouponId()).orderedAt(utils.now()).totalPrice(data.getTotalPrice()).usePoint(
                            data.getPoint()).couponDiscount(data.getCouponDiscountPrice()).ordererName(name).ordererTel(
                            tel).bankHolder(vBankRefundInfo != null ? vBankRefundInfo.getBankHolder() : null).bankCode(
                            vBankRefundInfo != null ? vBankRefundInfo.getBankCode() : null).bankName(vBankRefundInfo !=
                            null ? vBankRefundInfo.getBankName() : null).bankAccount(vBankRefundInfo !=
                            null ? vBankRefundInfo.getBankAccount() : null).build();

            Orders result = orderService.orderProduct(order, infos, orderDeliverPlace);

//            taxFreeAmount = taxFreeAmount - data.getCouponDiscountPrice() + data.getPoint();
            taxFreeAmount = orderService.getTaxFreeAmount(result, null);
            if (data.getPaymentWay().equals(OrderPaymentWay.KEY_IN)) {
                PaymentMethod paymentMethod = paymentMethodService.selectPaymentMethod(data.getPaymentMethodId());
                Product product = productService.selectProduct(data.getProducts().get(0).getProductId());
                KeyInPaymentReq
                        req =
                        KeyInPaymentReq.builder().paymentMethod(paymentMethod).order_name(name).orderId(orderId).total_amount(
                                data.getTotalPrice()).order_name(product.getTitle()).taxFree(taxFreeAmount).build();
                Boolean keyInResult = paymentService.processKeyInPayment(req);
                if (!keyInResult) return res.throwError("결제에 실패하였습니다.", "NOT_ALLOWED");
            }
            res.setData(Optional.ofNullable(orderService.convert2Dto(result, null, null)));
            if (data.getTotalPrice() == 0) {
                orderService.processOrderZeroAmount(result);
            }
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
            List<ProductListDto> productListDtos = products.stream().map(productService::convert2ListDto).toList();
            res.setData(Optional.of(productListDtos));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    // PROCESS
    // 무통장 입금 확인
    @PostMapping("/confirm-deposit/{orderId}")
    public ResponseEntity<CustomResponse<Boolean>> confirmDeposit(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                  @PathVariable("orderId") String orderId) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        Optional<TokenInfo>
                tokenInfo =
                jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.PARTNER, TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Integer adminId = null;
            if (tokenInfo.get().getType().equals(TokenAuthType.ADMIN)) adminId = tokenInfo.get().getId();
            Orders order = orderService.selectOrder(orderId);
            if (!order.getPaymentWay().equals(OrderPaymentWay.DEPOSIT))
                return res.throwError("무통장 입금의 경우만 확인 처리 가능합니다.", "NOT_ALLOWED");
            List<OrderProductInfo> infos = orderService.selectOrderProductInfoListWithOrderId(orderId);
            infos.forEach(v -> {
                Product product = productService.selectProduct(v.getProductId());
                if ((tokenInfo.get().getType().equals(TokenAuthType.PARTNER) &&
                        tokenInfo.get().getId() == product.getStoreId()) ||
                        (tokenInfo.get().getType().equals(TokenAuthType.ADMIN)))
                    v.setState(OrderProductState.PAYMENT_DONE);
            });
            orderService.updateOrderProductInfos(infos);
            if (adminId != null) {
                String content = "무통장 입금 확인하였습니다.";
                AdminLog
                        adminLog =
                        AdminLog.builder().id(adminLogQueryService.getAdminLogId()).adminId(adminId).type(AdminLogType.ORDER).targetId(
                                orderId).content(content).createdAt(utils.now()).build();
                adminLogCommandService.saveAdminLog(adminLog);
            }
            res.setData(Optional.of(true));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    // 결제 취소


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
            String content = null;
            if (data.getContent() != null) content = utils.validateString(data.getContent(), 1000L, "사유");
            info.setCancelReason(data.getCancelReason());
            info.setCancelReasonContent(content);
            Product product = productService.selectProduct(info.getProductId());
            orderService.requestCancelOrderProduct(info.getId());
            notificationCommandService.sendFcmToUser(tokenInfo.get().getId(),
                    NotificationMessageType.ORDER_CANCEL,
                    NotificationMessage.builder().productName(product.getTitle()).isCanceledByRegion(false).build());
            orderService.updateOrderProductInfo(new ArrayList<>(List.of(info)));
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
            Integer adminId = null;
            if (tokenInfo.get().getType().equals(TokenAuthType.ADMIN)) adminId = null;
            OrderProductInfo info = orderService.selectOrderProductInfo(orderProductInfoId);
            orderService.cancelOrderedProduct(orderProductInfoId);
            res.setData(Optional.of(true));
            Orders order = orderService.selectOrder(info.getOrderId());
            Product product = productService.findById(info.getProductId());
            info.setState(OrderProductState.CANCELED);
            orderService.updateOrderProductInfo(new ArrayList<>(List.of(info)));
            notificationCommandService.sendFcmToUser(order.getUserId(),
                    NotificationMessageType.ORDER_CANCEL,
                    NotificationMessage.builder().productName(product.getTitle()).build());
            if (adminId != null) {
                String content = product.getTitle() + " 주문이 취소 처리되었습니다.";
                AdminLog
                        adminLog =
                        AdminLog.builder().id(adminLogQueryService.getAdminLogId()).adminId(adminId).type(AdminLogType.ORDER).targetId(
                                order.getId()).content(content).createdAt(utils.now()).build();
                adminLogCommandService.saveAdminLog(adminLog);
            }
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    // 결제 취소 거절
    @PostMapping("/cancel/{orderProductInfoId}/reject")
    public ResponseEntity<CustomResponse<Boolean>> rejectCancelOrder(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                     @PathVariable("orderProductInfoId") Integer orderProductInfoId) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        Optional<TokenInfo>
                tokenInfo =
                jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.PARTNER, TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Integer adminId = null;
            if (tokenInfo.get().getType().equals(TokenAuthType.ADMIN)) adminId = tokenInfo.get().getId();
            OrderProductInfo info = orderService.selectOrderProductInfo(orderProductInfoId);
            if (!info.getState().equals(OrderProductState.CANCEL_REQUEST))
                return res.throwError("취소 신청된 상품이 아닙니다.", "INPUT_CHECK_REQUIRED");
            info.setState(OrderProductState.DELIVERY_READY);
            orderService.updateOrderProductInfos(new ArrayList<>(List.of(info)));
            Orders order = orderService.selectOrder(info.getOrderId());
            Product product = productService.findById(info.getProductId());
            notificationCommandService.sendFcmToUser(order.getUserId(),
                    NotificationMessageType.CANCEL_REJECT,
                    NotificationMessage.builder().productName(product.getTitle()).build());
            if (adminId != null) {
                String content = product.getTitle() + " 주문 취소 신청이 반려 처리되었습니다.";
                AdminLog
                        adminLog =
                        AdminLog.builder().id(adminLogQueryService.getAdminLogId()).adminId(adminId).type(AdminLogType.ORDER).targetId(
                                order.getId()).content(content).createdAt(utils.now()).build();
                adminLogCommandService.saveAdminLog(adminLog);
            }
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
            Integer adminId = null;
            if (tokenInfo.get().getType().equals(TokenAuthType.ADMIN)) adminId = tokenInfo.get().getId();
            OrderProductInfo info = orderService.selectOrderProductInfo(orderProductInfoId);
            if (!info.getState().equals(OrderProductState.CANCEL_REQUEST))
                return res.throwError("취소 신청된 상품이 아닙니다.", "INPUT_CHECK_REQUIRED");
            orderService.cancelOrderedProduct(orderProductInfoId);
            info.setState(OrderProductState.CANCELED);
            orderService.updateOrderProductInfos(List.of(info));
            if (adminId != null) {
                Product product = productService.selectProduct(info.getProductId());
                String content = product.getTitle() + " 주문이 취소 처리되었습니다.";
                AdminLog
                        adminLog =
                        AdminLog.builder().id(adminLogQueryService.getAdminLogId()).adminId(adminId).type(AdminLogType.ORDER).targetId(
                                info.getOrderId()).content(content).createdAt(utils.now()).build();
                adminLogCommandService.saveAdminLog(adminLog);
            }
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @PostMapping("/deliver-ready/{orderProductInfoId}")
    public ResponseEntity<CustomResponse<Boolean>> deliverReady(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                @PathVariable("orderProductInfoId") Integer orderProductInfoId) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        Optional<TokenInfo>
                tokenInfo =
                jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.PARTNER, TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Integer adminId = null;
            if (tokenInfo.get().getType().equals(TokenAuthType.ADMIN)) adminId = tokenInfo.get().getId();
            OrderProductInfo info = orderService.selectOrderProductInfo(orderProductInfoId);
            info.setState(OrderProductState.DELIVERY_READY);
            orderService.updateOrderProductInfo(new ArrayList<>(List.of(info)));
            if (adminId != null) {
                Product product = productService.selectProduct(info.getProductId());
                String content = product.getTitle() + " 주문이 발송 준비 처리되었습니다.";
                AdminLog
                        adminLog =
                        AdminLog.builder().id(adminLogQueryService.getAdminLogId()).adminId(adminId).type(AdminLogType.ORDER).targetId(
                                info.getOrderId()).content(content).createdAt(utils.now()).build();
                adminLogCommandService.saveAdminLog(adminLog);
            }
            res.setData(Optional.of(true));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    // 발송 처리


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
            Integer adminId = null;
            if (tokenInfo.get().getType().equals(TokenAuthType.ADMIN)) adminId = tokenInfo.get().getId();
            OrderProductInfo info = orderService.selectOrderProductInfo(orderProductInfoId);
            if (!info.getState().equals(OrderProductState.DELIVERY_READY) &&
                    !info.getState().equals(OrderProductState.ON_DELIVERY) &&
                    !info.getState().equals(OrderProductState.EXCHANGE_ACCEPT))
                return res.throwError("변경 불가능한 상태입니다.", "NOT_ALLOWED");
            if (data.getDeliverCompanyCode() == null) return res.throwError("택배사 코드를 입력해주세요.", "INPUT_CHECK_REQUIRED");
            if (data.getInvoice() == null) return res.throwError("운송장 번호를 입력해주세요.", "INPUT_CHECK_REQUIRED");
            Deliver.TrackingInfo
                    trackingInfo =
                    deliverService.selectTrackingInfo(data.getDeliverCompanyCode(), data.getInvoice());
            if (trackingInfo == null) return res.throwError("유효하지 않은 운송장 번호입니다.", "NOT_ALLOWED");
            info.setDeliverCompanyCode(data.getDeliverCompanyCode());
            info.setInvoiceCode(data.getInvoice());
            info.setState(OrderProductState.ON_DELIVERY);
            orderService.updateOrderProductInfo(new ArrayList<>(List.of(info)));
            Orders order = orderService.selectOrder(info.getOrderId());
            Product product = productService.selectProduct(info.getProductId());
            notificationCommandService.sendFcmToUser(order.getUserId(),
                    NotificationMessageType.DELIVER_READY,
                    NotificationMessage.builder().productName(product.getTitle()).build());
            if (adminId != null) {
                String content = product.getTitle() + " 주문이 발송 처리 되었습니다.";
                AdminLog
                        adminLog =
                        AdminLog.builder().id(adminLogQueryService.getAdminLogId()).adminId(adminId).type(AdminLogType.ORDER).targetId(
                                order.getId()).content(content).createdAt(utils.now()).build();
                adminLogCommandService.saveAdminLog(adminLog);
            }
            res.setData(Optional.of(true));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    // 교환 신청


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
            if (data.getReasonContent() == null) return res.throwError("교환 사유를 입력해주세요.", "INPUT_CHECK_REQUIRED");
            String content = data.getReasonContent().trim();
            info.setState(OrderProductState.EXCHANGE_REQUEST);
            orderService.updateOrderProductInfo(new ArrayList<>(List.of(info)));
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
            Integer adminId = null;
            if (tokenInfo.get().getType().equals(TokenAuthType.ADMIN)) adminId = tokenInfo.get().getId();
            OrderProductInfo info = orderService.selectOrderProductInfo(orderProductInfoId);
            if (!info.getState().equals(OrderProductState.EXCHANGE_REQUEST))
                return res.throwError("교환 요청된 주문이 아닙니다.", "INPUT_CHECK_REQUIRED");
            info.setState(OrderProductState.DELIVERY_DONE);
            orderService.updateOrderProductInfo(new ArrayList<>(List.of(info)));
            Orders order = orderService.selectOrder(info.getOrderId());
            Product product = productService.findById(info.getProductId());
            notificationCommandService.sendFcmToUser(order.getUserId(),
                    NotificationMessageType.EXCHANGE_REJECT,
                    NotificationMessage.builder().productName(product.getTitle()).build());
            if (adminId != null) {
                String content = product.getTitle() + " 주문 교환 신청이 반려 처리되었습니다.";
                AdminLog
                        adminLog =
                        AdminLog.builder().id(adminLogQueryService.getAdminLogId()).adminId(adminId).type(AdminLogType.ORDER).targetId(
                                order.getId()).content(content).createdAt(utils.now()).build();
                adminLogCommandService.saveAdminLog(adminLog);
            }
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
            Integer adminId = null;
            if (tokenInfo.get().getType().equals(TokenAuthType.ADMIN)) adminId = tokenInfo.get().getId();
            OrderProductInfo info = orderService.selectOrderProductInfo(orderProductInfoId);
            if (!info.getState().equals(OrderProductState.EXCHANGE_REQUEST))
                return res.throwError("교환 요청된 주문이 아닙니다.", "INPUT_CHECK_REQUIRED");
            info.setState(OrderProductState.EXCHANGE_ACCEPT);
            orderService.updateOrderProductInfo(new ArrayList<>(List.of(info)));
            Orders order = orderService.selectOrder(info.getOrderId());
            Product product = productService.findById(info.getProductId());
            notificationCommandService.sendFcmToUser(order.getUserId(),
                    NotificationMessageType.EXCHANGE_ACCEPT,
                    NotificationMessage.builder().productName(product.getTitle()).build());
            if (adminId != null) {
                String content = product.getTitle() + " 주문 교환 신청이 처리되었습니다.";
                AdminLog
                        adminLog =
                        AdminLog.builder().id(adminLogQueryService.getAdminLogId()).adminId(adminId).type(AdminLogType.ORDER).targetId(
                                order.getId()).content(content).createdAt(utils.now()).build();
                adminLogCommandService.saveAdminLog(adminLog);
            }
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    // 구매 확정
    @PostMapping("/confirm/{orderProductInfoId}")
    public ResponseEntity<CustomResponse<Boolean>> confirmOrderProduct(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                       @PathVariable("orderProductInfoId") Integer orderProductInfoId) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Integer userId = tokenInfo.get().getId();
            OrderProductInfo info = orderService.selectOrderProductInfo(orderProductInfoId);
            Orders order = orderService.selectOrder(info.getOrderId());
            if (userId != order.getUserId()) return res.throwError("타인의 주문 내역입니다.", "NOT_ALLOWED");
            if (!info.getState().equals(OrderProductState.DELIVERY_DONE))
                return res.throwError("배송 완료 후 처리 가능합니다.", "INPUT_CHECK_REQUIRED");
            UserInfo userInfo = userService.selectUserInfo(userId);
            Product product = productService.selectProduct(info.getProductId());
            float pointRate = product.getPointRate() != null ? product.getPointRate() : 0;
            Integer point = (int) Math.floor(info.getPrice() * info.getAmount() * pointRate);
            userInfo.setPoint(userInfo.getPoint() + point);
            info.setState(OrderProductState.FINAL_CONFIRM);
            info.setFinalConfirmedAt(utils.now());
            if (point != 0) userService.updateUserInfo(userInfo);
            orderService.updateOrderProductInfo(new ArrayList<>(List.of(info)));
            couponCommandService.publishSystemCoupon(userInfo.getUserId());
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    // 반품 요청
    @PostMapping("/refund/{orderProductInfoId}/request")
    public ResponseEntity<CustomResponse<Boolean>> requestRefundOrderProduct(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                             @PathVariable("orderProductInfoId") Integer orderProductInfoId,
                                                                             @RequestPart(value = "data") RequestCancelReq data) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            OrderProductInfo info = orderService.selectOrderProductInfo(orderProductInfoId);
            Orders order = orderService.selectOrder(info.getOrderId());
            if (tokenInfo.get().getId() != order.getUserId()) return res.throwError("타인의 주문 내역입니다.", "NOT_ALLOWED");
            info.setState(OrderProductState.REFUND_REQUEST);
            if (data.getCancelReason() == null) return res.throwError("취소/환불 사유를 선택해주세요.", "INPUT_CHECK_REQUIRED");
            String content = null;
            if (data.getContent() != null) content = utils.validateString(data.getContent(), 1000L, "사유");
            info.setCancelReason(data.getCancelReason());
            info.setCancelReasonContent(content);
            orderService.updateOrderProductInfo(new ArrayList<>(List.of(info)));
            res.setData(Optional.of(true));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    // 반품 요청 거절
    @PostMapping("/refund/{orderProductInfoId}/reject")
    public ResponseEntity<CustomResponse<Boolean>> rejectRefundOrderProduct(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                            @PathVariable("orderProductInfoId") Integer orderProductInfoId) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        Optional<TokenInfo>
                tokenInfo =
                jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.PARTNER, TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Integer adminId = null;
            if (tokenInfo.get().getType().equals(TokenAuthType.ADMIN)) adminId = tokenInfo.get().getId();
            OrderProductInfo info = orderService.selectOrderProductInfo(orderProductInfoId);
            Orders order = orderService.selectOrder(info.getOrderId());
            Product product = productService.findById(info.getProductId());
            notificationCommandService.sendFcmToUser(order.getUserId(),
                    NotificationMessageType.REFUND_REJECT,
                    NotificationMessage.builder().productName(product.getTitle()).build());
            info.setState(OrderProductState.DELIVERY_DONE);
            orderService.updateOrderProductInfo(new ArrayList<>(List.of(info)));
            if (adminId != null) {
                String content = product.getTitle() + " 주문 반품 신청이 반려되었습니다.";
                AdminLog
                        adminLog =
                        AdminLog.builder().id(adminLogQueryService.getAdminLogId()).adminId(adminId).type(AdminLogType.ORDER).targetId(
                                order.getId()).content(content).createdAt(utils.now()).build();
                adminLogCommandService.saveAdminLog(adminLog);
            }
            res.setData(Optional.of(true));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    // 반품 요청 확인
    @PostMapping("/refund/{orderProductInfoId}/confirm")
    public ResponseEntity<CustomResponse<Boolean>> confirmRefundOrderProduct(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                             @PathVariable("orderProductInfoId") Integer orderProductInfoId) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        Optional<TokenInfo>
                tokenInfo =
                jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.PARTNER, TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Integer adminId = null;
            if (tokenInfo.get().getType().equals(TokenAuthType.ADMIN)) adminId = tokenInfo.get().getId();
            OrderProductInfo info = orderService.selectOrderProductInfo(orderProductInfoId);
            Orders order = orderService.selectOrder(info.getOrderId());
            Product product = productService.findById(info.getProductId());
            notificationCommandService.sendFcmToUser(order.getUserId(),
                    NotificationMessageType.REFUND_ACCEPT,
                    NotificationMessage.builder().orderedAt(order.getOrderedAt()).productName(product.getTitle()).build());
            info.setState(OrderProductState.REFUND_ACCEPT);
            orderService.updateOrderProductInfo(new ArrayList<>(List.of(info)));
            res.setData(Optional.of(true));
            if (adminId != null) {
                String content = product.getTitle() + " 주문 반품 신청이 처리되었습니다.";
                AdminLog
                        adminLog =
                        AdminLog.builder().id(adminLogQueryService.getAdminLogId()).adminId(adminId).type(AdminLogType.ORDER).targetId(
                                order.getId()).content(content).createdAt(utils.now()).build();
                adminLogCommandService.saveAdminLog(adminLog);
            }
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    // 반품 완료
    @PostMapping("/refund/{orderProductInfoId}/done")
    public ResponseEntity<CustomResponse<Boolean>> doneRefundOrderProduct(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                          @PathVariable("orderProductInfoId") Integer orderProductInfoId) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        Optional<TokenInfo>
                tokenInfo =
                jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.PARTNER, TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Integer adminId = null;
            if (tokenInfo.get().getType().equals(TokenAuthType.ADMIN)) adminId = tokenInfo.get().getId();
            OrderProductInfo info = orderService.selectOrderProductInfo(orderProductInfoId);
            Orders order = orderService.selectOrder(info.getOrderId());
            Product product = productService.findById(info.getProductId());
            info.setState(OrderProductState.REFUND_DONE);
            orderService.cancelOrderedProduct(info.getId());
            orderService.updateOrderProductInfo(new ArrayList<>(List.of(info)));
            res.setData(Optional.of(true));
            notificationCommandService.sendFcmToUser(order.getUserId(),
                    NotificationMessageType.REFUND_DONE,
                    NotificationMessage.builder().productName(product.getTitle()).build());
            if (adminId != null) {
                String content = product.getTitle() + " 주문 반품 신청이 완료 처리되었습니다.";
                AdminLog
                        adminLog =
                        AdminLog.builder().id(adminLogQueryService.getAdminLogId()).adminId(adminId).type(AdminLogType.ORDER).targetId(
                                order.getId()).content(content).createdAt(utils.now()).build();
                adminLogCommandService.saveAdminLog(adminLog);
            }
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }
}
