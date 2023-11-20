package com.matsinger.barofishserver.domain.tastingNote.basketTastingNote.repository;

import com.matsinger.barofishserver.domain.tastingNote.basketTastingNote.domain.BasketTastingNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BasketTastingNoteRepository extends JpaRepository<BasketTastingNote, Integer> {
    List<BasketTastingNote> findAllByUserId(int userId);

    void deleteByUserIdAndProductId(Integer userId, Integer productId);
}
