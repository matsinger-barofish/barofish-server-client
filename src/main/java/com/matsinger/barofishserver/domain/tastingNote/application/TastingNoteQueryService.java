package com.matsinger.barofishserver.domain.tastingNote.application;

import com.matsinger.barofishserver.domain.product.application.ProductQueryService;
import com.matsinger.barofishserver.domain.product.domain.Product;
import com.matsinger.barofishserver.domain.product.domain.ProductState;
import com.matsinger.barofishserver.domain.product.repository.ProductRepository;
import com.matsinger.barofishserver.domain.tastingNote.domain.TastingNote;
import com.matsinger.barofishserver.domain.tastingNote.dto.ProductTastingNoteResponse;
import com.matsinger.barofishserver.domain.tastingNote.repository.TastingNoteQueryRepository;
import com.matsinger.barofishserver.domain.tastingNote.repository.TastingNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TastingNoteQueryService {

    private final TastingNoteRepository tastingNoteRepository;
    private final TastingNoteQueryRepository tastingNoteQueryRepository;
    private final ProductQueryService productQueryService;
    private final ProductRepository productRepository;

    public TastingNote findByOrderProductInfoId(Integer orderProductInfoId) {
        return tastingNoteRepository.findByOrderProductInfoId(orderProductInfoId)
                .orElseThrow(() -> new IllegalArgumentException("테이스팅 노트 정보를 찾을 수 없습니다."));
    }

    public boolean existsByOrderProductInfoId(Integer orderProductInfoId) {
        return tastingNoteRepository.existsByOrderProductInfoId(orderProductInfoId);
    }

    public ProductTastingNoteResponse getTastingNote(Integer productId) {
        Product findedProduct = productQueryService.findById(productId);
        if (findedProduct.getState() != ProductState.ACTIVE) {
            throw new IllegalArgumentException("현재 판매하지 않는 상품입니다.");
        }

        ProductTastingNoteResponse tastingNotesResponse = tastingNoteQueryRepository.getTastingNotesScore(findedProduct.getId());
        tastingNotesResponse.roundScoresToSecondDecimalPlace();

        tastingNotesResponse.setTastingNoteAdditionalInfo(findedProduct);

        return tastingNotesResponse;
    }

    public List<ProductTastingNoteResponse> compareTastingNotes(List<Integer> productIds) {
        List<ProductTastingNoteResponse> tastingNotes = new ArrayList<>();
        for (Integer productId : productIds) {
            tastingNotes.add(getTastingNote(productId));
        }
        return tastingNotes;
    }
}
