package com.matsinger.barofishserver;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.stereotype.Component;

@Component
public class ProfilesCheck implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        // 여기에 특정 환경 조건을 확인하는 로직을 작성합니다.
        // 예를 들어, 특정 프로파일이 활성화되어 있는지 확인할 수 있습니다.
        System.out.println("context = " + context.getEnvironment());
        return context.getEnvironment().acceptsProfiles("prod");
    }
}
