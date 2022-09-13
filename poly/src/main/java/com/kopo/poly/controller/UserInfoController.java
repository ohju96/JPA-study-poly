package com.kopo.poly.controller;

import com.kopo.poly.dto.UserInfoDto;
import com.kopo.poly.service.UserInfoService;
import com.kopo.poly.util.CmmUtil;
import com.kopo.poly.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@RequestMapping("/user")
@Controller
@RequiredArgsConstructor
public class UserInfoController {

    private final UserInfoService userInfoService;

    @GetMapping("/userReg/Form")
    public String userRegForm() {
        log.info(this.getClass().getName() + ".user/userRegForm ok");

        return "/user/UserRegForm";
    }

    @PostMapping("/insertUserInfo")
    public String insertUserInfo(HttpServletRequest request, ModelMap model) throws Exception {
        log.info(this.getClass().getName() + ".insertUserInfo start");

        String msg = "";

        UserInfoDto userInfoDto = null;

        try {
            String user_id = CmmUtil.nvl(request.getParameter("user_id"));
            String user_name = CmmUtil.nvl(request.getParameter("user_name"));
            String password = CmmUtil.nvl(request.getParameter("password"));
            String email = CmmUtil.nvl(request.getParameter("email"));
            String addr1 = CmmUtil.nvl(request.getParameter("addr1"));
            String addr2 = CmmUtil.nvl(request.getParameter("addr2"));

            userInfoDto = new UserInfoDto();

            userInfoDto.setUserId(user_id);
            userInfoDto.setUserName(user_name);
            userInfoDto.setPassword(EncryptUtil.encHashSHA256(password));
            userInfoDto.setEmail(EncryptUtil.encAES128CBC(email));
            userInfoDto.setAddr1(addr1);
            userInfoDto.setAddr2(addr2);

            int res = userInfoService.insertuserInfo(userInfoDto);

            log.info("회원가입 결과 res : {}", res);

            if (res == 1) {
                msg = "회원가입되었습니다.";
            } else if (res == 2) {
                msg = "이미 가입된 이메일 주소입니다.";
            } else {
                msg = "오류로 인해 회원가입이 실패하였습니다.";
            }
        } catch (Exception e) {
            msg = "실패하였습니다. : " + e;
            log.info(e.toString());
            e.printStackTrace();
        } finally {
            log.info(this.getClass().getName() + ".insertUserInfo end");

            model.addAttribute("msg", msg);
            model.addAttribute("userInfoDto",userInfoDto);

            userInfoDto = null;
        }

        return "/user/UserRegSuccess";
    }

    @GetMapping(value = "/loginForm")
    public String loginForm() {
        log.info(this.getClass().getName() + "user/loginForm ok");
        return "/user/LoginForm";
    }

    @PostMapping(value = "/getuserLoginCheck")
    public String getUserLoginCheck(HttpSession session, HttpServletRequest request, ModelMap model) throws Exception {
        log.info(this.getClass().getName() + ".getUserLoginCheck start");

        int res = 0;

        UserInfoDto userInfoDto = null;

        try {
            String user_id = CmmUtil.nvl(request.getParameter("user_id"));
            String password = CmmUtil.nvl(request.getParameter("password"));

            log.info("user id : {}", user_id);
            log.info("password : {}", password);

            userInfoDto = new UserInfoDto();
            userInfoDto.setUserId(user_id);
            userInfoDto.setPassword(EncryptUtil.encHashSHA256(password));

            res = userInfoService.getUserLoginCheck(userInfoDto);

            log.info("res : {}", res);

            if (res == 1) {
                session.setAttribute("SS_USER_ID", user_id);
            }

        } catch (Exception e) {
            res = 2;
            log.info(e.toString());
            e.printStackTrace();

        } finally {
            log.info(this.getClass().getName() + ".getUserLoginCheck end");

            model.addAttribute("res", String.valueOf(res));

            userInfoDto = null;
        }
        return "/user/LoginResult";
    }

}
