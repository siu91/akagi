package org.siu.myboot.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.siu.myboot.auth.constant.Constant;
import org.siu.myboot.auth.handler.TokenProvider;
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
     * @return
     */
    public String login(String user, String pass) {
        return this.login(user, pass, false);
    }

    /**
     * 登录
     *
     * @param user     用户
     * @param pass     密码
     * @param remember 是否记住密码
     * @return
     */
    public String login(String user, String pass, boolean remember) {
        // 认证，通过并返回权限
        Authentication authentication = authentication(user, pass);
        String jwt = tokenProvider.buildJWT(authentication, remember);
        log.info("认证通过，给用户[{}],颁发token[{}]", user, jwt);
        return Constant.Auth.TOKEN_PREFIX + jwt;
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
        // 将会会调用 org.siu.myboot.auth.service.AbstractAuthService.loadUserByUsername
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }
}
