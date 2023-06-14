package com.matsinger.barofishserver.data.tip;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TipService {
    @Autowired
    private final TipRepository tipRepository;

    public List<Tip> selectTipList(TipType type) {
        if (type == null) return tipRepository.findAll();
        else return tipRepository.findAllByType(type);
    }

    Tip selectTip(Integer id) {
        return tipRepository.findById(id).orElseThrow(() -> {
            throw new Error("팁 정보를 찾을 수 없습니다.");
        });
    }

    Tip add(Tip data) {
        return tipRepository.save(data);
    }

    Tip update(Integer id, Tip data) {
        Tip tip = tipRepository.findById(id).orElseThrow(() -> {
            throw new Error("팁 정보를 찾을 수 없습니다.");
        });
        if (data.getTitle() != null) tip.setTitle(data.getTitle());
        if(data.getType() != null )tip.setType(data.getType());
        if (data.getDescription() != null) tip.setDescription(data.getDescription());
        if (data.getImage() != null) tip.setImage(data.getImage());
        return tipRepository.save(tip);
    }

    Boolean delete(Integer id) {
        try {
            Tip tip = tipRepository.findById(id).orElseThrow(() -> {
                throw new Error("팁 정보를 찾을 수 없습니다.");
            });
            tipRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
