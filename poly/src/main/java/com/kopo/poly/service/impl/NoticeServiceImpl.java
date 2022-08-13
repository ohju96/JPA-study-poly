package com.kopo.poly.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kopo.poly.data.dto.NoticeDto;
import com.kopo.poly.data.entity.NoticeEntity;
import com.kopo.poly.data.repository.NoticeRepository;
import com.kopo.poly.service.NoticeService;
import com.kopo.poly.util.CmmUtil;
import com.kopo.poly.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service("NoticeService")
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;

    // 공지사항 결과 조회
    @Override
    public List<NoticeDto> getNoticeList() {
        log.info(this.getClass().getName() + ".getNoticeList Start");

        // 공지사항 전체 리스트 조회하기
        List<NoticeEntity> noticeEntityList = noticeRepository.findAllByOrderByNoticeSeqDesc();

        // 엔티티의 값들을 Dto에 맞게 넣어주기
        List<NoticeDto> noticeDtoList = new ObjectMapper().convertValue(noticeEntityList,
                new TypeReference<List<NoticeDto>>() {
                });

        log.info(this.getClass().getName() + ".getNoticeList end");

        return noticeDtoList;
    }

    // 조회수 증가와 같이 테이블의 값을 변경하는 쿼리 실행은 트렌젝션을 설정한다.
    @Transactional
    @Override
    public NoticeDto getNoticeInfo(NoticeDto noticeDto, boolean type) throws Exception {
        log.info(this.getClass().getName() + ".getNoticeInfo start");

        if (type) {
            // 조회수 증가하기
            int res = noticeRepository.updateReadCnt(noticeDto.getNoticeSeq());

            // 조회수 증가 성공 체크
            log.info("res : {}", res);
        }

        // 공지사항 상세내역 가져오기
        NoticeEntity noticeEntity = noticeRepository.findByNoticeSeq(noticeDto.getNoticeSeq());

        // 엔티티의 값을 Dto에 맞게 넣기
        NoticeDto noticeDtoValue = new ObjectMapper().convertValue(noticeEntity, NoticeDto.class);

        log.info(this.getClass().getName() + ".getNoticeInfo end");
        return noticeDtoValue;
    }

    @Transactional
    @Override
    public void updateNoticeInfo(NoticeDto noticeDto) throws Exception {
        log.info(this.getClass().getName() + ".updateNoticeInfo start");

        // 수정할 값을 변수로 받기
        Long noticeSeq = noticeDto.getNoticeSeq();

        String title = CmmUtil.nvl(noticeDto.getTitle());
        String noticeYn = CmmUtil.nvl(noticeDto.getNoticeYn());
        String contents = CmmUtil.nvl(noticeDto.getContents());
        String userId = CmmUtil.nvl(noticeDto.getUserId());

        log.info("noticeSeq : {}", noticeSeq);
        log.info("title : {}", title);
        log.info("noticeYn : {}", noticeYn);
        log.info("contents : {}", contents);
        log.info("userId : {}", userId);

        NoticeEntity repositoryByNoticeSeq = noticeRepository.findByNoticeSeq(noticeSeq);

        NoticeEntity noticeEntity = NoticeEntity.builder()
                .noticeSeq(noticeSeq).title(title).noticeYn(noticeYn).contents(contents).userId(userId)
                .readCnt(repositoryByNoticeSeq.getReadCnt())
                .build();

        // 데이터 수정하기
        // JPA는 수정, 등록을 구분하지 않고 save 메서드를 사용한다.
        // 캐시에 저장된 값과 비교하고 값이 다르면 update, 값이 없으면 insert 쿼리를 실행한다.
        noticeRepository.save(noticeEntity);

        log.info(this.getClass().getName() + ".updateNoticeInfo end");
    }

    @Override
    public void deleteNoticeInfo(NoticeDto noticeDto) throws Exception {
        log.info(this.getClass().getName() + ".deleteNoticeInfo start");

        Long noticeSeq = noticeDto.getNoticeSeq();
        log.info("noticeSeq : {}", noticeSeq);

        // 데이터 수정하기
        // 삭제는 PK 컬럼을 기준으로 삭제한다.@Id 어노테이션으로 PK를 지정했다.
        noticeRepository.deleteById(noticeSeq);

        log.info(this.getClass().getName() + ".deleteNoticeInfo end");
    }

    @Override
    public void InsertNoticeInfo(NoticeDto noticeDto) throws Exception {
        log.info(this.getClass().getName() + ".InsertNoticeInfo start");

        String title = CmmUtil.nvl(noticeDto.getTitle());
        String noticeYn = CmmUtil.nvl(noticeDto.getNoticeYn());
        String contents = CmmUtil.nvl(noticeDto.getContents());
        String userId = CmmUtil.nvl(noticeDto.getUserId());

        log.info("title : {}", title);
        log.info("noticeYn : {}", noticeYn);
        log.info("contents : {}", contents);
        log.info("userId : {}", userId);

        // 공지사항 저장을 위해 PK 값은 빌더에 추가하지 않는다.
        // JPA에 자동 증가 설정을 한다.
        NoticeEntity noticeEntity = NoticeEntity.builder()
                .title(title).noticeYn(noticeYn).contents(contents).userId(userId).readCnt(0L)
                .regId(userId).regDt(DateUtil.getDateTime("yyyy-MM-dd hh:mm:ss"))
                .chgId(userId).chgDt(DateUtil.getDateTime("yyyy-MM-dd hh:mm:ss"))
                .build();

        // 공지사항 저장하기
        noticeRepository.save(noticeEntity);

        log.info(this.getClass().getName() + ".InsertNoticeInfo end");
    }
}
