package org.siu.akagi.support.authorize;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 默认权限校验服务实现
 *
 * @Author Siu
 * @Date 2020/3/9 11:00
 * @Version 0.0.1
 */
@Slf4j
public class AuthorizeServiceWithSuperUser extends AbstractAuthorize {

    /**
     * 超级管理员拥有最高权限
     */
    private String superUser;

    private String refreshTokenPermit;


    public AuthorizeServiceWithSuperUser(String superUser, String refreshTokenPermit) {
        this.superUser = superUser;
        this.refreshTokenPermit = refreshTokenPermit;
    }

    /**
     * 是否有刷新token的权限
     *
     * @return
     */
    @Override
    public boolean has() {
        return this.hasAny(refreshTokenPermit);
    }

    @Override
    public boolean hasRole(String... role) {
        return hasAuthority(role);
    }

    @Override
    public boolean hasAuthority(String... authority) {
        if (authority == null) {
            authority = new String[]{superUser};
        } else {
            List<String> tmp = Lists.asList(superUser, authority);
            int size = tmp.size();
            authority = tmp.toArray(new String[size]);
        }
        return this.hasAny(authority);
    }

}

