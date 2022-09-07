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
}
