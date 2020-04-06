package org.siu.myboot.auth.config;

import org.siu.myboot.auth.handler.TokenAuthenticationFilter;
import org.siu.myboot.auth.handler.TokenProvider;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Set;

/**
 * BaseSecurityConfigurer 配置
 * 1、配置token认证拦截器
 *
 * @Author Siu
 * @Date 2020/3/4 15:21
 * @Version 0.0.1
 */
public class AkagiSecurityConfigurerAdapter extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    /**
     * token 提供器
     */
    private TokenProvider tokenProvider;
    /**
     * 忽略校验的路径pattern
     */
    private Set<String> ignorePathPattern;


    public AkagiSecurityConfigurerAdapter(TokenProvider tokenProvider, Set<String> ignorePathPattern) {
        this.tokenProvider = tokenProvider;
        this.ignorePathPattern = ignorePathPattern;
    }

    @Override
    public void configure(HttpSecurity http) {
        TokenAuthenticationFilter tokenAuthenticationFilter = new TokenAuthenticationFilter(tokenProvider,ignorePathPattern);
        // 把JWTFilter 放在默认Spring Security UsernamePasswordAuthenticationFilter 前面
        http.addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
