package com.kopo.poly.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kopo.poly.data.dto.ChatDto;
import com.kopo.poly.data.dto.PapagoDto;
import com.kopo.poly.service.PapagoService;
import com.kopo.poly.util.CmmUtil;
import com.kopo.poly.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChatHandler extends TextWebSocketHandler {

    // 웹 소켓에 접속되는 사용자들을 저장, 중복 제거를 위해 Set 데이터구조 사용
    private static Set<WebSocketSession> clients = Collections.synchronizedSet(new LinkedHashSet<>());

    // 채팅룸 조회하기 위해 사용
    public static Map<String, String> roomInfo = Collections.synchronizedMap(new LinkedHashMap<>());

    private final PapagoService papagoService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        log.info(this.getClass().getName() + ".afterConnectionEstablished start");

        String roomName = CmmUtil.nvl((String) session.getAttributes().get("roomName"));
        String userName = CmmUtil.nvl((String) session.getAttributes().get("userName"));
        String roomNameHash = CmmUtil.nvl((String) session.getAttributes().get("roomNameHash"));

        log.info("1roomName : {}", roomName);
        log.info("1userName : {}", userName);
        log.info("1roomNameHash : {}", roomNameHash);

        // 웹소켓에 접속된 모든 사용자 검색
        clients.forEach(s -> {

            // 내가 접속한 채팅방에 있는 세션만 메시지 보내기
            if (roomNameHash.equals(s.getAttributes().get("roomNameHash"))) {
                try {
                    //{"name":"오주현","msg":"ㅎㅇ","date":"2022. 7. 25. 오전 9:30:57"}
                    ChatDto chatDto = new ChatDto();
                    chatDto.setName("관리자");
                    chatDto.setMsg(userName + "님이" + roomName + " 채팅방에 입장하셨습니다.");
                    chatDto.setDate(DateUtil.getDateTime("yyyyMMdd hh:mm:ss"));

                    String json = new ObjectMapper().writeValueAsString(chatDto);
                    log.info("json : {}", json);

                    TextMessage chatMsg = new TextMessage(json);
                    s.sendMessage(chatMsg);

                    chatDto = null;
                } catch (IOException e) {
                    log.info("Error : {}", e);
                }
            }
        });

        // 기존 세션에 존재하지 않으면 신규 세션이기 때문에 저장한다.
        if (!clients.contains(session)) {
            clients.add(session); // 접속된 세션 저장
            roomInfo.put(roomName, roomNameHash); // 생성된 채팅룸 이름 저장

            log.info("session open : {}", session);
        }

        log.info(this.getClass().getName() + ".afterConnectionEstablished end");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info(this.getClass().getName() + ".handleTextMessage start");

        String roomName = CmmUtil.nvl((String) session.getAttributes().get("roomName"));
        String userName = CmmUtil.nvl((String) session.getAttributes().get("userName"));
        String roomNameHash = CmmUtil.nvl((String) session.getAttributes().get("roomNameHash"));

        log.info("roomName : {}", roomName);
        log.info("userName : {}", userName);
        log.info("roomNameHash : {}", roomNameHash);

        // 채팅 메시지
        String msg = CmmUtil.nvl(message.getPayload());
        log.info("msg : {}", msg);

        // 발송시간 추가를 위해 JSON 문자열을 Dto로 변환
        ChatDto chatDto = new ObjectMapper().readValue(msg, ChatDto.class);

        // 메시지 발송시간 서버 시간으로 설정하여 추가하기
        chatDto.setDate(DateUtil.getDateTime("yyyy-MM-dd hh:mm:ss"));

        // == 번역을 위한 기능 추가 파트 == //

        String sendMsg = CmmUtil.nvl(chatDto.getMsg()); //발송하는 메시지 번역을 위해 가져온다.
        log.info("sendMsg : {}", sendMsg);

        // 채팅 메시지 네이버 PapagoAPI로 영작 및 번역
        PapagoDto papagoDto = new PapagoDto();
        papagoDto.setText(sendMsg);

        PapagoDto resultPapagoDto = papagoService.translate(papagoDto);

        if (resultPapagoDto == null) {
            resultPapagoDto = new PapagoDto();
        }

        papagoDto = null;

        String translatedText = CmmUtil.nvl(resultPapagoDto.getTranslatedText()); // 번역된 글
        String scrLangType = CmmUtil.nvl(resultPapagoDto.getScrLangType()); // 원문의 언어 종류
        String tarLangType = CmmUtil.nvl(resultPapagoDto.getTarLangType());

        log.info("translatedText : {}", translatedText);
        log.info("scrLangType : {}", scrLangType);
        log.info("tarLangType : {}", tarLangType);

        sendMsg = "(원문) " + sendMsg; //발송하는 채팅 메시지

        if (tarLangType.equals("en")) {
            sendMsg += "=> (영어 영작) " + translatedText;
        } else if (tarLangType.equals("ko")) {
            sendMsg += "=> (한국어 번역) " + translatedText;
        }

        chatDto.setMsg(sendMsg);

        // == 파트 종료 == //


        // ChatDto를 JSON으로 다시 변환하기
        String json = new ObjectMapper().writeValueAsString(chatDto);
        log.info("json : {}", json);

        // 웹소켓에 접속된 모든 사용자 검색
        clients.forEach(s -> {
            // 내가 접속한 채팅방에 있는 세션만 메시지 보내기
            if (roomNameHash.equals(s.getAttributes().get("roomNameHash"))){
                try {
                    TextMessage chatMsg = new TextMessage(json);
                    s.sendMessage(chatMsg);
                } catch (IOException e) {
                    log.info("Error : {}", e);
                }
            }
        });

        log.info(this.getClass().getName() + ".handleTextMessage end");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info(this.getClass().getName() + ".afterConnectionClosed start");

        String roomName = CmmUtil.nvl((String) session.getAttributes().get("roomName"));
        String userName = CmmUtil.nvl((String) session.getAttributes().get("userName"));
        String roomNameHash = CmmUtil.nvl((String) session.getAttributes().get("roomNameHash"));

        log.info("roomName : {}", roomName);
        log.info("userName : {}", userName);
        log.info("roomNameHash : {}", roomNameHash);

        clients.remove(session); // 접속되어 있는 세션 삭제

        // 웹 소켓에 접속된 모든 사용자 검색
        clients.forEach(s -> {

            // 내가 접속한 채팅방에 있는 세션만 메시지 보내기
            if (roomNameHash.equals(s.getAttributes().get("roomNameHash"))) {

                try {
                    ChatDto chatDto = new ChatDto();
                    chatDto.setName("관리자");
                    chatDto.setMsg(userName + "님이 " + roomName + " 채팅방에 퇴장하셨습니다.");
                    chatDto.setDate(DateUtil.getDateTime("yyyy-MM-dd hh:mm:ss"));

                    String json = new ObjectMapper().writeValueAsString(chatDto);
                    log.info("json : {}", json);

                    TextMessage chatMsg = new TextMessage(json);
                    s.sendMessage(chatMsg);

                    chatDto = null;

                } catch (IOException e) {
                    log.info("Error : {}", e);
                }
            }
        });

        log.info(this.getClass().getName() + ".afterConnectionClosed end");
    }
}
