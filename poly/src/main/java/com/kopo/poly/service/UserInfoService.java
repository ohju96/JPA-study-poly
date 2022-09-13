package com.kopo.poly.service;

import com.kopo.poly.dto.UserInfoDto;

public interface UserInfoService {

    int insertuserInfo(UserInfoDto userInfoDto) throws Exception;

    int getUserLoginCheck(UserInfoDto userInfoDto) throws Exception;
}
