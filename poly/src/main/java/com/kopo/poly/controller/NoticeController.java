package com.kopo.poly.controller;

import com.kopo.poly.data.dto.NoticeDto;
import com.kopo.poly.service.NoticeService;
import com.kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/notice")
@Controller
public class NoticeController {

    private final NoticeService noticeService;

    // 게시판 리스트 보여주기
    @GetMapping("/noticeList")
    public String noticeList(ModelMap model) {
        log.info(this.getClass().getName() + ".noticeList start");

        // 공지사항 리스트 가져오기
        List<NoticeDto> noticeList = noticeService.getNoticeList();

        if (noticeList == null) {
            noticeList = new ArrayList<NoticeDto>();
        }

        // 조회된 리스트 결과값 넣어주기
        model.addAttribute("noticeList", noticeList);
        log.info("### noticeList : {}", noticeList.get(0).getTitle());

        // 변수 초기화(메모리 효율화 시키기 위해 사용)
        noticeList = null;

        log.info(this.getClass().getName() + ".noticeList end");
        return "/notice/NoticeList";
    }

    // 게시판 상세보기
    @GetMapping("/noticeInfo")
    public String noticeInfo(HttpServletRequest request, ModelMap model) throws Exception {
        log.info(this.getClass().getName() + ".noticeInfo start");

        // 게시판 글 등록되기 위해 사용되는 form 객체의 하위 input 객체 등을 받아오기 위해 사용
        String nSeq = CmmUtil.nvl(request.getParameter("nSeq"));

        // 반드시, 값을 받았으면 꼭 로그를 찍어서 값이 제대로 들어오는지 파악해야 한다. 반드시 작성해야 한다.
        log.info("nSeq : {}", nSeq);

        // 값 전달은 반드시 DTO 객체를 이용해 처리한다.
        NoticeDto noticeDto = new NoticeDto();
        noticeDto.setNoticeSeq(Long.parseLong(nSeq));

        // 공지사항 상세정보 가져오기
        NoticeDto noticeInfo = noticeService.getNoticeInfo(noticeDto, true);

        if (noticeInfo == null) {
            noticeInfo = new NoticeDto();
        }

        // 조회된 리스트 결과값 넣어주기
        model.addAttribute("noticeInfo", noticeInfo);

        log.info(this.getClass().getName() + ".noticeInfo end");
        return "/notice/NoticeInfo";
    }

    // 게시판 수정 보기
    @GetMapping("/noticeEditInfo")
    public String noticeEditInfo(HttpServletRequest request, ModelMap model) {
        log.info(this.getClass().getName() + ".noticeEditInfo start");

        String msg = "";

        try {
            String nSeq = CmmUtil.nvl(request.getParameter("nSeq"));// 공지글번호 PK

            log.info("nSeq : {}", nSeq);

            NoticeDto noticeDto = new NoticeDto();
            noticeDto.setNoticeSeq(Long.parseLong(nSeq));

            NoticeDto noticeInfo = noticeService.getNoticeInfo(noticeDto, false);

        } catch (Exception e) {
            msg = "실패하였습니다. : " + e.getMessage();

            log.info(e.toString());
            e.printStackTrace();
        } finally {
            log.info(this.getClass().getName() + ".NoticeUpdate end!");

            // 결과 메시지 전달하기
            model.addAttribute("msg", msg);
        }

        log.info(this.getClass().getName() + ".noticeEditInfo end");
        return "/notice/NoticeEditInfo";
    }

