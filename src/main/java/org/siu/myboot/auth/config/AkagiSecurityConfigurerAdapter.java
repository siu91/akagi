package org.siu.myboot.auth.config;

import org.siu.myboot.auth.handler.TokenAuthenticationFilter;
import org.siu.myboot.auth.handler.TokenProvider;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * BaseSecurityConfigurer 配置
 * 1、配置token认证拦截器
 *
 * @Author Siu
 * @Date 2020/3/4 15:21
 * @Version 0.0.1
 */
public class AkagiSecurityConfigurerAdapter extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private TokenProvider tokenProvider;


    public AkagiSecurityConfigurerAdapter(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void configure(HttpSecurity http) {
        TokenAuthenticationFilter tokenAuthenticationFilter = new TokenAuthenticationFilter(tokenProvider);
        // 把JWTFilter 放在默认Spring Security UsernamePasswordAuthenticationFilter 前面
        http.addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
