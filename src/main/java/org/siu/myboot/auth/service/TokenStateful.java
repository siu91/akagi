package org.siu.myboot.auth.service;


/**
 * token 有状态
 *
 * @Author Siu
 * @Date 2020/4/4 12:23
 * @Version 0.0.1
 */
public interface TokenStateful {

    /**
     * 获取token版本
     *
     * @param user
     * @return
     */
    Long getTokenVersion(final String user);

    /**
     * 设置token版本
     *
     * @param user
     * @param value
     * @return
     */
    boolean setTokenVersion(final String user, long value);

    boolean update();
}
