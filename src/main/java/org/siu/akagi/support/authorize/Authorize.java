package org.siu.akagi.support.authorize;

/**
 * 鉴权
 *
 * @Author Siu
 * @Date 2020/4/11 16:00
 * @Version 0.0.1
 */
public interface Authorize {


    /**
     * 校验角色
     *
     * @param role
     * @return
     */
    boolean hasRole(String... role);

    /**
     * 校验权限
     *
     * @param authority
     * @return
     */
    boolean hasAuthority(String... authority);

    /**
     * 校验所有
     *
     * @param any
     * @return
     */
    boolean hasAny(String... any);

    /**
     * 校验特殊实现
     *
     * @return
     */
    boolean has();

}
