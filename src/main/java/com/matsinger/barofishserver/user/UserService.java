package com.matsinger.barofishserver.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Optional<User> selectUserOptional(Integer id) {
        try {
            return userRepository.findById(id);
        } catch (Exception e) {
            return null;
        }
    }
}
