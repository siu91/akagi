package org.siu.myboot.auth.service;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.PatternMatchUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;

/**
 * 权限校验服务
 *
 * @Author Siu
 * @Date 2020/3/9 11:00
 * @Version 0.0.1
 */
@Slf4j
public class PermitService {

    /**
     * 超级管理员拥有最高权限
     */
    private String superUser;

    private String refreshTokenPermit;


    public PermitService(String superUser, String refreshTokenPermit) {
        this.superUser = superUser;
        this.refreshTokenPermit = refreshTokenPermit;
    }

    /**
     * 校验当前用户是否有此权限
     *
     * @param perm 权限标识
     * @return
     */
    public boolean hasPermit(String perm) {
        if (!StringUtils.hasText(perm)) {
            return false;
        }
        // 获取用户权限
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        // 校验权限，可以在此自定义
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .filter(StringUtils::hasText)
                .anyMatch(x -> x.equals(superUser) || PatternMatchUtils.simpleMatch(perm, x));
    }

    /**
     * 是否有刷新token的权限
     *
     * @return
     */
    public boolean hasRefreshTokenPermit() {
        return this.hasPermit(this.refreshTokenPermit);
    }

}

