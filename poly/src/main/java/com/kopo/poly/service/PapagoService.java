package com.kopo.poly.service;

import com.kopo.poly.data.dto.PapagoDto;

public interface PapagoService {

    //Papago 언어감지 API
    String detectLongsApiURL = "https://openapi.naver.com/v1/papago/detectLangs";

    //Papago 번역 API
    String translateApiURL = "https://openapi.naver.com/v1/papago/n2mt";

    //네이버 파파고 API를 호출하여 입력된 언어가 어느 나라 언어인지 찾기
    PapagoDto detectLangs(PapagoDto papagoDto) throws Exception;

    // 네이버 Papago API를 호출하여 언어 감지 후 번역
    PapagoDto translate(PapagoDto papagoDto) throws Exception;
}