    // 게시판 글 수정
    @PostMapping("/noticeUpdate")
    public String NoticeUpdate(HttpSession session, HttpServletRequest request, ModelMap model) {
        log.info(this.getClass().getName() + ".noticeUpdate start");

        String msg = "";

        try {
            String user_id = CmmUtil.nvl((String) session.getAttribute("SESSION_USER_ID"));
            String nSeq = CmmUtil.nvl(request.getParameter("nSeq"));
            String title = CmmUtil.nvl(request.getParameter("title"));
            String noticeYn = CmmUtil.nvl(request.getParameter("noticeYn"));
            String contents = CmmUtil.nvl(request.getParameter("contents"));

            log.info("user_id : {}", user_id);
            log.info("nSeq : {}", nSeq);
            log.info("title : {}", title);
            log.info("noticeYn : {}", noticeYn);
            log.info("contents : {}", contents);

            NoticeDto noticeDto = new NoticeDto();
            noticeDto.setUserId(user_id);
            noticeDto.setNoticeSeq(Long.parseLong(nSeq));
            noticeDto.setTitle(title);
            noticeDto.setNoticeYn(noticeYn);
            noticeDto.setContents(contents);

            // 게시글 수정하기 DB
            noticeService.updateNoticeInfo(noticeDto);

            msg = "수정되었습니다.";

        } catch (Exception e) {
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();
        } finally {
            log.info(this.getClass().getName() + ".noticeUpdate end");

            // 결과 메시지 전달하기
            model.addAttribute("msg", msg);
        }

        log.info(this.getClass().getName() + ".noticeUpdate end");
        return "/notice/MsgToList";
    }

    // 게시판 글 삭제
    @GetMapping("/noticeDelete")
    public String noticeDelete(HttpServletRequest request, ModelMap model) {
        log.info(this.getClass().getName() + ".noticeDelete start");

        String msg = "";

        try {
            String nSeq = CmmUtil.nvl(request.getParameter("nSeq"));
            log.info("nSep : {}", nSeq);

            NoticeDto noticeDto = new NoticeDto();
            noticeDto.setNoticeSeq(Long.parseLong(nSeq));

            // 게시글 삭제하기 DB
            noticeService.deleteNoticeInfo(noticeDto);

            msg = "삭제되었습니다.";

        } catch (Exception e) {
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();
        } finally {
            log.info(this.getClass().getName() + ".noticeDelete end");

            // 결과 메시지 전달하기
            model.addAttribute("msg", msg);
        }
        return "/notice/MsgToList";
    }

    // 게시판 작성 페이지 이동
    @GetMapping("/noticeReg")
    public String noticeReg() {
        log.info(this.getClass().getName() + ".noticeReg start");

        log.info(this.getClass().getName() + ".noticeReg end");
        return "/notice/NoticeReg";
    }

    // 게시판 글 등록
    @PostMapping("/noticeInsert")
    public String noticeInsert(HttpSession session, HttpServletRequest request, ModelMap model) {
        log.info(this.getClass().getName() + ".noticeInsert start");

        String msg = "";

        try {
            // 게시판 글 등록되기 위해 사용되는 form 객체의 하위 input 객체 등을 받아오기 위해 사용
            String user_id = CmmUtil.nvl((String) session.getAttribute("SESSION_USER_ID"));
            String title = CmmUtil.nvl(request.getParameter("title"));
            String noticeYn = CmmUtil.nvl(request.getParameter("noticeYn"));
            String contents = CmmUtil.nvl(request.getParameter("contents"));

            log.info("user_id : {}", user_id);
            log.info("title : {}", title);
            log.info("noticeYn : {}", noticeYn);
            log.info("contents : {}", contents);

            NoticeDto noticeDto = new NoticeDto();
            noticeDto.setUserId(user_id);
            noticeDto.setTitle(title);
            noticeDto.setNoticeYn(noticeYn);
            noticeDto.setContents(contents);

            // 게시글 등록하기 위한 비즈니스 로직을 호출
            noticeService.InsertNoticeInfo(noticeDto);

            // 저장이 완료되면 사용자에게 보여줄 메시지
            msg = "등록되었습니다.";


        } catch (Exception e) {
            // 저장이 실패되면 사용자에게 보여줄 메시지
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();
        } finally {
            log.info(this.getClass().getName() + ".noticeInsert end");

            // 결과 메시지 전달하기
            model.addAttribute("msg", msg);
        }
        return "/notice/MsgToList";
    }
}
