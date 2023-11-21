package com.matsinger.barofishserver.domain.tastingNote.basketTastingNote.application;

import com.matsinger.barofishserver.domain.tastingNote.basketTastingNote.dto.TastingNoteCompareBasketProductDto;
import com.matsinger.barofishserver.domain.tastingNote.basketTastingNote.repository.BasketTastingNoteQueryRepository;
import com.matsinger.barofishserver.domain.tastingNote.basketTastingNote.repository.BasketTastingNoteRepository;
import com.matsinger.barofishserver.domain.tastingNote.repository.TastingNoteRepository;
import com.matsinger.barofishserver.domain.user.application.UserQueryService;
import com.matsinger.barofishserver.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasketTastingNoteQueryService {

    private final BasketTastingNoteRepository basketTastingNoteRepository;
    private final UserQueryService userQueryService;
    private final BasketTastingNoteQueryRepository basketTastingNoteQueryRepository;
    private final TastingNoteRepository tastingNoteRepository;

    public List<TastingNoteCompareBasketProductDto> getAllBasketTastingNotes(Integer userId) {
        User findedUser = userQueryService.findById(userId);

        List<TastingNoteCompareBasketProductDto> basketProductDtos = basketTastingNoteQueryRepository.findBasketProductsByUserId(findedUser.getId());
        for (TastingNoteCompareBasketProductDto basketProductDto : basketProductDtos) {
            String firstImage = getFirstElementFromArrayFormatString(basketProductDto.getImage());
            basketProductDto.setImage(firstImage);

            basketProductDto.isTastingNoteExists(tastingNoteRepository.existsByProductId(basketProductDto.getProductId()));
        }

        return basketProductDtos;
    }

    private String getFirstElementFromArrayFormatString(String arrayFormatString) {
        StringBuilder resultBuilder = new StringBuilder(arrayFormatString.length());
        for (char c : arrayFormatString.toCharArray()) {
            if (c != '[' && c != ']') {
                resultBuilder.append(c);
            }
        }
        String bracketRemovedString = resultBuilder.toString();
        return bracketRemovedString.split(", ")[0];
    }

    public boolean isSaved(int userId, int productId) {
        User findedUser = userQueryService.findById(userId);
        return basketTastingNoteRepository.existsByUserIdAndProductId(findedUser.getId(), productId);
    }
}
