package com.matsinger.barofishserver.infra.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class PrometheusApplication {

    private final MeterRegistry meterRegistry;
    private Counter counter;

    @Bean
    public InMemoryHttpExchangeRepository inMemoryHttpExchangeRepository() {
        return new InMemoryHttpExchangeRepository();
    }

    public PrometheusApplication(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @PostConstruct
    public void init() {
        counter = meterRegistry.counter("api.call.count");
    }

    public static void main(String[] args) {
        SpringApplication.run(PrometheusApplication.class, args);
    }

    @GetMapping("/test")
    public String test() {
        counter.increment();
        return "test";
    }
}
