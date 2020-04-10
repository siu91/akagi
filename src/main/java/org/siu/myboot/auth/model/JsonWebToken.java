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
public class JsonWebToken {

    /**
     * 用于验证获取服务端资源
     */
    @Getter
    private String token;

    /**
     * 用于客户端主动刷新token
     */
    @Getter
    private String refreshToken;

    public JsonWebToken(String username, String token, String refreshToken) {
        this.token = Constant.Auth.TOKEN_PREFIX + Base64.getEncoder().encodeToString(username.getBytes()) + Constant.Auth.TOKEN_SPLIT + token;
        this.refreshToken = Constant.Auth.TOKEN_PREFIX + Base64.getEncoder().encodeToString(username.getBytes()) + Constant.Auth.TOKEN_SPLIT + refreshToken;
    }


}
