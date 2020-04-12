[![License](https://img.shields.io/github/license/apache/incubator-streampipes.svg)](http://www.apache.org/licenses/LICENSE-2.0)

# Akagi<img src="./assets/LOGO.png" align="right" />

> **Akagi**让你的SpringBoot应用快速集成认证授权





:mask:疫情无情，人有情，**Star**:star:一下吧 :point_up:



## 设计

![image-20200412212934256](./assets/akagi.png)

## 接入模式说明

### SINGLE模式

![image-20200404115623785](./assets/akagi-single.png)

### CS模式

![image-20200404115753822](./assets/akagi-cs.png)

## Quick Start

* git clone & mvn install 

* Add dependency

  ```java
      <dependency>
        <groupId>org.siu</groupId>
        <artifactId>akagi</artifactId>
        <version>${version}</version>
      </dependency>
  ```

- SINGLE 模式接入

  - 配置

    ```yml
    akagi:
      security:
        # 开放无需认证的接口
        permit-all:
          - "/v1/api/login"
    # redis 配置
    spring:
      redis:
        host: redis.host
        port: 6379
        timeout: 20000
        lettuce:
          pool:
            max-active: 200
            max-wait: -1
            max-idle: 10
            min-idle: 0
    ```

  - 代码示例

    ```java
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
             * @return
             */
            @GetMapping("/refresh_token")
            //@PreAuthorize("@pms.hasPremit('dafasdfsdfa')")
            //@PreAuthorize("hasRole('ADMIN') AND hasRole('DBA')")
            @PreAuthorize("hasAuthority('TEST1') AND hasAuthority('TEST2')")
            public Object refreshToken() {
                return loginService.refreshToken();
    
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
                 * 以下模拟从数据库查询用户密码
                 *
                 * 可以根据业务情况实现具体的逻辑
                 * 如：判断用户是否进入黑名单/未激活等
                 */
    
                LoginUser user = new LoginUser();
                user.setId("siu");
                user.setPass(passwordEncoder.encode("12345"));
    
                List<Authorities> authoritiesList = new ArrayList<>();
                Authorities authorities = new Authorities();
                authorities.setRole("USER");
                authorities.setPermit("USER:UPDATE");
                authoritiesList.add(authorities);
    
    
                Authorities authorities1 = new Authorities();
                authorities1.setRole("TEST1");
                authorities1.setPermit("TEST2");
                authoritiesList.add(authorities1);
    
                auth.setUser(user);
                auth.setAuthorities(authoritiesList);
    
                return auth;
            }
        }
    
    
    }
    
    ```

- CS模式接入

  - SERVER（同SINGLE模式）

  - CLIENT

    - 配置
  
      ```yml
      akagi:
        security:
          # 开放无需认证的接口
            permit-all:
            - "/v1/client/api/login"
          mode: CS_CLIENT
      ```
  
      
  
    - 代码示例
  
      ```java
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
      ```
  
      
  
  

## TODO

1. 测试


## Feedback

 [gshiwen@gmail.com](mailto:gshiwen@gmail.com)

## License

[Apache License 2.0](LICENSE)



