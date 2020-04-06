package org.siu.myboot;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @Author Siu
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


    @Slf4j
    @RestController
    @RequestMapping("/v1/client/api")
    public static class AuthController {



        /**
         * 登录接口
         *
         * @return
         */
        @PostMapping("/login")
        public String login() {
            // 请求服务端（CS_SERVER）获取token
            return "token";
        }


        /**
         * 接口使用权限控制
         *
         * @return
         */
        @PostMapping("/test")
        @PreAuthorize("@pms.hasPermit('USER')")
        public Object test() {
            return "success";

        }

    }


}
