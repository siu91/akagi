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
 * 自动配置相关配置项
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
        if (this.permitAllUri == null) {
            this.permitAllUri = new HashSet<>();
        }
        this.permitAllUri.addAll(Constant.Auth.PERMIT_ALL_API);
    }


    /**
     * 模式
     * 默认：单体应用接入模式
     * 1、单体应用模式，集成授权认证、资源权限校验
     * 2、CS模式，服务端提供认证和授权，客服端接收token（用户信息/认证授权信息）进行资源权限校验
     */
    private AkagiMode mode = AkagiMode.SINGLE;

    /**
     * Token Sign Key 保存策略
     * 默认：原生JWT策略，token无状态
     * <p>
     * PUBLIC : 所有用户公用一个 Token Sign,无状态，不支持注销/拉黑 (推荐)
     * CUSTOM_LOCAL : 每个用户单独的 Token Sign Key 保存在本地，不支持分布式下注销/拉黑
     * CUSTOM_REDIS : 每个用户单独的 Token Sign Key 保存在Redis，支持分布式下注销/拉黑（推荐）
     */
    private AkagiTokenStoreStrategy tokenStoreStrategy = AkagiTokenStoreStrategy.NATIVE;

    /**
     * 超级用户
     */
    private String superUser;

    /**
     * 开放无需认证的接口:支持 Ant Matcher
     */
    private Set<String> permitAllUri;


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
     * 权限校验，默认使用 AuthorizeService
     */
    private Class<? extends Authorize> authorizeServiceClass = AuthorizeService.class;

}
