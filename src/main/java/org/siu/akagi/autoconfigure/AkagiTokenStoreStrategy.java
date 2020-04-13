package org.siu.akagi.autoconfigure;

/**
 * Token Sign Key 保存的策略
 *
 * @Author Siu
 * @Date 2020/4/4 11:10
 * @Version 0.0.1
 */
public enum AkagiTokenStoreStrategy {

    /**
     * 所有用户公用一个 Token Sign，token无状态，原生的 Json Web Token
     */
    NATIVE,

    /**
     * 每个用户单独的 Token Sign Key 保存在本地，不支持分布式下注销/拉黑
     */
    LOCAL,
    /**
     * 每个用户单独的 Token Sign Key 保存在Redis，支持分布式下注销/拉黑
     */
    REDIS
}