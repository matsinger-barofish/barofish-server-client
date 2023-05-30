package com.matsinger.barofishserver.order.repository;


import com.matsinger.barofishserver.order.Order;
import com.matsinger.barofishserver.order.exception.OrderBusinessException;
import com.matsinger.barofishserver.order.exception.OrderErrorMessage;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {

    private final EntityManager em;

    @Transactional
    public Optional<Order> createSequence(Order order) {
        String orderId = generateOrderNumber(order.getOrderedAt());
        order.setId(orderId);
        return Optional.ofNullable(order);
    }

    private String generateOrderNumber(LocalDateTime orderDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMddHHmmss");
        String formattedDateTime = orderDateTime.format(formatter);
        // Order 테이블의 최대 시퀀스 값을 조회하여 현재 시퀀스 값을 결정
        int currentSequence = calculateCurrentSequence(orderDateTime);
        String formattedSequence = String.format("%04d", currentSequence);
        log.info("### formattedSequence: {} ###", formattedDateTime + "-" + formattedSequence);
        return formattedDateTime + "-" + formattedSequence;
    }

    private int calculateCurrentSequence(LocalDateTime orderDateTime) {
        // Order 테이블에서 해당 일자와 동일한 주문의 수를 조회
        String formattedDateTime = orderDateTime.format(DateTimeFormatter.ofPattern("yyMMddHHmm"));

        String query = "SELECT COUNT(o) FROM Order o WHERE " +
                "FUNCTION('DATE_FORMAT', o.orderedAt, '%y%m%d%H%i') = " +
                "FUNCTION('DATE_FORMAT', :formattedDateTime, '%y%m%d%H%i')";

        int currentSequence = getSequence(formattedDateTime, query);

        if (currentSequence > 9999) {
            throw new OrderBusinessException(OrderErrorMessage.ORDER_SEQUENCE_EXCEPTION);
        }
        log.info("### currentSequence: {} ###", currentSequence);

        return currentSequence;
    }

    private int getSequence(String formattedDateTime, String query) {
        int currentSequence;
        try {
            // JPQL 실행
            TypedQuery<Long> countQuery = em.createQuery(query, Long.class)
                    .setParameter("formattedDateTime", formattedDateTime);
            Long countResult = countQuery.getSingleResult();
            // 조회된 주문의 수에 +1하여 현재 시퀀스 값을 결정
            currentSequence = countResult.intValue() + 1;
        } catch (NoResultException e) {
            currentSequence = 0;
        }
        return currentSequence;
    }
}
