package com.matsinger.barofishserver.user.repository;

import com.matsinger.barofishserver.user.domain.User;
import com.matsinger.barofishserver.user.domain.UserState;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {
    List<User> findAllByState(UserState state);

    Optional<User> findById(@NotNull Integer id);

    List<User> findAllByIdIn(List<Integer> ids);

    Integer countAllByJoinAtBetween(Timestamp joinAtS, Timestamp joinAtE);

    List<User> findAllByWithdrawAtBefore(Timestamp timestamp);

    void deleteAllByIdIn(List<Integer> userIds);
}
