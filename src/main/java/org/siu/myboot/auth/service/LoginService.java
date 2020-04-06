package org.siu.myboot.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.siu.myboot.auth.constant.Constant;
import org.siu.myboot.auth.handler.TokenProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.Resource;

/**
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

    public String login(String user, String pass, boolean remember) {
        // 认证，通过并返回权限
        Authentication authentication = authentication(user, pass);
        String jwt = tokenProvider.buildJWT(authentication, remember);
        log.info("认证通过，给用户[{}],颁发token[{}]", user, jwt);
        return Constant.Auth.TOKEN_PREFIX + jwt;
    }


    private Authentication authentication(String username, String password) throws BadCredentialsException {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }
}
