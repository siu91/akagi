package org.siu.myboot.auth.service;


import java.security.Key;

/**
 * token secret 服务
 *
 * @Author Siu
 * @Date 2020/4/4 12:23
 * @Version 0.0.1
 */
public interface ITokenSecretService {


    /**
     * 设置token secret
     *
     * @return
     */
    boolean setTokenSecret();

    /**
     * 获取token secret key
     *
     * @return
     */
    Key getKey();
}
