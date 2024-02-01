package com.matsinger.barofishserver.domain.tastingNote.repository;

import com.matsinger.barofishserver.domain.tastingNote.domain.TastingNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TastingNoteRepository extends JpaRepository<TastingNote, Integer> {

    Optional<TastingNote> findByOrderProductInfoId(Integer orderProductInfoId);

    Boolean existsByOrderProductInfoId(Integer orderProductInfoId);

    boolean existsByProductId(Integer productId);
}
