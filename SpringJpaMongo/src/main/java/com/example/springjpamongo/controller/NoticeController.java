package com.example.springjpamongo.controller;

import com.example.springjpamongo.dto.NoticeDto;
import com.example.springjpamongo.service.impl.NoticeServiceImpl;
import com.example.springjpamongo.util.CmmUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequestMapping("/notice")
@Controller
public class NoticeController {

    @Resource(name = "NoticeService")
    private NoticeServiceImpl noticeService;

    // 게시판 리스트 보여주기
    @GetMapping("/noticeList")
    private String noticeList(ModelMap model) {
        log.info("### 시작");

        List<NoticeDto> noticeList = noticeService.getNoticeList();

        if (noticeList == null) {
            noticeList = new ArrayList<NoticeDto>();
        }

        // 조호된 리스트 결과값 넣어주기
        model.addAttribute("rList", noticeList);

        noticeList = null;

        log.info("### 종료");
        return "/notice/NoticeList";
    }

    // 게시판 상세보기
    @GetMapping("/noticeInfo")
    public String noticeInfo(HttpServletRequest request, ModelMap model) throws Exception{
        log.info("### 시작");

        // 게시판 글 등록을 위해 사용되는 폼 객체의 하위 인풋 객체 등을 받아오기 위해 사용
        String nSeq = CmmUtil.nvl(request.getParameter("nSeq"));

        log.info("nSeq : {}", nSeq);

        NoticeDto noticeDto = new NoticeDto();
        noticeDto.setNoticeSeq(nSeq);

        NoticeDto noticeInfo = noticeService.getNoticeInfo(noticeDto, true);
        if (noticeInfo == null) {
            noticeInfo = new NoticeDto();
        }

        model.addAttribute("rDTO", noticeInfo);

        log.info("### 종료");
        return "/notice/NoticeInfo";
    }

    // 게시판 수정 보기
    @GetMapping("/noticeEditInfo")
    public String noticeEditInfo(HttpServletRequest request, ModelMap model) throws Exception {
        log.info("### 시작");

        String msg = "";

        try {

            String nSeq = CmmUtil.nvl(request.getParameter("nSeq"));
            log.info("nSeq : {}", nSeq);

            NoticeDto noticeDto = new NoticeDto();
            noticeDto.setNoticeSeq(nSeq);

            NoticeDto resultNoticeDto = noticeService.getNoticeInfo(noticeDto, false);

            if (resultNoticeDto == null) {
                resultNoticeDto = new NoticeDto();
            }

            model.addAttribute("rDTO", resultNoticeDto);

        } catch (Exception e) {
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();
        } finally {
            log.info("### 종료");

            model.addAttribute("msg", msg);
        }

            log.info("### 종료");
        return "/notice/NoticeEditInfo";
    }

    // 게시판 글 수정
    @PostMapping("noticeUpdate")
    public String noticeUpdate(HttpSession session, HttpServletRequest request, ModelMap model) {
        log.info("### 시작");

        String msg = "";

        try {
            String user_id = CmmUtil.nvl((String) session.getAttribute("SESSION_USER_ID"));
            String nSeq = CmmUtil.nvl(request.getParameter("nSeq"));
            String title = CmmUtil.nvl(request.getParameter("title"));
            String noticeYn = CmmUtil.nvl(request.getParameter("noticeYn"));
            String contents = CmmUtil.nvl((request.getParameter("contents")));

            log.info("user_id : {}", user_id);
            log.info("nSeq : {}", nSeq);
            log.info("title : {}", title);
            log.info("noticeYn : {}", noticeYn);
            log.info("contents : {}", contents);

            NoticeDto noticeDto = new NoticeDto();
            noticeDto.setUserId(user_id);
            noticeDto.setNoticeSeq(nSeq);
            noticeDto.setTitle(title);
            noticeDto.setNoticeYn(noticeYn);
            noticeDto.setContents(contents);

            // 게시글 수정하기
            noticeService.updateNoticeInfo(noticeDto);

            msg = "수정되었습니다.";

        } catch (Exception e) {
            msg = "실패하였습니다. : " + e.toString();
            log.info(e.toString());
            e.printStackTrace();
        } finally {
            log.info("### 종료");
            model.addAttribute("msg", msg);
        }
        return "/notice/MsgToList";
    }

    // 게시판 글 삭제
    @GetMapping("/noticeDelete")
    public String noticeDelete(HttpServletRequest request, ModelMap model) {
        log.info("### 시작");

        String msg = "";

        try {
            String nSeq = CmmUtil.nvl(request.getParameter("nSeq"));
            log.info("nSeq : {}", nSeq);

            NoticeDto noticeDto = new NoticeDto();
            noticeDto.setNoticeSeq(nSeq);

            noticeService.deleteNoticeInfo(noticeDto);

            msg = "삭제되었습니다.";

        } catch (Exception e) {
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();
        } finally {
            log.info("### 종료");
            model.addAttribute("msg", msg);
        }
        return "/notice/MsgToList";
    }

    // 게시판 페이지 이동
    @GetMapping("/noticeReg")
    public String noticeReg() {
        log.info("## 시작");
        log.info("## 종료");

        return "/notice/NoticeReg";
    }

    // 게시판 글 등록
    @PostMapping("/noticeInsert")
    public String noticeInsert(HttpSession session, HttpServletRequest request, ModelMap model) {
        log.info("## 시작");

        String msg = "";

        try {
            String user_id = CmmUtil.nvl((String) session.getAttribute("SESSION_USER_ID"));
            String title = CmmUtil.nvl(request.getParameter("title"));
            String noticeYn = CmmUtil.nvl(request.getParameter("noticeYn"));
            String contents = CmmUtil.nvl((request.getParameter("contents")));

            log.info("user_id : {}", user_id);
            log.info("title : {}", title);
            log.info("noticeYn : {}", noticeYn);
            log.info("contents : {}", contents);

            NoticeDto noticeDto = new NoticeDto();
            noticeDto.setUserId(user_id);
            noticeDto.setTitle(title);
            noticeDto.setNoticeYn(noticeYn);
            noticeDto.setContents(contents);

            noticeService.InsertNoticeInfo(noticeDto);

            msg = "등록되었습니다.";
        } catch (Exception e) {
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();
        } finally {
            log.info("## 종료");
            model.addAttribute("msg", msg);
        }
        return "/notice/MsgToList";
    }


}
