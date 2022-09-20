package com.example.springjpamongo.service;

import com.example.springjpamongo.dto.NoticeDto;

import java.util.List;

public interface NoticeService {

    //공지사항 전체 가져오기
    List<NoticeDto> getNoticeList();

    //공지사항 상세 정보 가져오기
    NoticeDto getNoticeInfo(NoticeDto noticeDto, boolean type) throws Exception;

    // 해당 공지사항 수정하기
    void updateNoticeInfo(NoticeDto noticeDto) throws Exception;

    // 해당 공지사항 삭제하기
    void deleteNoticeInfo(NoticeDto noticeDto) throws Exception;

    // 해당 공지사항 저장하기
    void InsertNoticeInfo(NoticeDto noticeDto) throws Exception;

}
