package org.siu.myboot;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.siu.myboot.auth.model.Auth;
import org.siu.myboot.auth.model.LoginUser;
import org.siu.myboot.auth.model.UserAuthorities;
import org.siu.myboot.auth.service.AbstractAuthService;
import org.siu.myboot.auth.service.LoginService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author Siu
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


    @Data
    public static class Login {

        private String username;
        private String password;
    }

    @Slf4j
    @RestController
    @RequestMapping("/v1/api")
    public static class AuthController {

        @Resource
        private LoginService loginService;


        /**
         * 登录接口
         *
         * @param login
         * @return
         */
        @PostMapping("/login")
        public Object login(@Validated @RequestBody Login login) {
            return loginService.login(login.getUsername(), login.getPassword(), false);
        }




    }


    /**
     * 实现认证授权相关的业务
     * 1、用户基本信息 2、权限列表
     */
    @Service
    public static class AuthService extends AbstractAuthService {

        @Resource
        PasswordEncoder passwordEncoder;

        @Override
        public Auth auth(String s) {
            Auth auth = new Auth();
            /**
             * 可以根据业务情况实现具体的逻辑
             * 如：判断用户是否进入黑名单/未激活等
             */

            LoginUser user = new LoginUser();
            user.setId("siu");
            user.setPass(passwordEncoder.encode("12345"));
            user.setTokenVersion(5);

            List<UserAuthorities> authoritiesList = new ArrayList<>();
            UserAuthorities authorities = new UserAuthorities();
            authorities.setRole("USER");
            authorities.setPermit("USER:UPDATE");
            authoritiesList.add(authorities);

            auth.setUser(user);
            auth.setAuthorities(authoritiesList);

            return auth;
        }
    }


}
