package com.matsinger.barofishserver.domain.tastingNote.application;

import com.matsinger.barofishserver.domain.order.application.OrderQueryService;
import com.matsinger.barofishserver.domain.order.domain.Orders;
import com.matsinger.barofishserver.domain.order.orderprductinfo.application.OrderProductInfoQueryService;
import com.matsinger.barofishserver.domain.order.orderprductinfo.domain.OrderProductInfo;
import com.matsinger.barofishserver.domain.order.orderprductinfo.domain.OrderProductState;
import com.matsinger.barofishserver.domain.tastingNote.domain.TastingNote;
import com.matsinger.barofishserver.domain.tastingNote.dto.TastingNoteCreateRequest;
import com.matsinger.barofishserver.domain.tastingNote.repository.TastingNoteRepository;
import com.matsinger.barofishserver.domain.user.application.UserQueryService;
import com.matsinger.barofishserver.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TastingNoteCommandService {

    private final TastingNoteRepository tastingNoteRepository;
    private final OrderProductInfoQueryService orderProductInfoQueryService;
    private final UserQueryService userQueryService;

    @Transactional
    public boolean createTastingNote(int userId, TastingNoteCreateRequest request) {

        User findedUser = userQueryService.findById(userId);
        OrderProductInfo findedOrderProductInfo = orderProductInfoQueryService.findById(request.getOrderProductInfoId());
        Orders findedOrder = findedOrderProductInfo.getOrder();

        if (findedUser.getId() != findedOrder.getUserId()) {
            throw new IllegalArgumentException("주문인이 일치하지 않습니다.");
        }

        if (findedOrderProductInfo.getState() != OrderProductState.FINAL_CONFIRM) {
            throw new IllegalArgumentException("구매 확정 후 테이스팅 노트 작성이 가능합니다.");
        }

        boolean isTastingNoteExists = tastingNoteRepository.existsByOrderProductInfoId(findedOrderProductInfo.getId());
        if (isTastingNoteExists) {
            throw new IllegalArgumentException("테이스팅 노트 정보가 이미 존재합니다.");
        }

        TastingNote tastingNote = request.toEntity();
        tastingNoteRepository.save(tastingNote);
        return true;
    }
}
