package com.matsinger.barofishserver.domain.tastingNote.basketTastingNote.repository;

import com.matsinger.barofishserver.domain.tastingNote.basketTastingNote.domain.BasketTastingNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BasketTastingNoteRepository extends JpaRepository<BasketTastingNote, Integer> {
    List<BasketTastingNote> findAllByUserId(int userId);

    void deleteByUserIdAndProduct_Id(Integer userId, Integer productId);

    // BasketTastingNote와 User는 ManyToOne 관계이기 때문에 User 엔티티는 속성에 직접 접근 가능
    // 하지만 Product는 OneToOne이기 때문에 productId 속성에 접근 불가능 -> product.id를 사용하도록 해야함
    boolean existsByUserIdAndProduct_Id(int userId, int productId);
}
