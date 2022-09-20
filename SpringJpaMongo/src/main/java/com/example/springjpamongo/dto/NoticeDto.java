package com.example.springjpamongo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class NoticeDto {

    private String noticeSeq;
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
