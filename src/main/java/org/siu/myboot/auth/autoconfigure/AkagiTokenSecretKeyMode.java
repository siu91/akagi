package org.siu.myboot.auth.autoconfigure;

/**
 * Token Secret 模式
 *
 * 默认
 *
 * @Author Siu
 * @Date 2020/4/4 11:10
 * @Version 0.0.1
 */
public enum AkagiTokenSecretKeyMode {

    /**
     * 所有用户公用一个 token secret
     */
    PUBLIC,

    /**
     * 每个用户单独的 token secret(保存在本地)
     */
    CUSTOM_LOCAL,
    /**
     * 每个用户单独的 token secret（保存在redis）
     */
    CUSTOM_REDIS
}