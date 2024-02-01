package com.matsinger.barofishserver.domain.coupon.api;

import com.matsinger.barofishserver.domain.admin.log.application.AdminLogCommandService;
import com.matsinger.barofishserver.domain.admin.log.application.AdminLogQueryService;
import com.matsinger.barofishserver.domain.admin.log.domain.AdminLog;
import com.matsinger.barofishserver.domain.admin.log.domain.AdminLogType;
import com.matsinger.barofishserver.domain.coupon.application.CouponCommandService;
import com.matsinger.barofishserver.domain.coupon.application.CouponQueryService;
import com.matsinger.barofishserver.domain.coupon.domain.*;
import com.matsinger.barofishserver.domain.coupon.dto.CouponAddReq;
import com.matsinger.barofishserver.domain.coupon.dto.CouponDeleteRequest;
import com.matsinger.barofishserver.domain.coupon.dto.CouponDto;
import com.matsinger.barofishserver.domain.coupon.dto.UpdateSystemCoupon;
import com.matsinger.barofishserver.domain.user.application.UserCommandService;
import com.matsinger.barofishserver.domain.user.domain.User;
import com.matsinger.barofishserver.domain.userinfo.domain.UserInfo;
import com.matsinger.barofishserver.domain.userinfo.dto.UserInfoDto;
import com.matsinger.barofishserver.global.exception.BusinessException;
import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.utils.Common;
import com.matsinger.barofishserver.utils.CustomResponse;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
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
    private final CouponQueryService couponQueryService;
    private final CouponCommandService couponCommandService;
    private final AdminLogQueryService adminLogQueryService;
    private final AdminLogCommandService adminLogCommandService;
    private final UserCommandService userCommandService;
    private final JwtService jwt;
    private final Common utils;

    @GetMapping("/management")
    public ResponseEntity<CustomResponse<Page<CouponDto>>> selectCouponListByAdmin(@RequestHeader(value = "Authorization", required = false) Optional<String> auth,
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
        CustomResponse<Page<CouponDto>> res = new CustomResponse<>();

                jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);

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
//                predicates.add(builder.notEqual(root.get("publicType"), CouponPublicType.PRIVATE));
            return builder.and(predicates.toArray(new Predicate[0]));
        };
        PageRequest pageRequest = PageRequest.of(page, take, Sort.by(sort, orderBy.label));
        Page<CouponDto> coupons = couponQueryService.selectCouponListByAdmin(pageRequest, spec).map(v -> {
            List<UserInfoDto> users = null;
            if (v.getPublicType().equals(CouponPublicType.PRIVATE)) {
                List<Integer> userIds = couponQueryService.selectPublishedCouponUserIds(v.getId());
                users =
                        userCommandService.selectUserInfoListWithIds(userIds).stream().map(UserInfo::convert2Dto).toList();
            }
            return v.convert2Dto(users);
        });
        res.setData(Optional.ofNullable(coupons));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomResponse<CouponDto>> selectCoupon(@PathVariable("id") Integer id) {
        CustomResponse<CouponDto> res = new CustomResponse<>();

        Coupon coupon = couponQueryService.selectCoupon(id);
        List<UserInfoDto> users = null;
        if (coupon.getPublicType().equals(CouponPublicType.PRIVATE)) {
            List<Integer> userIds = couponQueryService.selectPublishedCouponUserIds(coupon.getId());
            users =
                    userCommandService.selectUserInfoListWithIds(userIds).stream().map(UserInfo::convert2Dto).toList();
        }
        res.setData(Optional.ofNullable(coupon.convert2Dto(users)));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/can-use")
    public ResponseEntity<CustomResponse<List<Coupon>>> selectCanUseCoupon(@RequestHeader(value = "Authorization") Optional<String> auth) {
        CustomResponse<List<Coupon>> res = new CustomResponse<>();

                TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);

        List<Coupon> coupons = couponQueryService.selectCanUseCoupon(tokenInfo.getId());
        res.setData(Optional.ofNullable(coupons));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/can-download")
    public ResponseEntity<CustomResponse<List<Coupon>>> selectCanDownloadCoupon(@RequestHeader(value = "Authorization") Optional<String> auth) {
        CustomResponse<List<Coupon>> res = new CustomResponse<>();

                TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);

        List<Coupon> coupons = couponQueryService.selectNotDownloadCoupon(tokenInfo.getId());
        res.setData(Optional.ofNullable(coupons));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<CustomResponse<List<Coupon>>> selectUserCoupons(@RequestHeader(value = "Authorization", required = false) Optional<String> auth,
                                                                          @PathVariable(value = "userId") Integer userId) {
        CustomResponse<List<Coupon>> res = new CustomResponse<>();

                jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);

        List<Coupon> coupons = couponQueryService.selectUserCouponList(userId);
        res.setData(Optional.ofNullable(coupons));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/downloaded")
    public ResponseEntity<CustomResponse<List<Coupon>>> selectDownloadedCoupon(@RequestHeader(value = "Authorization") Optional<String> auth) {
        CustomResponse<List<Coupon>> res = new CustomResponse<>();

        TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);

        List<Coupon> coupons = couponQueryService.selectDownloadedCoupon(tokenInfo.getId());
        res.setData(Optional.ofNullable(coupons));
        return ResponseEntity.ok(res);
    }

    @PostMapping("/download/{id}")
    public ResponseEntity<CustomResponse<Boolean>> selectDownloadCoupon(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                        @PathVariable("id") Integer id) {
        CustomResponse<Boolean> res = new CustomResponse<>();

        TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);

        Boolean checkDownloaded = couponQueryService.checkHasCoupon(id, tokenInfo.getId());
        if (checkDownloaded) throw new BusinessException("이미 다운로드 받은 쿠폰입니다.");
        couponCommandService.downloadCoupon(tokenInfo.getId(), id);
        res.setData(Optional.of(true));
        return ResponseEntity.ok(res);
    }


    @PostMapping("/add")
    public ResponseEntity<CustomResponse<Coupon>> addCoupon(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                            @RequestPart(value = "data") CouponAddReq data) {
        CustomResponse<Coupon> res = new CustomResponse<>();

                TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);

            Integer adminId = tokenInfo.getId();
            String title = utils.validateString(data.getTitle(), 100L, "제목");
            if (data.getType() == null) throw new BusinessException("할인 유형을 입력해주세요.");
            if (data.getType().equals(CouponType.RATE)) {
                if (data.getAmount() > 100) throw new BusinessException("할인율을 100%를 넘을 수 없습니다.");
            } else {
                if (data.getAmount() > data.getMinPrice())
                    throw new BusinessException("할인 금액이 주문 최소 금액을 넘을 수 없습니다.");
            }
            if (data.getAmount() < 0) throw new BusinessException("할인율을 확인해주세요.");
            if (data.getMinPrice() == null) data.setMinPrice(0);
            if (data.getStartAt() == null) throw new BusinessException("사용 가능 시작 기간을 입력해주세요.");
            boolean isPublic = data.getUserIds() == null;
            Coupon
                    coupon =
                    Coupon.builder().title(title).type(data.getType()).amount(data.getAmount()).startAt(data.getStartAt()).endAt(
                            data.getEndAt()).minPrice(data.getMinPrice()).state(CouponState.ACTIVE).publicType(isPublic ? CouponPublicType.PUBLIC : CouponPublicType.PRIVATE).build();
            coupon = couponCommandService.addCoupon(coupon);
            if (!isPublic) {
                List<User> users = userCommandService.selectUserListWithIds(data.getUserIds());
                Coupon finalCoupon = coupon;
                List<CouponUserMap>
                        couponUserMaps =
                        users.stream().map(v -> CouponUserMap.builder().couponId(finalCoupon.getId()).userId(v.getId()).isUsed(
                                false).build()).toList();
                couponCommandService.addCouponUserMapList(couponUserMaps);
            }
            couponCommandService.sendCouponCreateNotification(coupon, data.getUserIds());
            AdminLog
                    adminLog =
                    AdminLog.builder().id(adminLogQueryService.getAdminLogId()).adminId(adminId).type(AdminLogType.COUPON).targetId(
                            String.valueOf(coupon.getId())).content("쿠폰을 등록하였습니다.").createdAt(utils.now()).build();
            res.setData(Optional.ofNullable(coupon));
            return ResponseEntity.ok(res);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponse<Boolean>> deleteCoupon(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                @PathVariable("id") Integer id) {
        CustomResponse<Boolean> res = new CustomResponse<>();

                jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);

        Coupon coupon = couponQueryService.selectCoupon(id);
        if (coupon.getPublicType().equals(CouponPublicType.SYSTEM))
            throw new BusinessException("시스템 발행 쿠폰은 삭제 불가능합니다.");
        coupon.setState(CouponState.DELETED);
        couponCommandService.updateCoupon(coupon);
        res.setData(Optional.of(true));
        return ResponseEntity.ok(res);
    }

    @PostMapping("")
    public ResponseEntity<CustomResponse<Boolean>> deleteUserCoupon(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                @RequestBody CouponDeleteRequest request) {
        CustomResponse<Boolean> res = new CustomResponse<>();

        jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);

        couponCommandService.deleteUserCoupon(request);

        res.setData(Optional.of(true));
        return ResponseEntity.ok(res);
    }

    // ------ 시스템 쿠폰 처리
    @GetMapping("/system-coupon")
    public ResponseEntity<CustomResponse<List<Coupon>>> selectSystemCoupon(@RequestHeader(value = "Authorization") Optional<String> auth) {
        CustomResponse<List<Coupon>> res = new CustomResponse<>();

                TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);

        List<Coupon> coupons = couponQueryService.selectCouponWithPublicType(CouponPublicType.SYSTEM);
        res.setData(Optional.ofNullable(coupons));
        return ResponseEntity.ok(res);
    }

    @PostMapping("/system-coupon/update")
    public ResponseEntity<CustomResponse<List<Coupon>>> updateSystemCoupon(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                           @RequestPart(value = "data") UpdateSystemCoupon data) {
        CustomResponse<List<Coupon>> res = new CustomResponse<>();

                jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);

        if (data.getOrder_1stAmount() != null) {
            Coupon coupon = couponQueryService.selectCoupon(1);
            coupon.setAmount(data.getOrder_1stAmount());
            couponCommandService.updateCoupon(coupon);
        }
        if (data.getOrder_3rdAmount() != null) {
            Coupon coupon = couponQueryService.selectCoupon(2);
            coupon.setAmount(data.getOrder_3rdAmount());
            couponCommandService.updateCoupon(coupon);
        }
        if (data.getOrder_5thAmount() != null) {
            Coupon coupon = couponQueryService.selectCoupon(3);
            coupon.setAmount(data.getOrder_5thAmount());
            couponCommandService.updateCoupon(coupon);
        }
        return ResponseEntity.ok(res);
    }
}
