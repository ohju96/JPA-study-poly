package com.example.springsession1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableRedisHttpSession
@SpringBootApplication
public class SpringSession1Application {

    public static void main(String[] args) {
        SpringApplication.run(SpringSession1Application.class, args);
    }

}
