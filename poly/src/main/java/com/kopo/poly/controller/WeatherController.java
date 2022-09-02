package com.kopo.poly.controller;

import com.kopo.poly.dto.WeatherDto;
import com.kopo.poly.service.WeatherService;
import com.kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RequestMapping(value = "/weather")
@RestController
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping("/getWeather")
    public WeatherDto getWeather(HttpServletRequest request) throws Exception {
        log.info(this.getClass().getName() + ".getWeather start");

        String lat = CmmUtil.nvl(request.getParameter("lat"));
        String lon = CmmUtil.nvl(request.getParameter("lon"));

        WeatherDto weatherDto = new WeatherDto();
        weatherDto.setLat(lat);
        weatherDto.setLon(lon);

        // 내가 필요한 정보만 추출한 날씨 정보 가져오기
        WeatherDto weather = weatherService.getWeather(weatherDto);

        if (weather == null) {
            weather = new WeatherDto();
        }

        log.info(this.getClass().getName() + ".getWeather end");
        return weather;
    }

}
