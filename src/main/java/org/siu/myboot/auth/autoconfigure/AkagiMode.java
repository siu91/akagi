package org.siu.myboot.auth.autoconfigure;

/**
 * 运行模式
 *
 * @Author Siu
 * @Date 2020/4/4 11:10
 * @Version 0.0.1
 */
public enum AkagiMode {

    /**
     * 单体应用模式，集成授权认证、资源权限校验
     */
    SINGLE,
    /**
     * CS模式，服务端提供认证和授权，客服端接收token（用户信息/认证授权信息）进行资源权限校验
     */
    CS_SERVER,
    CS_CLIENT
}