package com.kopo.poly.service;

import com.kopo.poly.dto.WeatherDto;

public interface WeatherService {

    String apiURL = "https://api.openweathermap.org/data/3.0/onecall";

    // 날씨 API를 호출하여 날씨 결과 받아오기
    WeatherDto getWeather(WeatherDto weatherDto) throws Exception;
}
