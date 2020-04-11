package org.siu.myboot.auth.model;

import lombok.Getter;
import org.siu.myboot.auth.constant.Constant;

import java.util.Base64;

/**
 * Json Web Token
 *
 * @Author Siu
 * @Date 2020/4/7 20:19
 * @Version 0.0.1
 */
public class JWT {

    /**
     * 用于验证获取服务端资源
     */
    @Getter
    private String token;

    /**
     * 用于客户端主动刷新token
     */
    @Getter
    private String refresh;

    public JWT(String username, String token, String refresh) {
        this.token = Constant.Auth.TOKEN_PREFIX + Base64.getEncoder().encodeToString(username.getBytes()) + Constant.Auth.TOKEN_SPLIT + token;
        this.refresh = Constant.Auth.TOKEN_PREFIX + Base64.getEncoder().encodeToString(username.getBytes()) + Constant.Auth.TOKEN_SPLIT + refresh;
    }


}
