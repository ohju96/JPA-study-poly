package com.example.springsession2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableRedisHttpSession
@SpringBootApplication
public class SpringSession2Application {

    public static void main(String[] args) {
        SpringApplication.run(SpringSession2Application.class, args);
    }

}
