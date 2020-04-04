[![License](https://img.shields.io/github/license/apache/incubator-streampipes.svg)](http://www.apache.org/licenses/LICENSE-2.0)

# Akagi<img src="./assets/LOGO.png" align="right" />

> **Akagi**让你的SpringBoot应用快速集成认证授权





:mask:疫情无情，人有情，**Star**:star:一下吧 :point_up:



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

- 配置 yml：

  ```yml
  akagi:
    security:
        
  ```
  
- 实现抽象方法 AbstractAuthDetailsService#auth(String loginId) ：Auth

  ```java
  /**
   * 实现认证授权相关的业务
   * 如通过loginId（用户名/手机号等）从数据库中查找用户信息:1、用户基本信息 2、权限列表
   */
  
  @Slf4j
  @Component("userDetailsService")
  public class UserDetailsServiceImpl extends AbstractAuthDetailsService {
            
      ...
  
      /**
       * @param loginId 用户登录的ID（用户、手机等）
       * @return
       */
      @SneakyThrows
      @Override
      public Auth auth(final String loginId) {
          User user = repo.findByUserNameOrPhone(loginId);
          List<UserAuthorities> authorities = repo.findUserAuthorities(loginId);     
          return new Auth(user, authorities);
      }
  
  ```
  
- 登录接口

  ```java
      
      @Resource
      private LoginService loginService;
  
      @PostMapping("/auth")
      public Result<String> authorize(@Validated @RequestBody Login login) {
          String jwt = loginService.login(login.getUser(), login.getPass());
          return Result.ok(Constant.Auth.TOKEN_PREFIX + jwt);
  
      }
  ```
  
  
  
- 接口上使用权限控制

  ```java
  
      @PostMapping("/password")
      @PreAuthorize("@pms.hasPermit('USER')")
      public Object test() {
         // do whatever
      }
  ```

## TODO

1. 测试


## Feedback

 [gshiwen@gmail.com](mailto:gshiwen@gmail.com)

## License

[Apache License 2.0](LICENSE)



