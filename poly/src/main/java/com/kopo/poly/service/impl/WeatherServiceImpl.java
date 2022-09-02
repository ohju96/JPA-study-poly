package com.kopo.poly.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kopo.poly.dto.WeatherDailyDto;
import com.kopo.poly.dto.WeatherDto;
import com.kopo.poly.service.WeatherService;
import com.kopo.poly.util.CmmUtil;
import com.kopo.poly.util.NetworkUtil;
import com.kopo.poly.util.WeatherDateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class WeatherServiceImpl implements WeatherService {

    @Value("${weather.api.key}")
    private String apiKey;

    @Override
    public WeatherDto getWeather(WeatherDto weatherDto) throws Exception {
        log.info(this.getClass().getName() + ".getWeather start");

        String lat = CmmUtil.nvl(weatherDto.getLat());
        String lon = CmmUtil.nvl(weatherDto.getLon());

        String apiParam = "?lat=" + lat + "&lon=" + lon + "&appid=" + apiKey + "&units=metric";
        log.info("apiParam : {}", apiParam);

        String json = NetworkUtil.get(WeatherService.apiURL + apiParam);
        log.info("json : " + json);

        // JSON 구조를 Map 데이터 구조로 변경
        // 키와 값 구조의 JSON 구조로부터 데이터를 쉽게 가져오기위해 Map 데이터구조로 변경
        Map<String, Object> resultMap = new ObjectMapper().readValue(json, LinkedHashMap.class);

        //현재 날씨 정보를 가지고 있는 current 키의 값 가져오기
        Map<String, Double> current = (Map<String, Double>) resultMap.get("current");
        log.info("current : " + current.get("temp"));

        double currentTemp = current.get("temp"); //현재 기온
        log.info("현재 기온 : {}", currentTemp);

        //일별 날씨 조회 OpenAPI가 현재 날짜 기준으로 최대 7일까지 제공
        List<Map<String, Object>> dailyList = (List<Map<String, Object>>) resultMap.get("daily");

        //7일 동안 날씨 정보를 저장할 데이터
        // OpenAPI로부터 필요한 정보만 가져와서 처리하기 쉬운 JSON 구조로 변경에 활용
        List<WeatherDailyDto> weatherDailyDtoList = new LinkedList<>();

        for (Map<String, Object> dailyMap : dailyList) {
            String day = WeatherDateUtil.getLongDateTime(dailyMap.get("dt"), "yyyy-MM-dd"); //기준 날짜
            String sunrise = WeatherDateUtil.getLongDateTime(dailyMap.get("sunrise")); //해뜨는 시간
            String sunset = WeatherDateUtil.getLongDateTime(dailyMap.get("sunset")); //해지는 시간
            String moonrise = WeatherDateUtil.getLongDateTime(dailyMap.get("moonrise"));
            String moonset = WeatherDateUtil.getLongDateTime(dailyMap.get("moonset"));

            log.info("today : {}", day);
            log.info("해뜨는 시간 : {}", sunrise);
            log.info("해지는 시간 : {}", sunset);
            log.info("달뜨는 시간 : {}", moonrise);
            log.info("해뜨는 시간 : {}", moonset);

            Map<String, Double> dailyTemp = (Map<String, Double>) dailyMap.get("temp");

            //숫자형태보다 문자열 형태가 데이터처리하기 쉽기 때문에 Double 형태를 문자열로 변경
            String dayTemp = String.valueOf(dailyTemp.get("day"));
            String dayTempMax = String.valueOf(dailyTemp.get("max"));
            String dayTempMin = String.valueOf(dailyTemp.get("min"));

            log.info("평균 기온 : {}", dayTemp);
            log.info("최고 기온 : {}", dayTempMax);
            log.info("최저 기온 : {}", dayTempMin);

            WeatherDailyDto weatherDailyDto = new WeatherDailyDto();
            weatherDailyDto.setDay(day);
            weatherDailyDto.setSunrise(sunrise);
            weatherDailyDto.setSunset(sunset);
            weatherDailyDto.setMoonrise(moonrise);
            weatherDailyDto.setMoonset(moonset);
            weatherDailyDto.setDayTemp(dayTemp);
            weatherDailyDto.setDayTempMax(dayTempMax);
            weatherDailyDto.setDayTempMin(dayTempMin);

            weatherDailyDtoList.add(weatherDailyDto); //일별 날씨 정보를 리스트에 추가하기
            weatherDailyDto = null;
        }

        WeatherDto weatherDtos = new WeatherDto();
        weatherDtos.setLat(lat);
        weatherDtos.setLon(lon);
        weatherDtos.setCurrentTemp(currentTemp);
        weatherDtos.setDailyList(weatherDailyDtoList);

        log.info(this.getClass().getName() + ".getWeather end");
        return weatherDtos;
    }
}
