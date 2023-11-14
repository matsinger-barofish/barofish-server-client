package com.matsinger.barofishserver.domain.tastingNote.application;

import com.matsinger.barofishserver.domain.tastingNote.domain.TastingNote;
import com.matsinger.barofishserver.domain.tastingNote.repository.TastingNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TastingNoteQueryService {

    private final TastingNoteRepository tastingNoteRepository;

    public TastingNote findByOrderProductInfoId(Integer orderProductInfoId) {
        return tastingNoteRepository.findByOrderProductInfoId(orderProductInfoId)
                .orElseThrow(() -> new IllegalArgumentException("테이스팅 노트 정보를 찾을 수 없습니다."));
    }

    public boolean existsByOrderProductInfoId(Integer orderProductInfoId) {
        return tastingNoteRepository.existsByOrderProductInfoId(orderProductInfoId);
    }
}
