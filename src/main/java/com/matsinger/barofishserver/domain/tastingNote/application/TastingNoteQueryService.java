package com.matsinger.barofishserver.domain.tastingNote.application;

import com.matsinger.barofishserver.domain.product.application.ProductQueryService;
import com.matsinger.barofishserver.domain.product.domain.Product;
import com.matsinger.barofishserver.domain.product.domain.ProductState;
import com.matsinger.barofishserver.domain.tastingNote.domain.TastingNote;
import com.matsinger.barofishserver.domain.tastingNote.domain.TastingNoteTastes;
import com.matsinger.barofishserver.domain.tastingNote.domain.TastingNoteTextures;
import com.matsinger.barofishserver.domain.tastingNote.dto.ProductTastingNoteInquiryDto;
import com.matsinger.barofishserver.domain.tastingNote.dto.ProductTastingNoteResponse;
import com.matsinger.barofishserver.domain.tastingNote.repository.TastingNoteQueryRepository;
import com.matsinger.barofishserver.domain.tastingNote.repository.TastingNoteRepository;
import com.matsinger.barofishserver.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TastingNoteQueryService {

    private final TastingNoteRepository tastingNoteRepository;
    private final TastingNoteQueryRepository tastingNoteQueryRepository;
    private final ProductQueryService productQueryService;

    public TastingNote findByOrderProductInfoId(Integer orderProductInfoId) {
        return tastingNoteRepository.findByOrderProductInfoId(orderProductInfoId)
                .orElseThrow(() -> new BusinessException("테이스팅 노트 정보를 찾을 수 없습니다."));
    }

    public boolean existsByOrderProductInfoId(Integer orderProductInfoId) {
        return tastingNoteRepository.existsByOrderProductInfoId(orderProductInfoId);
    }

    public ProductTastingNoteResponse getTastingNoteInfo(Integer productId) {
        Product findedProduct = productQueryService.findById(productId);
        if (findedProduct.getState() != ProductState.ACTIVE) {
            throw new BusinessException("현재 판매하지 않는 상품입니다.");
        }

        ProductTastingNoteInquiryDto productTastingNoteInquiryDto;
        try {
            productTastingNoteInquiryDto = tastingNoteQueryRepository.getTastingNotesScore(findedProduct.getId());
            productTastingNoteInquiryDto.roundScoresToSecondDecimalPlace();
        } catch (RuntimeException e) {
            return null;
        }

        ProductTastingNoteResponse tastingNoteResponse = convertToResponse(productTastingNoteInquiryDto);

        return tastingNoteResponse;
    }

    @NotNull
    private ProductTastingNoteResponse convertToResponse(ProductTastingNoteInquiryDto productTastingNoteInquiryDto) {
        TastingNoteTastes tastes = productTastingNoteInquiryDto.getTastes();
        tastes.sortByScore();
        TastingNoteTextures textures = productTastingNoteInquiryDto.getTextures();
        textures.sortByScore();

        ProductTastingNoteResponse tastingNoteResponse = new ProductTastingNoteResponse(tastes, textures);
        tastingNoteResponse.setDifficultyLevelOfTrimming(productTastingNoteInquiryDto.getDifficultyLevelOfTrimming());
        tastingNoteResponse.setTheScentOfTheSea(productTastingNoteInquiryDto.getTheScentOfTheSea());

        List<String> recommendedCookingWays = convertToArray(
                productTastingNoteInquiryDto.getRecommendedCookingWay()
        );
        tastingNoteResponse.setRecommendedCookingWay(recommendedCookingWays);
        return tastingNoteResponse;
    }

    private List<String> convertToArray(String arrayFormatString) {
        StringBuilder resultBuilder = new StringBuilder(arrayFormatString.length());
        for (char c : arrayFormatString.toCharArray()) {
            if (c != '[' && c != ']') {
                resultBuilder.append(c);
            }
        }
        String bracketRemovedString = resultBuilder.toString();
        return Arrays.stream(bracketRemovedString.split(", ")).toList();
    }

    public List<ProductTastingNoteResponse> compareTastingNotes(List<Integer> productIds) {
        List<ProductTastingNoteResponse> tastingNotes = new ArrayList<>();
        for (Integer productId : productIds) {
            tastingNotes.add(getTastingNoteInfo(productId));
        }
        return tastingNotes;
    }
}
