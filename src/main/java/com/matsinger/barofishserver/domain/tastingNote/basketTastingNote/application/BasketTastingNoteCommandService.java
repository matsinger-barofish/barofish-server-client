package com.matsinger.barofishserver.domain.tastingNote.basketTastingNote.application;

import com.matsinger.barofishserver.domain.product.application.ProductQueryService;
import com.matsinger.barofishserver.domain.product.domain.Product;
import com.matsinger.barofishserver.domain.tastingNote.basketTastingNote.domain.BasketTastingNote;
import com.matsinger.barofishserver.domain.tastingNote.basketTastingNote.repository.BasketTastingNoteQueryRepository;
import com.matsinger.barofishserver.domain.tastingNote.basketTastingNote.repository.BasketTastingNoteRepository;
import com.matsinger.barofishserver.domain.user.application.UserQueryService;
import com.matsinger.barofishserver.domain.user.domain.User;
import com.matsinger.barofishserver.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BasketTastingNoteCommandService {

    private final ProductQueryService productQueryService;
    private final BasketTastingNoteRepository basketTastingNoteRepository;
    private final UserQueryService userQueryService;
    private final BasketTastingNoteQueryRepository basketTastingNoteQueryRepository;

    @Transactional
    public void addTastingNote(Integer userId, Integer productId) {
        Product findedProduct = productQueryService.findById(productId);
        User findedUser = userQueryService.findById(userId);

        boolean isProductExists = basketTastingNoteRepository.existsByUserIdAndProductId(userId, productId);
        if (isProductExists) {
            throw new BusinessException("저장함에 같은 상품이 있습니다.");
        }

        basketTastingNoteRepository.save(
                BasketTastingNote.builder()
                        .product(findedProduct)
                        .user(findedUser)
                        .build()
        );
    }

    @Transactional
    public void deleteTastingNote(Integer userId, List<Integer> productIds) {

//        boolean isProductExists = basketTastingNoteRepository.existsByUserIdAndProductId(userId, productId);
//        if (!isProductExists) {
//            throw new BusinessException("상품이 존재하지 않습니다.");
//        }

        try {
            basketTastingNoteQueryRepository.deleteAllByUserIdAndProductId(userId, productIds);
        } catch (RuntimeException e) {
            throw new BusinessException("상품 정보를 찾을 수 없습니다.");
        }
    }
}
