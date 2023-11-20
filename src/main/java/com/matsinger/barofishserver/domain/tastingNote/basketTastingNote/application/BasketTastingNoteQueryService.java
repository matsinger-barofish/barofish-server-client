package com.matsinger.barofishserver.domain.tastingNote.basketTastingNote.application;

import com.matsinger.barofishserver.domain.tastingNote.basketTastingNote.domain.BasketTastingNote;
import com.matsinger.barofishserver.domain.tastingNote.basketTastingNote.repository.BasketTastingNoteRepository;
import com.matsinger.barofishserver.domain.user.application.UserQueryService;
import com.matsinger.barofishserver.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasketTastingNoteQueryService {

    private final BasketTastingNoteRepository basketTastingNoteRepository;
    private final UserQueryService userQueryService;

    public List<BasketTastingNote> getAllBasketTastingNotes(Integer userId) {
        User findedUser = userQueryService.findById(userId);
        List<BasketTastingNote> findedTastingNotes = basketTastingNoteRepository.findAllByUserId(findedUser.getId());

        return findedTastingNotes;
    }
}
