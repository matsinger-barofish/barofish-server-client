package com.matsinger.barofishserver.domain.tastingNote.basketTastingNote.api;

import com.matsinger.barofishserver.domain.tastingNote.basketTastingNote.application.BasketTastingNoteCommandService;
import com.matsinger.barofishserver.domain.tastingNote.basketTastingNote.application.BasketTastingNoteQueryService;
import com.matsinger.barofishserver.domain.tastingNote.basketTastingNote.dto.BasketTastingNoteAddReq;
import com.matsinger.barofishserver.domain.tastingNote.basketTastingNote.dto.BasketTastingNoteDeleteReq;
import com.matsinger.barofishserver.domain.tastingNote.basketTastingNote.dto.TastingNoteCompareBasketProductDto;
import com.matsinger.barofishserver.domain.user.application.UserQueryService;
import com.matsinger.barofishserver.global.exception.ErrorCode;
import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.jwt.exception.JwtBusinessException;
import com.matsinger.barofishserver.utils.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/basket-tasting-note")
public class BasketTastingNoteController {

    private final JwtService jwtService;
    private final UserQueryService userQueryService;
    private final BasketTastingNoteCommandService basketTastingNoteCommandService;
    private final BasketTastingNoteQueryService basketTastingNoteQueryService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @PostMapping("/add")
    public ResponseEntity<CustomResponse<Boolean>> addTastingNoteToBasket(@RequestHeader(value = "Authorization", required = false) Optional<String> auth,
                                                                          @RequestPart(value = "data") BasketTastingNoteAddReq data) {
        CustomResponse<Boolean> response = new CustomResponse<>();

        TokenInfo tokenInfo = jwtService.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo.getType() != TokenAuthType.USER && !userQueryService.existsById(tokenInfo.getId())) {
            throw new JwtBusinessException(ErrorCode.NOT_ALLOWED);
        }

        basketTastingNoteCommandService.addTastingNote(tokenInfo.getId(), data.getProductId());

        response.setIsSuccess(true);
        return ResponseEntity.ok(response);
    }

    @GetMapping("")
    public ResponseEntity<CustomResponse<List<TastingNoteCompareBasketProductDto>>> getTastingNoteBasket(
            @RequestHeader(value = "Authorization", required = false) Optional<String> auth) {
        CustomResponse<List<TastingNoteCompareBasketProductDto>> response = new CustomResponse<>();

        TokenInfo tokenInfo = jwtService.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo.getType() != TokenAuthType.USER && !userQueryService.existsById(tokenInfo.getId())) {
            throw new JwtBusinessException(ErrorCode.NOT_ALLOWED);
        }

        List<TastingNoteCompareBasketProductDto> basketProductDtos = basketTastingNoteQueryService.getAllBasketTastingNotes(tokenInfo.getId());
        response.setIsSuccess(true);
        response.setData(Optional.ofNullable(basketProductDtos));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/delete")
    public ResponseEntity<CustomResponse<Boolean>> deleteTastingNoteToBasket(@RequestHeader(value = "Authorization", required = false) Optional<String> auth,
                                                                             @RequestPart(value = "data") BasketTastingNoteDeleteReq request) {
        CustomResponse<Boolean> response = new CustomResponse<>();

        TokenInfo tokenInfo = jwtService.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo.getType() != TokenAuthType.USER && !userQueryService.existsById(tokenInfo.getId())) {
            throw new JwtBusinessException(ErrorCode.NOT_ALLOWED);
        }

        basketTastingNoteCommandService.deleteTastingNote(tokenInfo.getId(), request.getProductId());

        response.setIsSuccess(true);
        return ResponseEntity.ok(response);
    }
}
