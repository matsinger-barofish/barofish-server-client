package com.matsinger.barofishserver.domain.tastingNote.basketTastingNote.api;

import com.matsinger.barofishserver.domain.tastingNote.basketTastingNote.application.BasketTastingNoteCommandService;
import com.matsinger.barofishserver.domain.tastingNote.basketTastingNote.application.BasketTastingNoteQueryService;
import com.matsinger.barofishserver.domain.user.application.UserQueryService;
import com.matsinger.barofishserver.global.error.ErrorCode;
import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.jwt.exception.JwtBusinessException;
import com.matsinger.barofishserver.utils.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PatchMapping("/add")
    public ResponseEntity<CustomResponse<Object>> addTastingNoteToBasket(@RequestHeader(value = "Authorization", required = false) Optional<String> auth,
                                                                         @RequestBody Integer productId) {
        CustomResponse<Object> response = new CustomResponse<>();

        TokenInfo tokenInfo = jwtService.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo.getType() != TokenAuthType.USER && !userQueryService.existsById(tokenInfo.getId())) {
            throw new JwtBusinessException(ErrorCode.NOT_ALLOWED);
        }

        basketTastingNoteCommandService.addTastingNote(tokenInfo.getId(), productId);

        response.setIsSuccess(true);
        return ResponseEntity.ok(response);
    }

    @GetMapping("")
    public ResponseEntity<CustomResponse<Object>> getTastingNoteBasket(@RequestHeader(value = "Authorization", required = false) Optional<String> auth) {

        CustomResponse<Object> response = new CustomResponse<>();

        TokenInfo tokenInfo = jwtService.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo.getType() != TokenAuthType.USER && !userQueryService.existsById(tokenInfo.getId())) {
            throw new JwtBusinessException(ErrorCode.NOT_ALLOWED);
        }

        basketTastingNoteQueryService.getAllBasketTastingNotes(tokenInfo.getId());

        return null;
    }

    @PatchMapping("/delete")
    public ResponseEntity<CustomResponse<Object>> deleteTastingNoteToBasket(@RequestHeader(value = "Authorization", required = false) Optional<String> auth,
                                                                            @RequestBody Integer productId) {
        CustomResponse<Object> response = new CustomResponse<>();

        TokenInfo tokenInfo = jwtService.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo.getType() != TokenAuthType.USER && !userQueryService.existsById(tokenInfo.getId())) {
            throw new JwtBusinessException(ErrorCode.NOT_ALLOWED);
        }

        basketTastingNoteCommandService.deleteTastingNote(tokenInfo.getId(), productId);

        response.setIsSuccess(true);
        return ResponseEntity.ok(response);
    }
}
