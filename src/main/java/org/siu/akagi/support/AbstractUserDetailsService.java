package org.siu.akagi.support;

import lombok.extern.slf4j.Slf4j;
import org.siu.akagi.model.User;
import org.siu.akagi.model.UserDetails;
import org.siu.akagi.model.Authorities;
import org.siu.akagi.model.UserProperties;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 认证与授权服务
 * <p>
 * 1、实现了UserDetailsService.loadUserByUsername()
 * 2、抽象方法auth提供给子类实现认证与授权业务
 *
 * @Author Siu
 * @Date 2020/3/4 15:26
 * @Version 0.0.1
 */
@Slf4j
public abstract class AbstractUserDetailsService implements UserDetailsService {


    /**
     * 实现UserDetailsService.loadUserByUsername()
     *
     * @param userLoginId 用户登录的ID（用户、手机等）
     * @return
     */
    @Override
    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(final String userLoginId) {
        UserDetails userDetails = auth(userLoginId);
        return buildAuthUser(userDetails.getUser(), userDetails.getAuthorities());

    }


    /**
     * 由实现类去实现认证授权的业务
     *
     * @param userLoginId
     * @return
     */
    public abstract UserDetails auth(final String userLoginId);

    /**
     * 认证与授权对象拼装
     *
     * @param userProperties            登录认证后的用户信息
     * @param userAuthorities 用户的授权信息
     * @return 用户信息&权限信息
     */
    protected User buildAuthUser(UserProperties userProperties, List<Authorities> userAuthorities) {
        Set<String> tmp = new HashSet<>();
        for (Authorities authorities : userAuthorities) {
            if (authorities.getRole() != null) {
                tmp.add(authorities.getRole());
            }
            if (authorities.getPermit() != null) {
                tmp.add(authorities.getPermit());
            }
        }

        // 用户的权限信息
        List<GrantedAuthority> grantedAuthorities = new ArrayList<String>() {
            {
                addAll(tmp);
            }
        }.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

        return new User(userProperties.getId(), userProperties.getPass(), grantedAuthorities, userProperties.getV());
    }
}
