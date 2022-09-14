package com.kopo.poly.controller;

import com.kopo.poly.data.dto.PapagoDto;
import com.kopo.poly.service.PapagoService;
import com.kopo.poly.util.CmmUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@Slf4j
@RequestMapping("/papago")
@RequiredArgsConstructor
public class PapagoController {

    private final PapagoService papagoService;

    @GetMapping("/detectLangs")
    private PapagoDto detectLangs(HttpServletRequest request)
        throws Exception {
        log.info("### {} : start", this.getClass().getName());

        // 분석할 문장
        String text = CmmUtil.nvl(request.getParameter("text"));

        log.info("text : {}", text);

        PapagoDto papagoDto = new PapagoDto();
        papagoDto.setText(text);

        // 입력된 문장의 언어 감지를 위해 서비스 호출하여 결과 받기
        PapagoDto resultPapagoDto = papagoService.detectLangs(papagoDto);

        if (resultPapagoDto == null) {
            resultPapagoDto = new PapagoDto();
        }

        log.info("### {} : end", this.getClass().getName());
        return resultPapagoDto;
    }

    @GetMapping("/translate")
    public PapagoDto translate(HttpServletRequest request) throws Exception {
        log.info(this.getClass().getName() + ".translate start");

        String text = CmmUtil.nvl(request.getParameter("text"));
        log.info("text : {}", text);

        PapagoDto papagoDto = new PapagoDto();
        papagoDto.setText(text);

        PapagoDto resultDto = papagoService.translate(papagoDto);

        if (resultDto == null) {
            resultDto = new PapagoDto();
        }

        log.info(this.getClass().getName() + ".translate end");
        return resultDto;
    }

}
