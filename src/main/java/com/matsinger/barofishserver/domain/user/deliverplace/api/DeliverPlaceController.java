package com.matsinger.barofishserver.domain.user.deliverplace.api;

import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.domain.user.deliverplace.application.DeliverPlaceService;
import com.matsinger.barofishserver.domain.user.deliverplace.DeliverPlace;
import com.matsinger.barofishserver.jwt.exception.JwtExceptionMessage;
import com.matsinger.barofishserver.utils.Common;
import com.matsinger.barofishserver.utils.CustomResponse;
import com.matsinger.barofishserver.utils.RegexConstructor;
import io.jsonwebtoken.JwtException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/deliverPlace")
public class DeliverPlaceController {

    private final DeliverPlaceService deliverPlaceService;
    private final JwtService jwt;
    private final Common utils;

    private final RegexConstructor re;

    @GetMapping("/list")
    public ResponseEntity<CustomResponse<List<DeliverPlace>>> selectDeliverPlace(@RequestHeader(value = "Authorization") Optional<String> auth) {
        CustomResponse<List<DeliverPlace>> res = new CustomResponse<>();

        if (auth.isEmpty()) {
            throw new JwtException(JwtExceptionMessage.TOKEN_REQUIRED);
        }
        TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth.get());
        Integer userId = tokenInfo.getId();

        List<DeliverPlace> deliverPlaces = deliverPlaceService.selectDeliverPlaceList(userId);
        res.setData(Optional.ofNullable(deliverPlaces));
        return ResponseEntity.ok(res);
    }

    @Getter
    @NoArgsConstructor
    private static class AddDeliverPlaceReq {
        String name;
        String receiverName;
        String tel;
        String postalCode;
        String address;
        String addressDetail;
        String deliverMessage;
        String bcode;
        Boolean isDefault;
    }

    @PostMapping("/add")
    public ResponseEntity<CustomResponse<DeliverPlace>> addDeliverPlace(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                        @RequestPart(value = "data") AddDeliverPlaceReq data) {
        CustomResponse<DeliverPlace> res = new CustomResponse<>();

        if (auth.isEmpty()) {
            throw new JwtException(JwtExceptionMessage.TOKEN_REQUIRED);
        }
        TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth.get());
        Integer userId = tokenInfo.getId();

        String name = utils.validateString(data.name, 50L, "배송지명");
        String receiverName = utils.validateString(data.receiverName, 20L, "수령인");
        String tel = utils.validateString(data.tel, 11L, "연락처");
        if (data.postalCode == null) return res.throwError("우편번호를 입력해주세요.", " INPUT_CHECK_REQUIRED");
        if (!Pattern.matches(re.phone, tel)) return res.throwError("연락처 번호 형식을 확인해주세요.", "INPUT_CHECK_REQUIRED");
        String address = utils.validateString(data.address, 100L, "주소");
        String addressDetail = utils.validateString(data.addressDetail, 100L, "상세 주소");
        String deliverMessage = utils.validateString(data.deliverMessage, 100L, "배송메시지");
        String bcode = utils.validateString(data.getBcode(), 10L, "법정동 코드");
        DeliverPlace
                deliverPlace =
                DeliverPlace.builder().userId(userId).name(name).receiverName(receiverName).tel(tel).address(address).addressDetail(
                        addressDetail).deliverMessage(deliverMessage).bcode(bcode).isDefault(data.isDefault).postalCode(
                        data.postalCode).build();
        if (data.isDefault) {
            Optional<DeliverPlace> place = deliverPlaceService.selectDefaultDeliverPlace(userId);
            if (place != null && place.isPresent()) {
                place.get().setIsDefault(false);
                deliverPlaceService.updateDeliverPlace(place.get());
            }
        }
        deliverPlaceService.addDeliverPlace(deliverPlace);

        res.setData(Optional.ofNullable(deliverPlace));
        return ResponseEntity.ok(res);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<CustomResponse<DeliverPlace>> updateDeliverPlace(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                           @PathVariable("id") Integer id,
                                                                           @RequestPart(value = "data") AddDeliverPlaceReq data) {
        CustomResponse<DeliverPlace> res = new CustomResponse<>();

        if (auth.isEmpty()) {
            throw new JwtException(JwtExceptionMessage.TOKEN_REQUIRED);
        }
        TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth.get());

        DeliverPlace deliverPlace = deliverPlaceService.selectDeliverPlace(id);
        if (data.name != null) {
            String name = utils.validateString(data.name, 50L, "배송지명");
            deliverPlace.setName(name);
        }
        if (data.receiverName != null) {
            String receiverName = utils.validateString(data.receiverName, 20L, "수령인");
            deliverPlace.setReceiverName(receiverName);
        }
        if (data.postalCode != null) {
            deliverPlace.setPostalCode(data.postalCode);
        }
        if (data.tel != null) {
            String tel = utils.validateString(data.tel, 11L, "연락처");
            if (!Pattern.matches(re.phone, tel))
                return res.throwError("연락처 번호 형식을 확인해주세요.", "INPUT_CHECK_REQUIRED");
            deliverPlace.setTel(tel);
        }
        if (data.address != null) {
            if (data.getBcode() == null)
                return res.throwError("주소 변경 시 법정동 코드도 같이 입력해주세요.", "INPUT_CHECK_REQUIRED");
            String address = utils.validateString(data.address, 100L, "주소");
            deliverPlace.setAddress(address);
            deliverPlace.setBcode(data.getBcode());
        }
        if (data.addressDetail != null) {
            String addressDetail = utils.validateString(data.addressDetail, 100L, "상세 주소");
            deliverPlace.setAddressDetail(addressDetail);
        }
        if (data.deliverMessage != null) {
            String deliverMessage = utils.validateString(data.deliverMessage, 100L, "배송메시지");
            deliverPlace.setDeliverMessage(deliverMessage);
        }
        if (data.isDefault != null) {
            if (data.isDefault) {
                Optional<DeliverPlace>
                        place =
                        deliverPlaceService.selectDefaultDeliverPlace(deliverPlace.getUserId());
                if (place != null && place.isPresent()) {
                    place.get().setIsDefault(false);
                    deliverPlaceService.updateDeliverPlace(place.get());
                }
                deliverPlace.setIsDefault(true);
            } else {
                deliverPlace.setIsDefault(false);
            }
        }
        deliverPlaceService.updateDeliverPlace(deliverPlace);
        res.setData(Optional.ofNullable(deliverPlace));
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponse<Boolean>> deleteDeliverPlace(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                      @PathVariable("id") Integer id) {
        CustomResponse<Boolean> res = new CustomResponse<>();

        if (auth.isEmpty()) {
            throw new JwtException(JwtExceptionMessage.TOKEN_REQUIRED);
        }
        TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth.get());
        Integer userId = tokenInfo.getId();

        DeliverPlace deliverPlace = deliverPlaceService.selectDeliverPlace(id);
        deliverPlaceService.deleteDeliverPlace(id);
        return ResponseEntity.ok(res);
    }
}
