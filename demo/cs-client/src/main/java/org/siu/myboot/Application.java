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
        public Object login() {
            // 请求服务端（CS_SERVER）获取token
            return "{\n" +
                    "    \"token\": \"Bearer eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJBa2FnaSBUb2tlbiBQcm92aWRlciIsInN1YiI6InNpdSIsImF1dGgiOiJVU0VSLFVTRVI6VVBEQVRFIiwib19hdXRoIjoiIiwidmVyc2lvbiI6NSwiZXhwIjoxNTg2NDg0MTMwLCJuYmYiOjE1ODYzOTc3MzEsImlhdCI6MTU4NjM5NzczMX0.cKTH6mk59B2_Y1BOEhrf5fTvrbSAxS2ijehK19GKeo47GC4mie8XKl1nKSMMeU1fvLKzkb1s-qYnMpq_D6R97g\",\n" +
                    "    \"refreshToken\": \"Bearer eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJBa2FnaSBSZWZyZXNoIFRva2VuIFByb3ZpZGVyIiwic3ViIjoic2l1IiwiYXV0aCI6IlNZUzpSRUZSRVNIX1RPS0VOIiwib19hdXRoIjoiVVNFUixVU0VSOlVQREFURSIsInZlcnNpb24iOjUsImV4cCI6MTU4NjM5ODMzNiwibmJmIjoxNTg2Mzk3NzMxLCJpYXQiOjE1ODYzOTc3MzF9.FzYfDLr39OoMsnxm3DPuIIwCQXNCn2jsvm_JK43t2sX65ljN2Ao0FnKTGTBk-MbXWCe1DEo0dIsq13bgMuvPKg\"\n" +
                    "}";
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
