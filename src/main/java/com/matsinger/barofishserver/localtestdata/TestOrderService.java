package com.matsinger.barofishserver.localtestdata;

import com.matsinger.barofishserver.order.dto.OrderProductInfoDto;
import com.matsinger.barofishserver.order.dto.OrderProductOptionDto;
import com.matsinger.barofishserver.order.dto.request.OrderRequestDto;
import com.matsinger.barofishserver.order.service.OrderCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TestOrderService {

    private final OrderCommandService orderCommandService;
    private final TestUserService testUserService;

    public void createTestOrders() {

        List<OrderProductInfoDto> products = createProductInfoDtos();

        OrderRequestDto orderRequest = OrderRequestDto.builder()
                .loginId("test1")
                .totalPrice(100000)
                .products(products).build();

        orderCommandService.createOrderSheet(orderRequest);
    }

    private List<OrderProductInfoDto> createProductInfoDtos() {
        List<OrderProductInfoDto> products = new ArrayList<>();

        for (int i = 1; i < 3; i++) {
            List<OrderProductOptionDto> options = new ArrayList<>();

            createOptionDtos(options);

            OrderProductInfoDto productInfoDto = OrderProductInfoDto.builder()
                    .productId(i)
                    .originPrice(5000 * i)
                    .discountRate(i * 0.1)
                    .amount(10 * i)
                    .deliveryFee(1000 * i)
                    .options(options).build();
            products.add(productInfoDto);
        }
        return products;
    }

    private void createOptionDtos(List<OrderProductOptionDto> options) {
        for (int j = 1; j < 3; j++) {
            OrderProductOptionDto optionDto = OrderProductOptionDto.builder()
                    .optionId(j)
                    .optionName("testOption" + j)
                    .optionPrice(1000 * j).build();
            options.add(optionDto);
        }
    }
}
