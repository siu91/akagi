package org.siu.myboot.auth.autoconfigure;

import lombok.Data;
import org.siu.myboot.auth.constant.Constant;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

/**
 * spring security 配置
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
     * 开放无需认证的接口
     */
    private Set<String> permitAll;

    /**
     * 是否让token有状态，默认放入redis，可以实现注销/黑名单
     */
    private boolean statefulToken;


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


    public Set<String> getPermitAll() {
        return permitAll;
    }

    public void setPermitAll(Set<String> permitAll) {
        this.permitAll = permitAll;
    }
}
