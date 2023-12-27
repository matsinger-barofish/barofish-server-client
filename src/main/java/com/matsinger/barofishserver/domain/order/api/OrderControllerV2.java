package com.matsinger.barofishserver.domain.order.api;

import com.matsinger.barofishserver.domain.order.dto.OrderDto;
import com.matsinger.barofishserver.domain.order.dto.OrderReq;
import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.utils.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/order")
public class OrderControllerV2 {

    private final JwtService jwt;

    @PostMapping("")
    public ResponseEntity<CustomResponse<OrderDto>> orderProduct(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                 @RequestBody OrderReq data) throws Exception {
        CustomResponse<OrderDto> res = new CustomResponse<>();

        TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);

        Integer userId = tokenInfo.getId();

//        orderCommandService.

        return ResponseEntity.ok(res);
    }
}
