package com.kopo.poly.data.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class NoticeDto {

    /**
     * 순서대로
     * 기본키, 제목, 공지글 여부, 글 내용, 작성자, 조회수, 등록자 아이디, 등록일, 수정자 이이디, 수정일
     * 등록자명
     */
    private Long noticeSeq;
    private String title;
    private String noticeYn;
    private String contents;
    private String userId;
    private String readCnt;
    private String regId;
    private String regDt;
    private String chgId;
    private String chgDt;

    private String userName;
}
