package com.matsinger.barofishserver.utils.monitorting;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MyController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final MyUserService myUserService;

    @GetMapping("/user/{userId}")
    String userName(@PathVariable(name = "userId") String userId) {
        logger.info("Got a request");
        return myUserService.userName(userId);
    }
}
