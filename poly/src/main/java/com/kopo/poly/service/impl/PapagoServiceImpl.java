package com.kopo.poly.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kopo.poly.data.dto.PapagoDto;
import com.kopo.poly.service.PapagoService;
import com.kopo.poly.util.CmmUtil;
import com.kopo.poly.util.NetworkUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Service
public class PapagoServiceImpl implements PapagoService {

    @Value("${naver.papago.clientId}")
    private String clientId;

    @Value("${naver.papago.clientSecret}")
    private String clientSecret;

    //== 네이버 API 사용을 위한 접속 정보 설정하기 ==//
    private Map<String, String> setNaverInfo() {

        HashMap<String, String> requestheader = new HashMap<>();

        requestheader.put("X-Naver-Client-Id", clientId);
        requestheader.put("X-Naver-Client-Secret", clientSecret);

        log.info("clientId : {}", clientId);
        log.info("clientSecret : {}", clientSecret);

        return requestheader;
    }

    @Override
    public PapagoDto detectLangs(PapagoDto papagoDto) throws Exception {
        log.info("### detectLangs {} : start", this.getClass().getName());

        // 영작할 문장
        String text = CmmUtil.nvl(papagoDto.getText());

        // 호출할 papago 번역 API 정보 설정
        String param = "query=" + URLEncoder.encode(text, "UTF-8");

        // papagoAPI 호출하기
        String json = NetworkUtil.post(PapagoService.detectLongsApiURL, this.setNaverInfo(), param);

        log.info("### json : {}", json);

        // JSON 구조를 Map 데이터 구조로 변경
        // 키와 값 구조의 JSON 구조로부터 데이터를 쉽게 가져오기 위해 Map 데이터구조로 변경
        PapagoDto resultPapagoDto = new ObjectMapper().readValue(json, PapagoDto.class);

        // 언어 감지를 위한 원문 저장
        resultPapagoDto.setText(text);

        log.info("### detectLangs {} : end", this.getClass().getName());
        return resultPapagoDto;
    }

    @Override
    public PapagoDto translate(PapagoDto papagoDto) throws Exception {
        log.info("### start : {}", this.getClass().getName());

        //언어 종류 찾기
        PapagoDto resultPapagoDto = this.detectLangs(papagoDto);

        // 찾은 언어 종류
        String langCode = CmmUtil.nvl(resultPapagoDto.getLangCode());

        // 사용 후 메모리에서 삭제
        resultPapagoDto = null;

        String source = ""; // 원문 영어
        String target = ""; // 번역할 언어

        if (langCode.equals("ko")) {
            source = "ko";
            target = "en";
        } else if (langCode.equals("en")) {
            source = "en";
            target = "ko";
        } else {
            //한국어와 영어가 아니면 에러 발생
            new Exception("한국어와 영어만 번역합니다.");
        }

        String text = CmmUtil.nvl(papagoDto.getText()); // 번역할 문장

        // 한국어를 영어로 번역하기 위한 파라미터 설정
        String postParams = "source=" + source + "&target=" + target + "&text=" + URLEncoder.encode(text, "UTF-8");
        log.info("postParams : {}", postParams);

        //Papago API 호출
        String json = NetworkUtil.post(PapagoService.translateApiURL, this.setNaverInfo(), postParams);
        log.info("json : {}", json);

        // JSON 구조를 Map 데이터 구조로 변경하기
        // 키와 값 구조의 JSON 구조로부터 데이터를 쉽게 가져오기 위해 Map 데이터구조로 변경한다.
        Map<String, Object> resultMap = new ObjectMapper().readValue(json, LinkedHashMap.class);

        // 결과 내용 중 message 정보 가져오기
        Map<String, Object> messageMap = (Map<String, Object>) resultMap.get("message");

        // message 결과 내용 중 result 정보가져오기
        Map<String, String> messageResultMap = (Map<String, String>) messageMap.get("result");
        log.info("messageResultMap : {}", messageResultMap);

        String srcLangType = CmmUtil.nvl(messageResultMap.get("srcLangType"));
        String tarLangType = CmmUtil.nvl(messageResultMap.get("tarLangType"));
        String translatedText = CmmUtil.nvl(messageResultMap.get("translatedText"));

        log.info("srcLangType : {}", srcLangType);
        log.info("tarLangType : {}", tarLangType);
        log.info("translatedText : {}", translatedText);

        PapagoDto resultDto = new PapagoDto();
        resultDto.setText(text);
        resultDto.setTranslatedText(translatedText);
        resultDto.setScrLangType(srcLangType);
        resultDto.setTarLangType(tarLangType);

        messageMap = null;
        messageResultMap = null;
        resultMap = null;

        log.info("### end : {}", this.getClass().getName());
        return resultDto;
    }
}
