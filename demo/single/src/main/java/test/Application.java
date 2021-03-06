package test;


import ai.grakn.redismock.RedisServer;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.siu.akagi.annotations.Black;
import org.siu.akagi.annotations.Logout;
import org.siu.akagi.model.Authorities;
import org.siu.akagi.model.User;
import org.siu.akagi.model.UserDetails;
import org.siu.akagi.model.UsernameAndPassword;
import org.siu.akagi.support.AbstractUserDetailsService;
import org.siu.akagi.support.LoginService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author Siu
 */
@SpringBootApplication
public class Application {

    private static RedisServer server = null;

    public static void setUp() throws IOException {
        server = RedisServer.newRedisServer(6379);  // bind to a random port
        server.start();
        String h = server.getHost();//0.0.0.0
        System.out.println("内嵌redis绑定主机"+h);
    }


    public static void tireDownAll() throws IOException {
        server.stop();
        server = null;
    }


    public static void main(String[] args) throws IOException {
        setUp
                ();
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


        @GetMapping("/logout")
        public Object logout() {
            loginService.logout();
            return "success";
        }

        @Logout
        @GetMapping("/logout1")
        public Object logout1() {
            return "success";
        }

        @Black
        @GetMapping("/black")
        public Object black() {
            return "success";
        }


        /**
         * 刷新token接口（接口使用权限控制）
         *
         *  hasAuthority 和 hasAnyAuthority 来判定。 其实底层实现和 hasAnyRole 方法一样，只不过 prefix 为 null 。也就是你写入的 GrantedAuthority 是什么样子的，这里传入参数的就是什么样子的，不再受 ROLE_ 前缀的制约。
         *
         *
         * @return
         */
        @GetMapping("/refresh_token")
        //@PreAuthorize("@pms.hasPremit('dafasdfsdfa')")
        //@PreAuthorize("hasRole('ADMIN') AND hasRole('DBA')")
        //@PreAuthorize("hasAuthority('TEST1') AND hasAuthority('TEST2')")
        @PreAuthorize("hasAuthority('MY_APP_CUSTOM:REFRESH_TOKEN_PERMIT') AND hasAuthority('MY_APP_CUSTOM:REFRESH_TOKEN_PERMIT')")
        public Object refreshToken() {
            return loginService.refreshToken();

        }


    }


    /**
     * 实现认证授权相关的业务
     * 1、用户基本信息 2、权限列表
     */
    @Service
    public static class AuthService extends AbstractUserDetailsService {

        @Resource
        PasswordEncoder passwordEncoder;

        @Override
        public UserDetails loadUser(String s) {
            UserDetails userDetails = new UserDetails();
            /**
             * 以下模拟从数据库查询用户密码
             *
             * 可以根据业务情况实现具体的逻辑
             * 如：判断用户是否进入黑名单/未激活等
             */

            UsernameAndPassword usernameAndPassword = new UsernameAndPassword();
            usernameAndPassword.setName("siu");
          //  user.setPass(passwordEncoder.encode("12345"));
            usernameAndPassword.setPass("$2a$10$s/gvWeHi/XUGOgSwdlcnVeFuHLdxvQlwch97qYLkAlwMmYo6l4GDC");

            List<Authorities> authoritiesList = new ArrayList<>();
            Authorities authorities = new Authorities();
            authorities.setRole("USER");
            authorities.setPermit("USER:UPDATE");
            authoritiesList.add(authorities);


            Authorities authorities1 = new Authorities();
            authorities1.setRole("TEST1");
            authorities1.setPermit("TEST2");
            authoritiesList.add(authorities1);

            userDetails.setUsernameAndPassword(usernameAndPassword);
            userDetails.setUserAuthorities(authoritiesList);

            return userDetails;
        }
    }


}
