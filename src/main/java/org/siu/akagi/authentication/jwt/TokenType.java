package org.siu.akagi.authentication.jwt;


/**
 * @Author Siu
 * @Date 2020/4/18 19:11
 * @Version 0.0.1
 */
public enum TokenType {

    /**
     * 通用类型token
     */
    COMMON,

    /**
     * 只用于刷新token
     */
    REFRESH;


}
