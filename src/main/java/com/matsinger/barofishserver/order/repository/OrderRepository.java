package com.matsinger.barofishserver.order.repository;

import com.matsinger.barofishserver.order.object.OrderState;
import com.matsinger.barofishserver.order.object.Orders;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Orders, String> {
    @Query(value = "select concat(date_format(current_timestamp()+interval 9 hour,'%y%m%d%H%i%s'),lpad(nextval" +
            "(order_seq),4,'0')) id", nativeQuery = true)
    public Tuple selectOrderId();

    public List<Orders> findAllByStateNotInAndUserId(List<OrderState> orderStates,Integer userId);

    public List<Orders> findAllByStateInAndUserId(List<OrderState> orderStates, Integer userId);

}
