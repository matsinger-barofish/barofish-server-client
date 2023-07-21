package com.matsinger.barofishserver.data.tip.application;

import com.matsinger.barofishserver.data.tip.domain.Tip;
import com.matsinger.barofishserver.data.tip.domain.TipType;
import com.matsinger.barofishserver.data.tip.repository.TipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TipQueryService {
    private final TipRepository tipRepository;
    public Page<Tip> selectTip(PageRequest pageRequest, Specification<Tip> spec) {
        return tipRepository.findAll(spec, pageRequest);
    }

    public List<Tip> selectTipList(TipType type) {
        if (type == null) return tipRepository.findAll();
        else return tipRepository.findAllByType(type);
    }

    public Tip selectTip(Integer id) {
        return tipRepository.findById(id).orElseThrow(() -> {
            throw new Error("팁 정보를 찾을 수 없습니다.");
        });
    }
}
