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
    void remove();

    /**
     * 设置Token Sign key
     *
     */
    void store();

    /**
     * 获取用户的签名key
     *
     * @param user
     * @return
     */
    Key get(String user);


    /**
     * token 验证
     *
     * @param jwt
     * @return
     */
    Token authentication(String jwt);

    /**
     * 生成 jwt 对象
     *
     * @param authentication
     * @return
     */
    JWT create(Authentication authentication);

    /**
     * 生成 jwt对象
     *
     * @param authentication
     * @param rememberMe
     * @return
     */
    JWT create(Authentication authentication, boolean rememberMe);

    /**
     * 刷新token
     *
     * @return
     */
    JWT refresh();
}
