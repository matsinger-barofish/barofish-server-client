package com.matsinger.barofishserver.utils.monitorting;

import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class MyUserService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Random random = new Random();

    // <user.name> will be used as a metric name
    // <getting-user-name> will be used as a span name
    // <userType=userType2> will be set as a tag gor both metric & span
    @Observed(name = "user.name",
              contextualName = "getting-user-name",
              lowCardinalityKeyValues = {"userType", "userType2"})
    String userName(String userId) {
        logger.info("Getting user name for ser with id<{}>", userId);
        try {
            // simulates latency
            Thread.sleep(random.nextLong(200L));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return "foo";
    }
}
