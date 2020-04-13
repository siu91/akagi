package org.siu.akagi.autoconfigure;

import lombok.Data;
import org.siu.akagi.authorize.AuthorizeService;
import org.siu.akagi.constant.Constant;
import org.siu.akagi.authorize.Authorize;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

/**
 * security 配置
 *
 * @Author Siu
 * @Date 2020/3/12 22:04
 * @Version 0.0.1
 */
@ConfigurationProperties(prefix = AkagiProperties.PREFIX)
@Data
public class AkagiProperties {

    public static final String PREFIX = "akagi.security";

    @PostConstruct
    private void init() {
        if (this.permitAll == null) {
            this.permitAll = new HashSet<>();
        }
        this.permitAll.addAll(Constant.Auth.PERMIT_ALL_API);
    }


    /**
     * 模式
     * 默认：单体应用接入模式
     * 1、单体应用模式，集成授权认证、资源权限校验
     * 2、CS模式，服务端提供认证和授权，客服端接收token（用户信息/认证授权信息）进行资源权限校验
     */
    private AkagiMode mode = AkagiMode.SINGLE;

    /**
     * Token Sign Key 模式
     * PUBLIC : 所有用户公用一个 Token Sign
     * CUSTOM : 每个用户单独的 Token Sign
     */
    private AkagiTokenSignKeyMode tokenSignKeyMode = AkagiTokenSignKeyMode.PUBLIC;

    /**
     * 超级用户
     */
    private String superUser;

    /**
     * 开放无需认证的接口:支持 Ant Matcher
     */
    private Set<String> permitAll;


    /**
     * token base64 secret
     */
    private String jsonWebTokenBase64Secret = "ZmQ0ZGI5NjQ0MDQwY2I4MjMxY2Y3ZmI3MjdhN2ZmMjNhODViOTg1ZGE0NTBjMGM4NDA5NzYxMjdjOWMwYWRmZTBlZjlhNGY3ZTg4Y2U3YTE1ODVkZDU5Y2Y3OGYwZWE1NzUzNWQ2YjFjZDc0NGMxZWU2MmQ3MjY1NzJmNTE0MzI=";

    /**
     * 默认token失效时间(秒)
     */
    private long jsonWebTokenExpire = 86400;

    /**
     * 记住密码时token失效时间(秒)
     */
    private long jsonWebTokenExpireForRemember = 108000;

    /**
     * 刷新token权限标识，默认："SYS:REFRESH_TOKEN"
     */
    private String jsonWebTokenRefreshPermit = Constant.Auth.JSON_WEB_TOKEN_REFRESH_PERMIT;


    /**
     * 密码加密器，默认使用 BCryptPasswordEncoder
     */
    private Class<? extends PasswordEncoder> passwordEncoder = BCryptPasswordEncoder.class;

    /**
     * 权限校验，默认使用 PermitService
     */
    private Class<? extends Authorize> authService = AuthorizeService.class;



    public Set<String> getPermitAll() {
        return permitAll;
    }

    public void setPermitAll(Set<String> permitAll) {
        this.permitAll = permitAll;
    }
}
