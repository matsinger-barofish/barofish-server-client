package com.matsinger.barofishserver.order;

import com.matsinger.barofishserver.order.dto.request.OrderRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class OrderController {

    private final OrderQueryService orderQueryService;
    private final OrderCommandService orderCommandService;

    // 주문하기
    @PostMapping("/v1/payment")
    public String order(@RequestBody OrderRequestDto request) {
        try {

        }
    }

    // 주문취소

    // 주문취소 거절

    // 주문내역 삭제
    // 1. 사용자가 현재 주문서를 이탈하면 주문서 삭제
    // 2. 매일 결제 테이블이 매핑되지 않은 주문서 삭제하는 스케쥴러 구현
}
