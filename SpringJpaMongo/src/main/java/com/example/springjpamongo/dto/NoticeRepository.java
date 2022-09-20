package com.example.springjpamongo.dto;

import com.example.springjpamongo.repository.entity.NoticeEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends MongoRepository<NoticeEntity, String> {

    List<NoticeEntity> findAllByOrderByNoticeSeqDesc();

    NoticeEntity findByNoticeSeq(String noticeSeq);
}
