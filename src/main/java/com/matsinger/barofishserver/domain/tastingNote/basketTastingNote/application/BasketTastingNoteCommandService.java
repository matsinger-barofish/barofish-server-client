package com.matsinger.barofishserver.domain.tastingNote.basketTastingNote.application;

import com.matsinger.barofishserver.domain.product.application.ProductQueryService;
import com.matsinger.barofishserver.domain.product.domain.Product;
import com.matsinger.barofishserver.domain.tastingNote.basketTastingNote.domain.BasketTastingNote;
import com.matsinger.barofishserver.domain.tastingNote.basketTastingNote.repository.BasketTastingNoteRepository;
import com.matsinger.barofishserver.domain.user.application.UserQueryService;
import com.matsinger.barofishserver.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasketTastingNoteCommandService {

    private final ProductQueryService productQueryService;
    private final BasketTastingNoteRepository basketTastingNoteRepository;
    private final UserQueryService userQueryService;

    @Transactional
    public void addTastingNote(Integer userId, Integer productId) {
        Product findedProduct = productQueryService.findById(productId);
        User findedUser = userQueryService.findById(userId);

        boolean isProductExists = basketTastingNoteRepository.existsByUserIdAndProductId(userId, productId);
        if (isProductExists) {
            throw new IllegalArgumentException("저장함에 같은 상품이 있습니다.");
        }

        basketTastingNoteRepository.save(
                BasketTastingNote.builder()
                        .product(findedProduct)
                        .user(findedUser)
                        .build()
        );
    }

    @Transactional
    public void deleteTastingNote(Integer userId, Integer productId) {

        boolean isProductExists = basketTastingNoteRepository.existsByUserIdAndProductId(userId, productId);
        if (!isProductExists) {
            throw new IllegalArgumentException("상품이 존재하지 않습니다.");
        }

        try {
            basketTastingNoteRepository.deleteByUserIdAndProductId(userId, productId);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("상품 정보를 찾을 수 없습니다.");
        }
    }
}
