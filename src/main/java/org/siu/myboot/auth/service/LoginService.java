package org.siu.myboot.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.siu.myboot.auth.handler.TokenProvider;
import org.siu.myboot.auth.model.AuthUser;
import org.siu.myboot.auth.model.JsonWebToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.Resource;

/**
 * 登录服务
 *
 * @Author Siu
 * @Date 2020/4/3 22:14
 * @Version 0.0.1
 */
@Slf4j
public class LoginService {


    @Resource
    private TokenProvider tokenProvider;

    @Resource
    private AuthenticationManagerBuilder authenticationManagerBuilder;

    /**
     * 登录
     *
     * @param user 用户
     * @param pass 密码
     * @return JsonWebToken
     */
    public JsonWebToken login(String user, String pass) {
        return this.login(user, pass, false);
    }

    /**
     * 登录
     *
     * @param user     用户
     * @param pass     密码
     * @param remember 是否记住密码
     * @return JsonWebToken
     */
    public JsonWebToken login(String user, String pass, boolean remember) {
        // 认证，通过并返回权限
        Authentication authentication = authentication(user, pass);

        return tokenProvider.buildJsonWebToken(authentication, remember);

    }

    /**
     * 注销
     */
    public void logout() {
        tokenProvider.removeKey();
    }

    /**
     * 刷新token
     *
     * @return
     */
    public JsonWebToken refreshToken() {
        return tokenProvider.refresh();
    }


    /**
     * @param username 用户
     * @param password 密码
     * @return
     * @throws BadCredentialsException
     */
    private Authentication authentication(String username, String password) throws BadCredentialsException {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        // 将会会调用 AbstractAuthService.loadUserByUsername
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        Object o = authentication.getPrincipal();
        if (o instanceof AuthUser) {
            Object[] o1 = ((AuthUser) o).getV();
            if (o1 != null) {
                Object[] v = new Object[o1.length + 1];
                v[0] = password.hashCode();
                for (int i = 0; i < o1.length; i++) {
                    v[i + 1] = o1[i];
                }
                ((AuthUser) o).setV(v);
            }
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        tokenProvider.setKey();
        return authentication;
    }
}
