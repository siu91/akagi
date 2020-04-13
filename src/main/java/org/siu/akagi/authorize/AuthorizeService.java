package org.siu.akagi.authorize;

/**
 * 鉴权
 *
 * @Author Siu
 * @Date 2020/4/13 10:35
 * @Version 0.0.1
 */
public class AuthorizeService extends AbstractAuthorize {

    @Override
    public boolean hasRole(String... role) {
        return this.hasAny(role);
    }

    @Override
    public boolean hasAuthority(String... authority) {
        return this.hasAny(authority);
    }
}
