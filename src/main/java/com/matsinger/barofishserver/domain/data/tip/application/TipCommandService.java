package com.matsinger.barofishserver.domain.data.tip.application;

import com.matsinger.barofishserver.domain.data.tip.domain.Tip;
import com.matsinger.barofishserver.domain.data.tip.repository.TipRepository;
import com.matsinger.barofishserver.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TipCommandService {
    private final TipRepository tipRepository;

    public Tip add(Tip data) {
        return tipRepository.save(data);
    }

    public Tip update(Integer id, Tip data) {
        Tip tip = tipRepository.findById(id).orElseThrow(() -> {
            throw new BusinessException("팁 정보를 찾을 수 없습니다.");
        });
        if (data.getTitle() != null) tip.setTitle(data.getTitle());
        if (data.getType() != null) tip.setType(data.getType());
        if (data.getDescription() != null) tip.setDescription(data.getDescription());
        if (data.getImage() != null) tip.setImage(data.getImage());
        if (data.getContent() != null) tip.setContent(data.getContent());
        return tipRepository.save(tip);
    }

    public void delete(Integer id) {

        Tip tip = tipRepository.findById(id).orElseThrow(() -> {
            throw new BusinessException("팁 정보를 찾을 수 없습니다.");
        });
        tipRepository.deleteById(id);
    }

    public void updateTipList(List<Tip> tips) {
        tipRepository.saveAll(tips);
    }
}
