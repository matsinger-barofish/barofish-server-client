package com.matsinger.barofishserver.coupon;

import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.utils.Common;
import com.matsinger.barofishserver.utils.CustomResponse;
import jakarta.persistence.criteria.Predicate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
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
@RequestMapping("/api/v1/coupon")
public class CouponController {
    private final CouponService couponService;
    private final JwtService jwt;
    private final Common utils;

    @GetMapping("/management")
    public ResponseEntity<CustomResponse<Page<Coupon>>> selectCouponListByAdmin(@RequestHeader(value = "Authorization", required = false) Optional<String> auth,
                                                                                @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                                                                @RequestParam(value = "take", required = false, defaultValue = "10") Integer take,
                                                                                @RequestParam(value = "orderby", defaultValue = "id") CouponOrderBy orderBy,
                                                                                @RequestParam(value = "orderType", defaultValue = "DESC") Sort.Direction sort,
                                                                                @RequestParam(value = "title", required = false) String title,
                                                                                @RequestParam(value = "type", required = false) String type,
                                                                                @RequestParam(value = "startAtS", required = false) Timestamp startAtS,
                                                                                @RequestParam(value = "startAtE", required = false) Timestamp startAtE,
                                                                                @RequestParam(value = "endAtS", required = false) Timestamp endAtS,
                                                                                @RequestParam(value = "endAtE", required = false) Timestamp endAtE) {
        CustomResponse<Page<Coupon>> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Specification<Coupon> spec = (root, query, builder) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (title != null) predicates.add(builder.like(root.get("title"), "%" + title + "%"));
                if (type != null)
                    predicates.add(builder.and(root.get("type").in(Arrays.stream(type.split(",")).map(CouponType::valueOf).toList())));
                if (startAtS != null) predicates.add(builder.greaterThan(root.get("startAt"), startAtS));
                if (startAtE != null) predicates.add(builder.lessThan(root.get("startAt"), startAtE));
                if (endAtS != null) predicates.add(builder.greaterThan(root.get("endAt"), endAtS));
                if (endAtE != null) predicates.add(builder.lessThan(root.get("endAt"), endAtE));
                predicates.add(builder.equal(root.get("state"), CouponState.ACTIVE));
                return builder.and(predicates.toArray(new Predicate[0]));
            };
            PageRequest pageRequest = PageRequest.of(page, take, Sort.by(sort, orderBy.label));
            Page<Coupon> coupons = couponService.selectCouponListByAdmin(pageRequest, spec);
            res.setData(Optional.ofNullable(coupons));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomResponse<Coupon>> selectCoupon(@PathVariable("id") Integer id) {
        CustomResponse<Coupon> res = new CustomResponse<>();
        try {
            Coupon coupon = couponService.selectCoupon(id);
            res.setData(Optional.ofNullable(coupon));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/can-use")
    public ResponseEntity<CustomResponse<List<Coupon>>> selectCanUseCoupon(@RequestHeader(value = "Authorization") Optional<String> auth) {
        CustomResponse<List<Coupon>> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            List<Coupon> coupons = couponService.selectCanUseCoupon(tokenInfo.get().getId());
            res.setData(Optional.ofNullable(coupons));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/can-download")
    public ResponseEntity<CustomResponse<List<Coupon>>> selectCanDownloadCoupon(@RequestHeader(value = "Authorization") Optional<String> auth) {
        CustomResponse<List<Coupon>> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            List<Coupon> coupons = couponService.selectNotDownloadCoupon(tokenInfo.get().getId());
            res.setData(Optional.ofNullable(coupons));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/downloaded")
    public ResponseEntity<CustomResponse<List<Coupon>>> selectDownloadedCoupon(@RequestHeader(value = "Authorization") Optional<String> auth) {
        CustomResponse<List<Coupon>> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            List<Coupon> coupons = couponService.selectDownloadedCoupon(tokenInfo.get().getId());
            res.setData(Optional.ofNullable(coupons));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @PostMapping("/download/{id}")
    public ResponseEntity<CustomResponse<Boolean>> selectDownloadCoupon(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                        @PathVariable("id") Integer id) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Boolean checkDownloaded = couponService.checkHasCoupon(id, tokenInfo.get().getId());
            if (checkDownloaded) return res.throwError("이미 다운로드 받은 쿠폰입니다.", "INPUT_CHECK_REQUIRED");
            couponService.downloadCoupon(tokenInfo.get().getId(), id);
            res.setData(Optional.of(true));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    private static class CouponAddReq {
        String title;
        CouponType type;
        Integer amount;
        Timestamp startAt;
        Timestamp endAt;
        Integer minPrice;
    }

    @PostMapping("add")
    public ResponseEntity<CustomResponse<Coupon>> addCoupon(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                            @RequestPart(value = "data") CouponAddReq data) {
        CustomResponse<Coupon> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            String title = utils.validateString(data.getTitle(), 100L, "제목");
            if (data.getType() == null) return res.throwError("할인 유형을 입력해주세요.", "INPUT_CHECK_REQUIRED");
            if (data.getType().equals(CouponType.RATE)) {
                if (data.getAmount() > 100) return res.throwError("할인율을 100%를 넘을 수 없습니다.", "INPUT_CHECK_REQUIRED");
            } else {
                if (data.getAmount() > data.getMinPrice())
                    return res.throwError("할인 금액이 주문 최소 금액을 넘을 수 없습니다.", "INPUT_CHECK_REQUIRED");
            }
            if (data.getAmount() < 0) return res.throwError("할인율을 확인해주세요.", "INPUT_CHECK_REQUIRED");
            if (data.getMinPrice() == null) data.setMinPrice(0);
            if (data.getStartAt() == null) return res.throwError("사용 가능 시작 기간을 입력해주세요.", " INPUT_CHECK_REQUIRED");
            Coupon
                    coupon =
                    Coupon.builder().title(title).type(data.getType()).amount(data.getAmount()).startAt(data.getStartAt()).endAt(
                            data.getEndAt()).minPrice(data.getMinPrice()).state(CouponState.ACTIVE).build();
            coupon = couponService.addCoupon(coupon);
            couponService.sendCouponCreateNotification(coupon);
            res.setData(Optional.ofNullable(coupon));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponse<Boolean>> deleteCoupon(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                @PathVariable("id") Integer id) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Coupon coupon = couponService.selectCoupon(id);
            coupon.setState(CouponState.DELETED);
            couponService.updateCoupon(coupon);
            res.setData(Optional.of(true));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

}
