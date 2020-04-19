package org.siu.akagi.authentication.jwt;


import org.siu.akagi.model.JWT;
import org.springframework.security.core.Authentication;

import java.security.Key;

/**
 * Token Sign 服务
 *
 * @Author Siu
 * @Date 2020/4/4 12:23
 * @Version 0.0.1
 */
public interface TokenProvider {


    /**
     * 移除Key
     */
    void removeSignKey();


    /**
     * 获取用户的签名key
     *
     * @param user
     * @return
     */
    Key signKey(String user);



    /**
     * 交换 jwt
     *
     * @param jwt
     * @return
     */
    Token parseToken(String jwt);



    /**
     * 生成 jwt对象
     *
     * @param authentication
     * @param rememberMe
     * @return
     */
    JWT createToken(Authentication authentication, boolean rememberMe);

    /**
     * 刷新token
     *
     * @return
     */
    JWT refreshToken();
}
