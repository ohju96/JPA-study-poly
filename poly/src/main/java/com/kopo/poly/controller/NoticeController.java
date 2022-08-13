package com.kopo.poly.controller;

import com.kopo.poly.data.dto.NoticeDto;
import com.kopo.poly.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/notice")
@Controller
public class NoticeController {

    private final NoticeService noticeService;

    // 게시판 리스트 보여주기
    @GetMapping("noticeList")
    public String noticeList(ModelMap model) {
        log.info(this.getClass().getName() + ".noticeList start");

        // 공지사항 리스트 가져오기
        List<NoticeDto> noticeList = noticeService.getNoticeList();

        if (noticeList == null) {
            noticeList = new ArrayList<NoticeDto>();
        }

        // 조회된 리스트 결과값 넣어주기
        model.addAttribute("noticeList", noticeList);

        // 변수 초기화(메모리 효율화 시키기 위해 사용)
        noticeList = null;

        log.info(this.getClass().getName() + ".noticeList end");
        return "/notice/NoticeList";
    }


}
