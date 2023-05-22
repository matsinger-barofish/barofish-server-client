package com.matsinger.barofishserver.order.controller;

import com.matsinger.barofishserver.order.dto.request.OrderRequestDto;
import com.matsinger.barofishserver.order.dto.response.OrderResponseDto;
import com.matsinger.barofishserver.order.exception.OrderBusinessException;
import com.matsinger.barofishserver.order.service.OrderCommandService;
import com.matsinger.barofishserver.order.service.OrderQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RequiredArgsConstructor
@RestController
public class OrderController {
    private final OrderCommandService orderCommandService;
    private final OrderQueryService orderQueryService;

    // 주문하기
    @PostMapping("/v1/order")
    public ResponseEntity<Object> order(@RequestBody OrderRequestDto request) {
        try {
            HashMap<String, String> response = new HashMap<>();
            String orderId = orderCommandService.createOrderSheet(request);
            response.put("orderId", orderId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            throw new OrderBusinessException(e.getMessage(), e);
        }
    }

    // 주문내역 단건조회
    @GetMapping("/v1/order/{id}")
    public ResponseEntity<Object> getOrder(@PathVariable("id") String orderId) {
        OrderResponseDto response = orderQueryService.getOrder(orderId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    // 주문내역 전체조회

    // 주문취소

    // 주문내역 삭제
    // 1. 사용자가 현재 주문서를 이탈하면 주문서 삭제
    // 2. 매일 결제 테이블이 매핑되지 않은 주문서 삭제하는 스케쥴러 구현
}
