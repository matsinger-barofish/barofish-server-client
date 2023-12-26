package com.matsinger.barofishserver.monitoring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
class HelloController {

//    @Autowired
//    private TracerProvider tracerProvider;

    private static final Logger LOGGER = LoggerFactory.getLogger(HelloController.class);
    private final RestTemplate restTemplate;

    HelloController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/hello")
    public String hello() {
        LOGGER.info("---------Hello method started---------");
        ResponseEntity<String> responseEntity = this.restTemplate.postForEntity("https://httpbin.org/post", "Hello, Cloud!", String.class);
//        tracerProvider.toString();
        if ( 3 == 3) {
            throw new RuntimeException("hello warning !!!");
        }

        return responseEntity.getBody();
    }

    @PostMapping("/hello")
    public String helloPost(@RequestBody TestRequest request) {
        LOGGER.info("---------Hello method started---------");
        ResponseEntity<String> responseEntity = this.restTemplate.postForEntity("https://httpbin.org/post", "Hello, Cloud!", String.class);
//        tracerProvider.toString();
        if ( 3 == 3) {
            throw new RuntimeException("hello warning !!!");
        }

        return responseEntity.getBody();
    }
}
