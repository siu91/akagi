package org.siu.akagi.autoconfigure;

/**
 * Token Sign 模式
 *
 * 默认
 *
 * @Author Siu
 * @Date 2020/4/4 11:10
 * @Version 0.0.1
 */
public enum AkagiTokenSignKeyMode {

    /**
     * 所有用户公用一个 Token Sign
     */
    PUBLIC,

    /**
     * 每个用户单独的 Token Sign(保存在本地)
     */
    CUSTOM_LOCAL,
    /**
     * 每个用户单独的 Token Sign（保存在redis）
     */
    CUSTOM_REDIS
}