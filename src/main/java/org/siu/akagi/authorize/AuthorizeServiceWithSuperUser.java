package org.siu.akagi.authorize;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.siu.akagi.context.AkagiSecurityContextHolder;

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
     * 是否有刷新token的权限
     *
     * @return
     */
    @Override
    public boolean has() {
        return this.hasAny(AkagiSecurityContextHolder.getAkagiGlobalProperties().getJsonWebTokenRefreshPermit());
    }

    @Override
    public boolean hasRole(String... role) {
        return hasAuthority(role);
    }

    @Override
    public boolean hasAuthority(String... authority) {
        if (authority == null) {
            authority = new String[]{AkagiSecurityContextHolder.getAkagiGlobalProperties().getSuperUser()};
        } else {
            List<String> tmp = Lists.asList(AkagiSecurityContextHolder.getAkagiGlobalProperties().getSuperUser(), authority);
            int size = tmp.size();
            authority = tmp.toArray(new String[size]);
        }
        return this.hasAny(authority);
    }

}

