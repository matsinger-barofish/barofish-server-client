package com.matsinger.barofishserver.localtestdata;

import com.matsinger.barofishserver.order.OrderState;
import com.matsinger.barofishserver.order.dto.OrderProductInfoDto;
import com.matsinger.barofishserver.order.dto.OrderProductOptionDto;
import com.matsinger.barofishserver.order.dto.request.OrderRequestDto;
import com.matsinger.barofishserver.order.service.OrderCommandService;
import com.matsinger.barofishserver.product.Product;
import com.matsinger.barofishserver.product.ProductRepository;
import com.matsinger.barofishserver.userauth.UserAuth;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TestOrderService {

    private final OrderCommandService orderCommandService;
    private final TestUserService testUserService;
    private final ProductRepository productRepository;

    public void createTestOrders() {

        List<UserAuth> userAuths = testUserService.createUser();

        // 각 유저에 대해서 주문을 2개씩 생성. ex) userA -> orderA, orderB
        int seq = 0;
        String suffix;
        for (int i=0; i<userAuths.size(); i++) {

            for (int j=0; j<2; j++) {
                seq++;
                if (seq % 2 == 0) {
                    suffix = "B";
                } else {
                    suffix = "A";
                }

                List<OrderProductInfoDto> products = createProductInfoDtos();

                OrderRequestDto orderRequest = OrderRequestDto.builder()
                        .loginId(userAuths.get(i).getLoginId())
                        .totalPrice(100000)
                        .name("order" + suffix)
                        .products(products).build();

                orderCommandService.createOrderSheet(orderRequest);
            }
        }
    }

    private List<OrderProductInfoDto> createProductInfoDtos() {
        List<OrderProductInfoDto> products = new ArrayList<>();

        for (int i = 1; i < 3; i++) {
            List<OrderProductOptionDto> options = new ArrayList<>();

            createOptionDtos(options);

            Product findProduct = productRepository.findByTitle("testProduct" + i).get();

            OrderProductInfoDto productInfoDto = OrderProductInfoDto.builder()
                    .productId(findProduct.getId())
                    .originPrice(5000 * i)
                    .discountRate(i * 0.1)
                    .amount(10 * i)
                    .state(OrderState.WAIT_DEPOSIT)
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
                    .amount(1)
                    .optionPrice(1000 * j).build();
            options.add(optionDto);
        }
    }
}
