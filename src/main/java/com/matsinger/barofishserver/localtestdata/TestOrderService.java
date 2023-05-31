//package com.matsinger.barofishserver.localtestdata;
//
//import com.matsinger.barofishserver.order.service.OrderCommandService;
//import com.matsinger.barofishserver.product.*;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
//@Component
//@RequiredArgsConstructor
//public class TestOrderService {
//
//    private final OrderCommandService orderCommandService;
//    private final ProductRepository productRepository;
//    private final OptionItemRepository optionItemRepository;
//
//    private final List<String> productSuffixes = TestProductService.suffixes;
//    private final List<String> optionSuffixes = TestOptionService.optionSuffixes;
//
//    private static final List<String> suffixes = List.of("A", "B", "C", "D");
//
//    public void createTestOrder() {
//
//        orderCommandService.createOrder(orderRequest);
//    }
//
//    private List<OrderProductInfoDto> createProductInfoDtos(List<Product> products) {
//
//        List<OrderProductInfoDto> dtos = new ArrayList<>();
//        for (int i = 0; i < products.size(); i++) {
//
//            List<OrderProductOptionDto> options = createOptionDtos();
//            Product findProduct = products.get(i);
//
//            OrderProductInfoDto productInfoDto = OrderProductInfoDto.builder()
//                    .productId(findProduct.getId())
//                    .originPrice(5000)
//                    .discountRate(i * 0.1)
//                    .state(OrderState.WAIT_DEPOSIT)
//                    .deliveryFee(1000 * i)
//                    .options(options).build();
//            dtos.add(productInfoDto);
//        }
//        return dtos;
//    }
//
//    private List<OrderProductOptionDto> createOptionDtos(OptionItem optionItem) {
//
//        List<OrderProductOptionDto> options = new ArrayList<>();
//
//        for (int j = 0; j < optionSuffixes.size(); j++) {
//            OptionItem findOptionItem = optionItemRepository
//                    .findByName("optionItem" + optionSuffixes.get(j)).get();
//
//            OrderProductOptionDto optionDto = OrderProductOptionDto.builder()
//                    .optionId(j)
//                    .optionName(optionSuffixes.get(j))
//                    .amount(findOptionItem.getAmount())
//                    .optionPrice(findOptionItem.getPrice()).build();
//            options.add(optionDto);
//        }
//        return options;
//    }
//}
