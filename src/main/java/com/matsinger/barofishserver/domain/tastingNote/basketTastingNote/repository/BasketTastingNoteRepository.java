package com.matsinger.barofishserver.domain.tastingNote.basketTastingNote.repository;

import com.matsinger.barofishserver.domain.tastingNote.basketTastingNote.domain.BasketTastingNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BasketTastingNoteRepository extends JpaRepository<BasketTastingNote, Integer> {
}
