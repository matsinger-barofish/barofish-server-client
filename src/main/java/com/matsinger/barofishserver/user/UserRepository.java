package com.matsinger.barofishserver.user;

import com.matsinger.barofishserver.user.object.User;
import com.matsinger.barofishserver.user.object.UserState;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {
    List<User> findAllByState(UserState state);

    List<User> findAllByIdIn(List<Integer> ids);

    Optional<User> findById(@NotNull Integer id);

    Integer countAllByJoinAtBetween(Timestamp joinAtS, Timestamp joinAtE);
}
