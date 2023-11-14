package com.matsinger.barofishserver.domain.tastingNote.basketTastingNote.api;

import com.matsinger.barofishserver.domain.compare.dto.CompareMain;
import com.matsinger.barofishserver.utils.CustomResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/basket-tasting-note")
public class BasketTastingNoteController {

    @PutMapping("/add")
    public ResponseEntity<CustomResponse<Object>> addTastingNoteToBasket(@RequestHeader(value = "Authorization", required = false) Optional<String> auth,
                                                                         @RequestBody List<Integer> productIds) {

        return null;
    }

    @GetMapping("")
    public ResponseEntity<CustomResponse<Object>> getTastingNoteBasket(@RequestHeader(value = "Authorization", required = false) Optional<String> auth) {

        return null;
    }
}
