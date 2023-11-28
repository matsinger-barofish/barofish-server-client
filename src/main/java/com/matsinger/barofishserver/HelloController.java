//package com.matsinger.barofishserver;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.client.RestTemplate;
//
//@RestController
//class HelloController {
//
//    private static final Logger logger = LoggerFactory.getLogger(HelloController.class);
//    private final RestTemplate restTemplate;
//
//    HelloController(RestTemplate restTemplate) {
//        this.restTemplate = restTemplate;
//    }
//
//    @GetMapping("/hello")
//    public String hello() {
//        logger.info("---------- Hello method started ----------");
//        ResponseEntity<String> responseEntity = this.restTemplate.postForEntity("https://httpbin.org/post", "Hello, Cloud!", String.class);
//        return responseEntity.getBody();
//    }
//}
