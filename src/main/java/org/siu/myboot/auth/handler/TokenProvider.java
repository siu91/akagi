package org.siu.myboot.auth.handler;


import org.siu.myboot.auth.model.JsonWebToken;
import org.siu.myboot.auth.model.Token;
import org.springframework.security.core.Authentication;

import java.security.Key;

/**
 * token secret 服务
 *
 * @Author Siu
 * @Date 2020/4/4 12:23
 * @Version 0.0.1
 */
public interface TokenProvider {


    /**
     * 设置token secret key
     *
     * @return
     */
    boolean setKey();

    /**
     * 获取token secret key
     *
     * @return
     */
    Key getKey();


    /**
     * token 验证
     *
     * @param jwt
     * @return
     */
    Token validate(String jwt);

    /**
     * 生成 jwt 对象
     *
     * @param authentication
     * @return
     */
    JsonWebToken buildJsonWebToken(Authentication authentication);

    /**
     * 生成 jwt对象
     *
     * @param authentication
     * @param rememberMe
     * @return
     */
    JsonWebToken buildJsonWebToken(Authentication authentication, boolean rememberMe);

    /**
     * 刷新token
     *
     * @return
     */
    JsonWebToken refresh();
}
