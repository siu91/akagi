package org.siu.myboot.auth.service;

/**
 * 权限校验
 *
 * @Author Siu
 * @Date 2020/4/11 16:00
 * @Version 0.0.1
 */
public interface PermitChecker {

    /**
     * 校验权限
     *
     * @param perm
     * @return
     */
    boolean hasPermit(String perm);

    /**
     * 校验刷新token权限
     *
     * @return
     */
    boolean hasRefreshTokenPermit();
}
