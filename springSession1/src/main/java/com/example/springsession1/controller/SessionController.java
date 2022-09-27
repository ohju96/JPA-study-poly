package com.example.springsession1.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@Slf4j
@RestController
public class SessionController {

    @GetMapping("/session/test")
    public String sessionTest(HttpSession session) {
        log.info("### 시작");

        String sessionId = session.getId();

        session.setAttribute("test", "1234");

        String test = (String) session.getAttribute("test");


        log.info("### test : {}", test);

        log.info("### 종료");

        return sessionId;
    }
}
