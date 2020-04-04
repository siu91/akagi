package org.siu.myboot.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.siu.myboot.auth.model.Auth;
import org.siu.myboot.auth.model.AuthUser;
import org.siu.myboot.auth.model.LoginUser;
import org.siu.myboot.auth.model.UserAuthorities;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author Siu
 * @Date 2020/3/4 15:26
 * @Version 0.0.1
 */
@Slf4j
public abstract class AbstractAuthService implements UserDetailsService {


    /**
     * @param userLoginId 用户登录的ID（用户、手机等）
     * @return
     */
    @Override
    public UserDetails loadUserByUsername(final String userLoginId) {
        Auth auth = auth(userLoginId);
        return buildAuthUser(auth.getUser(), auth.getAuthorities());

    }


    /**
     * 由实现类去实现认证授权的业务
     *
     * @param userLoginId
     * @return
     */
    public abstract Auth auth(final String userLoginId);


    /**
     * @param user            登录认证后的用户信息
     * @param userAuthorities 用户的授权信息
     * @return
     */
    protected AuthUser buildAuthUser(LoginUser user, List<UserAuthorities> userAuthorities) {
        Set<String> tmp = new HashSet<>();
        for (UserAuthorities authorities : userAuthorities) {
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

        return new AuthUser(user.getId(), user.getPass(), grantedAuthorities, user.getTokenVersion());
    }
}
