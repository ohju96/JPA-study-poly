package com.example.springjpamongo.service.impl;

import com.example.springjpamongo.dto.NoticeDto;
import com.example.springjpamongo.dto.NoticeRepository;
import com.example.springjpamongo.repository.entity.NoticeEntity;
import com.example.springjpamongo.service.NoticeService;
import com.example.springjpamongo.util.CmmUtil;
import com.example.springjpamongo.util.DateUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service("NoticeService")
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;

    @Override
    public List<NoticeDto> getNoticeList() {
        log.info("### 시작");

        // 공지사항 전체 조회
        List<NoticeEntity> resultList = noticeRepository.findAllByOrderByNoticeSeqDesc();

        // 엔티티의 값들을 Dto에 맞게 넣기
        List<NoticeDto> noticeDtoList = new ObjectMapper().convertValue(resultList,
                new TypeReference<List<NoticeDto>>() {
                });

        log.info("### 종료");
        return noticeDtoList;
    }

    @Transactional
    @Override
    public NoticeDto getNoticeInfo(NoticeDto noticeDto, boolean type) throws Exception {
        log.info("### 시작");

        NoticeEntity resultEntity = noticeRepository.findByNoticeSeq(noticeDto.getNoticeSeq());

        if (type) {
            //조회수 증가하기
            // MongoDB는 @DynamicUpdate를 아직 지원하지 않아 기존 값을 불러와 저장하고 수정할 값을 넣어야 한다.
            NoticeEntity noticeEntity = NoticeEntity.builder()
                    .noticeSeq(resultEntity.getNoticeSeq()).title(resultEntity.getTitle())
                    .noticeYn(resultEntity.getNoticeYn()).contents(resultEntity.getContents())
                    .userId(resultEntity.getUserId())
                    .readCnt(resultEntity.getReadCnt() + 1)
                    .regId(resultEntity.getRegId()).regDt(resultEntity.getRegDt())
                    .chgId(resultEntity.getChgId()).chgDt(resultEntity.getChgDt())
                    .build();

            noticeRepository.save(noticeEntity);

            resultEntity = noticeRepository.findByNoticeSeq(noticeEntity.getNoticeSeq());
        }

        NoticeDto value = new ObjectMapper().convertValue(resultEntity, NoticeDto.class);

        log.info("### 종료");
        return value;
    }

    @Transactional
    @Override
    public void updateNoticeInfo(NoticeDto noticeDto) throws Exception {
        log.info("### 시작");

        String noticeSeq = CmmUtil.nvl(noticeDto.getNoticeSeq());
        String title = CmmUtil.nvl(noticeDto.getTitle());
        String noticeYn = CmmUtil.nvl(noticeDto.getNoticeYn());
        String contents = CmmUtil.nvl(noticeDto.getContents());
        String userId = CmmUtil.nvl(noticeDto.getUserId());

        log.info("noticeSeq : {}", noticeSeq);
        log.info("title : {}", title);
        log.info("noticeYn : {}", noticeYn);
        log.info("contents : {}", contents);
        log.info("userId : {}", userId);

        NoticeEntity resultEntity = noticeRepository.findByNoticeSeq(noticeDto.getNoticeSeq());

        NoticeEntity noticeEntity = NoticeEntity.builder()
                .noticeSeq(noticeSeq).title(title)
                .userId(userId)
                .readCnt(resultEntity.getReadCnt())
                .regId(resultEntity.getRegId()).regDt(resultEntity.getRegDt())
                .chgId(userId).chgDt(DateUtil.getDateTime("yyyy-MM-dd hh:mm:ss"))
                .build();

        // 데이터 수정
        noticeRepository.save(noticeEntity);

        log.info("### 종료");
    }

    @Transactional
    @Override
    public void deleteNoticeInfo(NoticeDto noticeDto) throws Exception {
        log.info("### 시작");

        String noticeSeq = CmmUtil.nvl(noticeDto.getNoticeSeq());
        log.info("noticeSeq : {}", noticeSeq);

        noticeRepository.deleteById(noticeSeq);

        log.info("### 종ㄹ");
    }

    @Transactional
    @Override
    public void InsertNoticeInfo(NoticeDto noticeDto) throws Exception {
        log.info("### 시작");

        String title = CmmUtil.nvl(noticeDto.getTitle());
        String noticeYn = CmmUtil.nvl(noticeDto.getNoticeYn());
        String contents = CmmUtil.nvl(noticeDto.getContents());
        String userId = CmmUtil.nvl(noticeDto.getUserId());

        log.info("title : {}", title);
        log.info("noticeYn : {}", noticeYn);
        log.info("contents : {}", contents);
        log.info("userId : {}", userId);

        // 공지사항 저장을 위해서는 PK 값은 빌더에 추가하지 않는다.
        // JPA에 자동 증가 설정을 해놨다.
        NoticeEntity noticeEntity = NoticeEntity.builder()
                .title(title).noticeYn(noticeYn).contents(contents).userId(userId).readCnt(0L)
                .regId(userId).regDt(DateUtil.getDateTime("yyyy-MM-dd hh:mm:ss"))
                .chgId(userId).chgDt(DateUtil.getDateTime("yyyy-MM-dd hh:mm:ss"))
                .build();

        // 공지사항 저장하기
        noticeRepository.save(noticeEntity);

        log.info("### 종료");
    }
}
