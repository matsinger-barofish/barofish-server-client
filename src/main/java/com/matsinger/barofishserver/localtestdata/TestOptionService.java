package com.matsinger.barofishserver.localtestdata;

import com.matsinger.barofishserver.product.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Component
@RequiredArgsConstructor
public class TestOptionService {

    private final OptionRepository optionRepository;
    private final OptionItemRepository optionItemRepository;

    public static final List<String> optionSuffixes = List.of("A", "B", "C", "D");
    public static final List<String> optionDescriptions = List.of("재고가 충분한 케이스", "재고가 부족한 케이스");
    public static final List<Integer> optionAmounts = List.of(10000, 10);

    public OptionItem createOptions(Product product, int price, int amount, String suffix) {

        // 시나리오에 따라 Option, OptionItem 생성 후
        Option createdOption = Option.builder()
                .isNeeded((byte) 1)
                .description("option" + suffix).build();

        OptionItem createdOptionItem = OptionItem.builder()
                .name("optionItem" + suffix)
                .price(price)
                .amount(amount).build();

        // 연관관계 설정
        createdOptionItem.setOption(createdOption);
        createdOption.setProduct(product);

        // Product는 영속된 객체이며
        // Product - Options - OptionItems 으로 영속성 전파 (cascade)
        // Product는 저장하지 않아도 더티체킹?에 의해 자동 수정되는지 실험

        optionItemRepository.save(createdOptionItem);
        optionRepository.save(createdOption);

        return createdOptionItem;
    }
}
