package com.matsinger.barofishserver.domain.tastingNote.api;

import com.matsinger.barofishserver.domain.compare.dto.CompareMain;
import com.matsinger.barofishserver.domain.tastingNote.application.TastingNoteCommandService;
import com.matsinger.barofishserver.domain.tastingNote.application.TastingNoteQueryService;
import com.matsinger.barofishserver.domain.tastingNote.dto.ProductTastingNoteResponse;
import com.matsinger.barofishserver.domain.tastingNote.dto.TastingNoteCreateRequest;
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

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tasting-note")
public class TastingNoteController {

    private final JwtService jwtService;
    private final TastingNoteCommandService tastingNoteCommandService;
    private final TastingNoteQueryService tastingNoteQueryService;
    private final UserQueryService userQueryService;

    @GetMapping("/main")
    public ResponseEntity<CustomResponse<CompareMain>> selectMain(
            @RequestHeader(value = "Authorization") Optional<String> auth,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "take", required = false, defaultValue = "10") Integer take) {

        return null;
    }

    @PostMapping("/")
    public ResponseEntity<CustomResponse<Object>> createTastingNote(@RequestHeader(value = "Authorization", required = false) Optional<String> auth,
                                                                    @RequestBody TastingNoteCreateRequest request) {
        CustomResponse<Object> response = new CustomResponse<>();

        TokenInfo tokenInfo = jwtService.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo.getType() != TokenAuthType.USER && !userQueryService.existsById(tokenInfo.getId())) {
            throw new JwtBusinessException(ErrorCode.NOT_ALLOWED);
        }

        boolean isSuccess = tastingNoteCommandService.createTastingNote(tokenInfo.getId(), request);
        response.setIsSuccess(isSuccess);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<CustomResponse<Object>> getProductTastingNote(@RequestHeader(value = "Authorization", required = false) Optional<String> auth,
                                                                        @PathVariable(value = "productId") Integer productId) {
        CustomResponse<Object> response = new CustomResponse<>();

        jwtService.validateAndGetTokenInfo(Set.of(TokenAuthType.USER, TokenAuthType.ALLOW), auth);

        ProductTastingNoteResponse productTastingNoteResponse = tastingNoteQueryService.getTastingNote(productId);

        response.setIsSuccess(true);
        response.setData(Optional.of(productTastingNoteResponse));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    public ResponseEntity<CustomResponse<Object>> getMyProductTastingNotes(@RequestHeader(value = "Authorization", required = false) Optional<String> auth) {

        return null;
    }

    @GetMapping("/compare")
    public ResponseEntity<CustomResponse<Object>> getMyProductTastingNotes(@RequestHeader(value = "Authorization", required = false) Optional<String> auth,
                                                                           @RequestParam(value = "productIds") List<Integer> productIds) {
        CustomResponse<Object> response = new CustomResponse<>();

        jwtService.validateAndGetTokenInfo(Set.of(TokenAuthType.USER, TokenAuthType.ALLOW), auth);

        if (productIds.size() > 3) {
            throw new IllegalArgumentException("최대 3개까지 비교할 수 있습니다.");
        }

        List<ProductTastingNoteResponse> tastingNotes = tastingNoteQueryService.compareTastingNotes(productIds);

        response.setIsSuccess(true);
        response.setData(Optional.of(tastingNotes));
        return ResponseEntity.ok(response);
    }
}
