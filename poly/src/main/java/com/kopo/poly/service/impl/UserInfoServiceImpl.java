package com.kopo.poly.service.impl;

import com.kopo.poly.data.entity.UserInfoEntity;
import com.kopo.poly.data.repository.UserInfoRepository;
import com.kopo.poly.dto.UserInfoDto;
import com.kopo.poly.service.UserInfoService;
import com.kopo.poly.util.CmmUtil;
import com.kopo.poly.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserInfoServiceImpl implements UserInfoService {

    private final UserInfoRepository userInfoRepository;

    @Override
    public int insertuserInfo(UserInfoDto userInfoDto) throws Exception {

        int res = 0;

        String userId = CmmUtil.nvl(userInfoDto.getUserId());
        String userName = CmmUtil.nvl(userInfoDto.getUserName());
        String password = CmmUtil.nvl(userInfoDto.getPassword());
        String email = CmmUtil.nvl(userInfoDto.getEmail());
        String addr1 = CmmUtil.nvl(userInfoDto.getAddr1());
        String addr2 = CmmUtil.nvl(userInfoDto.getAddr2());

        log.info("userId : {}", userId);
        log.info("userName : {}", userName);
        log.info("password : {}", password);
        log.info("email : {}", email);
        log.info("addr1 : {}", addr1);
        log.info("addr2 : {}", addr2);

        Optional<UserInfoEntity> resultEmtity = userInfoRepository.findByUserId(userId);

        if (resultEmtity.isPresent()) { // 값이 존재한다면
            res = 2;
        } else {
            UserInfoEntity build = UserInfoEntity.builder()
                    .userId(userId).userName(userName).password(password).email(email)
                    .addr1(addr1).addr2(addr2)
                    .reg_id(userId).reg_dt(DateUtil.getDateTime("yyyy-MM-dd hh:mm:ss"))
                    .chgId(userId).chgDt(DateUtil.getDateTime("yyyy-MM-dd hh:mm:ss"))
                    .build();

            userInfoRepository.save(build);

            resultEmtity = userInfoRepository.findByUserId(userId);

            if (resultEmtity.isPresent()) {
                res = 1;
            } else {
                res = 0;
            }
        }

        return res;
    }

    @Override
    public int getUserLoginCheck(UserInfoDto userInfoDto) throws Exception {

        int res = 0;

        String userId = CmmUtil.nvl(userInfoDto.getUserId());
        String password = CmmUtil.nvl(userInfoDto.getPassword());

        log.info("userId : {}", userId);
        log.info("password : {}", password);

        Optional<UserInfoEntity> resultEntity = userInfoRepository.findByUserIdAndPassword(userId, password);

        if (resultEntity.isPresent()) {
            res = 1;
        }

        return res;
    }
}
