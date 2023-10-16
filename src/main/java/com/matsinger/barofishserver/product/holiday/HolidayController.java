package com.matsinger.barofishserver.product.holiday;

import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.utils.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/holiday")
public class HolidayController {

    private final JwtService jwt;
    private final HolidayQueryService holidayQueryService;

    @GetMapping("")
    public ResponseEntity<CustomResponse<Object>> getHolidays(@RequestParam(value = "Authrization") Optional<String> auth,
                                                              @RequestParam(value = "take", required = false, defaultValue = "10") Integer take,
                                                              @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                                              @RequestParam(value = "year") String year,
                                                              @RequestParam(value = "month") String month) {
        CustomResponse<Object> res = new CustomResponse<>();

        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) {
            return res.throwError("토큰 정보가 유효하지 않습니다.", "01");
        }

        PageRequest pageRequest = PageRequest.of(page, take);

        try {
            Holidays holidays = holidayQueryService.getOpenDataHolidayInfoResponse(year, month, pageRequest);

            return ResponseEntity.ok(null);
        } catch(Exception e) {
            return res.defaultError(e);
        }
    }
}
